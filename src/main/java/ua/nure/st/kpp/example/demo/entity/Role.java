package ua.nure.st.kpp.example.demo.entity;

/**
 * @author Stanislav Hlova
 */
public enum Role {
    ADMIN, USER;

    public static Role getInstance(String roleStr) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(roleStr)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Can't find Role for passed value: " + roleStr);
    }
}
