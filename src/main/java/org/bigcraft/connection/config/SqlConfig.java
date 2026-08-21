package org.bigcraft.connection.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import me.wyne.wutils.config.Config;
import me.wyne.wutils.config.ConfigEntry;
import org.bigcraft.connection.ConnectionSource;
import org.bigcraft.connection.jdbc.DriverLibrary;

@SuppressWarnings("FieldMayBeFinal")
@Singleton
@Getter
public class SqlConfig implements org.bigcraft.connection.api.SqlConfig {

    @ConfigEntry(section = "SQL", comment = "Load and register JDBC driver, options: NONE, H2_V1, H2_V2, MYSQL, MARIADB, POSTGRESQL, SQLITE")
    private String driver = "NONE";

    @ConfigEntry(section = "SQL")
    private String jdbcUrl = "jdbc:mysql://localhost:3306/test";

    @ConfigEntry(section = "SQL")
    private String username = "", password = "";

    private final ConnectionSource plugin;

    @Inject
    public SqlConfig(ConnectionSource plugin) {
        this.plugin = plugin;
        Config.global.registerConfigObject(this);
    }

    @Override
    public boolean isConfigured()
    {
        return (jdbcUrl != null && username != null && password != null)
                && (!jdbcUrl.isEmpty() && !username.isEmpty());
    }

    @Override
    public void registerDriver() {
        DriverLibrary driverLibrary;
        try {
            driverLibrary = DriverLibrary.valueOf(driver);
        } catch (IllegalArgumentException e) {
            plugin.getLog().error("Unknown JDBC driver '{}'", driver);
            return;
        }

        try {
            driverLibrary.registerDriver();
            plugin.getLog().info("Registered '{}' JDBC driver", driver);
        } catch (Exception e) {
            plugin.getLog().error("An exception occurred trying to register '{}' JDBC driver", driver, e);
        }
    }

}
