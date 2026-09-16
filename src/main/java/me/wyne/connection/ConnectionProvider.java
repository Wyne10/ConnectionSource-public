package me.wyne.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.SneakyThrows;
import me.wyne.connection.api.ConnectionPool;
import me.wyne.connection.config.SqlConfig;
import me.wyne.connection.pool.HikariOrmLiteConnectionPool;

import java.sql.SQLException;

@Singleton
@Getter
public class ConnectionProvider implements me.wyne.connection.api.ConnectionProvider {

    private ConnectionPool<com.j256.ormlite.support.ConnectionSource> connectionPool;

    private final ConnectionSource plugin;
    private final SqlConfig config;

    @Inject
    public ConnectionProvider(ConnectionSource plugin, SqlConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void reloadConnectionPool() {
        if (!config.isConfigured()) {
            plugin.getLog().warn("SQL connection is not configured");
            return;
        }
        if (connectionPool != null)
            close();
        try {
            this.connectionPool = new HikariOrmLiteConnectionPool(config.getJdbcUrl(), config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            this.connectionPool = null;
            plugin.getLog().error("An exception occurred trying to establish data source connection with {}", config.getJdbcUrl(), e);
        }
    }

    @Override
    public boolean isActive() {
        return connectionPool != null && connectionPool.isActive();
    }

    @SneakyThrows
    @Override
    public void close() {
        if (connectionPool != null)
            connectionPool.close();
    }

}
