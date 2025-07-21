package org.bigcraft.connection.api;

public interface SqlConfig {

    String getDriver();
    String getJdbcUrl();
    String getUsername();
    String getPassword();
    boolean isConfigured();
    void registerDriver();

}
