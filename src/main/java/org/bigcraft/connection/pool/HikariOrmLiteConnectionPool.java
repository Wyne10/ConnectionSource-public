package org.bigcraft.connection.pool;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;
import org.bigcraft.connection.api.ConnectionPool;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public class HikariOrmLiteConnectionPool implements ConnectionPool<ConnectionSource> {

    private final String url;
    private final String username;
    private final String password;

    private final HikariDataSource dataSource = new HikariDataSource();
    private ConnectionSource connectionSource;

    public HikariOrmLiteConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password) throws SQLException {
        this(url, username, password, source -> {});
    }

    public HikariOrmLiteConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password,
                                       @NotNull Consumer<@NotNull HikariDataSource> configurator) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        initializeDataSource(configurator);
    }

    public void initializeDataSource() throws SQLException {
        initializeDataSource(source -> {});
    }

    /**
     * Building the connection source starts the Hikari pool, which seals its configuration,
     * so the constructor configurator is the only usable hook for this implementation.
     */
    public void initializeDataSource(@NotNull Consumer<@NotNull HikariDataSource> configurator) throws SQLException {
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        configurator.accept(dataSource);
        connectionSource = new DataSourceConnectionSource(dataSource, url);
    }

    @Override
    public boolean isActive() {
        return dataSource.isRunning();
    }

    @Override
    public @NotNull Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public @NotNull ConnectionSource getSource() {
        return connectionSource;
    }

    @Override
    public void close() throws Exception {
        connectionSource.close();
        dataSource.close();
    }

}
