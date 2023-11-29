CREATE SCHEMA IF NOT EXISTS `warehouse` DEFAULT CHARACTER SET utf8mb4;
USE `warehouse` ;

CREATE TABLE IF NOT EXISTS `warehouse`.`companies`
(
    `id`      INT          NOT NULL AUTO_INCREMENT,
    `name`    VARCHAR(200) NOT NULL,
    `email`   VARCHAR(100) NOT NULL,
    `address` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
    UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
    UNIQUE INDEX `address_UNIQUE` (`address` ASC) VISIBLE
);

CREATE TABLE IF NOT EXISTS `warehouse`.`units`
(
    `id`   INT         NOT NULL AUTO_INCREMENT,
    `unit` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `unit_UNIQUE` (`unit` ASC) VISIBLE
);

CREATE TABLE IF NOT EXISTS `warehouse`.`items`
(
    `id`           INT            NOT NULL AUTO_INCREMENT,
    `vendor`       VARCHAR(20)    NOT NULL,
    `name`         VARCHAR(100)   NOT NULL,
    `unit_id`      INT            NOT NULL,
    `weight`       DECIMAL(65, 4) NOT NULL,
    `amount`       INT            NULL DEFAULT NULL,
    `reserve_rate` INT            NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `vendor_UNIQUE` (`vendor` ASC) VISIBLE,
    INDEX `unit_id` (`unit_id` ASC) VISIBLE,
    CONSTRAINT `items_ibfk_1`
        FOREIGN KEY (`unit_id`)
            REFERENCES `warehouse`.`units` (`id`)
);

CREATE TABLE IF NOT EXISTS `warehouse`.`income_journal`
(
    `id`              INT            NOT NULL AUTO_INCREMENT,
    `document_number` VARCHAR(45)    NOT NULL,
    `items_id`        INT            NOT NULL,
    `companies_id`    INT            NOT NULL,
    `date`            DATETIME       NOT NULL,
    `price`           DECIMAL(65, 4) NOT NULL,
    `amount`          INT            NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `document_number_UNIQUE` (`document_number` ASC) VISIBLE,
    INDEX `fk_income_journal_items1_idx` (`items_id` ASC) VISIBLE,
    INDEX `fk_income_journal_companies1_idx` (`companies_id` ASC) VISIBLE,
    CONSTRAINT `fk_income_journal_companies1`
        FOREIGN KEY (`companies_id`)
            REFERENCES `warehouse`.`companies` (`id`),
    CONSTRAINT `fk_income_journal_items1`
        FOREIGN KEY (`items_id`)
            REFERENCES `warehouse`.`items` (`id`)
);

CREATE TABLE IF NOT EXISTS `warehouse`.`outcome_journal`
(
    `id`              INT            NOT NULL AUTO_INCREMENT,
    `document_number` VARCHAR(45)    NOT NULL,
    `items_id`        INT            NOT NULL,
    `companies_id`    INT            NOT NULL,
    `date`            DATETIME       NOT NULL,
    `price`           DECIMAL(65, 4) NOT NULL,
    `amount`          INT            NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `document_number_UNIQUE` (`document_number` ASC) VISIBLE,
    INDEX `fk_income_journal_items1_idx` (`items_id` ASC) VISIBLE,
    INDEX `fk_income_journal_companies1_idx` (`companies_id` ASC) VISIBLE,
    CONSTRAINT `fk_income_journal_companies10`
        FOREIGN KEY (`companies_id`)
            REFERENCES `warehouse`.`companies` (`id`),
    CONSTRAINT `fk_income_journal_items10`
        FOREIGN KEY (`items_id`)
            REFERENCES `warehouse`.`items` (`id`)
);
CREATE TABLE IF NOT EXISTS `warehouse`.`role`
(
    `id`   INT          NOT NULL AUTO_INCREMENT,
    `role` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `username_UNIQUE` (`role` ASC) VISIBLE
);


CREATE TABLE IF NOT EXISTS `warehousecpp`.`users`
(
    `id`       INT          NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(100) NOT NULL,
    `password` VARCHAR(256) NOT NULL,
    `role_id`  INT          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
    CONSTRAINT `fk_role_id`
        FOREIGN KEY (`role_id`)
            REFERENCES `warehouse`.`role` (`id`)
);


DELIMITER //
CREATE PROCEDURE get_all_items_by_name(IN name VARCHAR(200))
BEGIN
    SELECT items.id, vendor, items.name, unit, weight, amount, reserve_rate
    FROM items
             JOIN units ON items.unit_id = units.id
    WHERE items.name LIKE CONCAT('%', name, '%');
END //
DELIMITER ;