package org.bigcraft.connection.pool;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.bigcraft.connection.api.ConnectionPool;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public class OrmLiteConnectionPool implements ConnectionPool<ConnectionSource> {

    private final String url;
    private final String username;
    private final String password;

    private final JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource();
    private boolean isInitialized = false;

    public OrmLiteConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password) throws SQLException {
        this(url, username, password, source -> {});
    }

    public OrmLiteConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password,
                                 @NotNull Consumer<@NotNull JdbcPooledConnectionSource> configurator) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        initializeDataSource(configurator);
    }

    public void initializeDataSource() throws SQLException {
        initializeDataSource(source -> {});
    }

    public void initializeDataSource(@NotNull Consumer<@NotNull JdbcPooledConnectionSource> configurator) throws SQLException {
        connectionSource.setUrl(url);
        connectionSource.setUsername(username);
        connectionSource.setPassword(password);
        configurator.accept(connectionSource);
        connectionSource.initialize();
        isInitialized = true;
    }

    @Override
    public boolean isActive() {
        return isInitialized;
    }

    /**
     * Always throws, ORMLite manages connections internally, use {@link #getSource()} instead.
     */
    @Override
    public @NotNull Connection getConnection() {
        throw new UnsupportedOperationException("OrmLiteConnectionPool doesn't provide java.sql connections");
    }

    @Override
    public @NotNull ConnectionSource getSource() {
        return connectionSource;
    }

    @Override
    public void close() throws Exception {
        try {
            connectionSource.close();
        } finally {
            isInitialized = false;
        }
    }

}
