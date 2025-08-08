package org.bigcraft.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bigcraft.connection.api.ConnectionPool;
import org.bigcraft.connection.config.SqlConfig;
import org.bigcraft.connection.pool.HikariOrmLiteConnectionPool;

@Singleton
@Getter
public class ConnectionProvider implements org.bigcraft.connection.api.ConnectionProvider {

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
        this.connectionPool = new HikariOrmLiteConnectionPool(config.getJdbcUrl(), config.getUsername(), config.getPassword(), plugin.getLog());
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
