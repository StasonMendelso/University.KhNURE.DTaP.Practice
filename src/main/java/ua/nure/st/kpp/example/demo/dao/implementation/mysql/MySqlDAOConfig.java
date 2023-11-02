package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "database")
public class MySqlDAOConfig {
    private String type;
    private String url;
    private String user;
    private String password;
    private int poolSize;
    private int maxPoolSize;

    public MySqlDAOConfig() {
    }

    public MySqlDAOConfig(String type) {
        this.type = type;
    }

    public MySqlDAOConfig(String type, String url, String user, String password) {
        this.type = type;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    @Override
    public String toString() {
        return "MySqlDAOConfig{" +
               "type='" + type + '\'' +
               ", url='" + url + '\'' +
               ", user='" + user + '\'' +
               ", password='" + password + '\'' +
               ", poolSize=" + poolSize +
               ", maxPoolSize=" + maxPoolSize +
               '}';
    }
}
