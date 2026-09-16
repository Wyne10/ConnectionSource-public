package me.wyne.connection.api;

/**
 * Exposes the plugin's configured JDBC connection settings.
 */
public interface SqlConfig {

    /**
     * @return the configured JDBC driver library name (e.g. {@code MYSQL}, {@code SQLITE}, {@code NONE})
     */
    String getDriver();
    String getJdbcUrl();
    String getUsername();
    String getPassword();

    /**
     * @return whether a JDBC URL and username are both set, making {@link #getJdbcUrl()} usable to open connections
     */
    boolean isConfigured();

    /**
     * Loads and registers the JDBC driver named by {@link #getDriver()}. Logs and returns without throwing
     * if the driver name is unrecognized or registration fails.
     */
    void registerDriver();

}
