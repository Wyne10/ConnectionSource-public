package org.bigcraft.connection.pool;

import com.zaxxer.hikari.HikariDataSource;
import org.bigcraft.connection.api.ConnectionPool;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public class HikariConnectionPool implements ConnectionPool<HikariDataSource> {

    private final String url;
    private final String username;
    private final String password;

    private final HikariDataSource dataSource = new HikariDataSource();

    public HikariConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password) {
        this(url, username, password, source -> {});
    }

    public HikariConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password,
                                @NotNull Consumer<@NotNull HikariDataSource> configurator) {
        this.url = url;
        this.username = username;
        this.password = password;
        initializeDataSource(configurator);
    }

    public void initializeDataSource() {
        initializeDataSource(source -> {});
    }

    /**
     * HikariCP seals a data source configuration once the pool has started, so calling this
     * after the first connection has been handed out throws {@link IllegalStateException}.
     */
    public void initializeDataSource(@NotNull Consumer<@NotNull HikariDataSource> configurator) {
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        configurator.accept(dataSource);
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
    public @NotNull HikariDataSource getSource() {
        return dataSource;
    }

    @Override
    public void close() {
        dataSource.close();
    }

}
