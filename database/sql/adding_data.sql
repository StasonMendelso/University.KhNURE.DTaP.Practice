USE warehouse;

INSERT INTO role(role)
VALUES ('ADMIN'),
       ('USER');

INSERT INTO units(unit)
VALUES('kg'),
      ('apiece'),
      ('packaging'),
      ('square meter');

INSERT INTO items(vendor, name, unit_id, weight, amount, reserve_rate)
VALUES('038-123-53-365-234','Cookies with cream',1,1,12,30),
      ('038-153-365-234','Nails',3,0.350,20,50),
      ('123-365435-2674','Black Tile',4,2.1,80,120),
      ('9785-75-623-36','White Tile',4,1.6,45,120),
      ('96-3248-387-907','3-meter aluminium pipe',2,3.300,23,50);

INSERT INTO companies(name,email,address)
VALUES ('Delicious cookies', 'cookie_company@gmail.com', 'Ukraine, Kyiv region, Kyiv, St. Hrushevsky, №55, flat №30'),
       ('Hot iron', 'metal_company@gmail.com', 'Ukraine, Dnipro region, Dnipro, St. Budivelna, №12'),
       ('InterCerama', 'intercerama_company@gmail.com', 'Ukraine, Lviv region, Lviv, St. Hlynyana, №20');

INSERT INTO income_journal(document_number, items_id,companies_id, date, price, amount)
VALUES('in-345-645-75634',1,1,'2022-12-13 10:36:12', 130.25, 12),
      ('in-345-885-99234',2,2,'2022-12-13 10:30:56', 230, 20),
      ('in-685-115-58634',3,3,'2022-12-13 9:30:12', 400, 80),
      ('in-570-388-12534',4,3,'2022-12-13 9:30:12', 560, 45),
      ('in-9990-624-74831',5,2,'2022-12-13 10:31:00', 670, 23);

SELECT * FROM units;
SELECT * FROM items;
SELECT * FROM companies;
SELECT * FROM income_journal;
INSERT INTO income_journal(document_number, items_id,companies_id, date, price, amount)
VALUES('in-345-645-75987',1,1,'2022-12-13 11:36:34', 107.50, 12);
UPDATE items SET amount = amount + 12 WHERE id = 1;
INSERT INTO companies(name,email,address)
VALUES ('ATB', 'atb_company@gmail.com', 'Ukraine, Kharkiv region, Kharkiv, St. Naykova, №35');

INSERT INTO outcome_journal(document_number, items_id,companies_id, date, price, amount)
VALUES('out-598-867-75237',1,4,'2022-12-13 12:30:56', 150.50, 20);

UPDATE items SET amount = amount - 20 WHERE id = 1;
INSERT INTO income_journal(document_number, items_id,companies_id, date, price, amount)
VALUES('in-345-645-95687',2,2,'2022-12-13 14:30:32', 220, 15),
      ('in-978-865-74367',5,2,'2022-12-13 14:30:32', 670, 10);

UPDATE items SET amount = amount + 15 WHERE id = 2;
UPDATE items SET amount = amount + 10 WHERE id = 5;
INSERT INTO companies(name,email,address)
VALUES ('BudMaster', 'budMaster_company@gmail.com', 'Ukraine, Poltava region, Poltava, St. Sobornosty, №10'),
       ('Epicentr', 'epicentr_company@gmail.com', 'Ukraine, Poltava region, Poltava, St. Shevchenka, №56');

INSERT INTO outcome_journal(document_number, items_id,companies_id, date, price, amount)
VALUES('out-248-967-75235',2,5,'2022-12-13 16:30:45', 260, 10),
      ('out-598-8536-75237',2,6,'2022-12-13 18:15:34', 250, 15),
      ('out-524-127-07867',5,6,'2022-12-13 18:15:34', 750, 25);

UPDATE items SET amount = amount - 10 WHERE id = 2;
UPDATE items SET amount = amount - 15 WHERE id = 2;
UPDATE items SET amount = amount - 25 WHERE id = 5;
