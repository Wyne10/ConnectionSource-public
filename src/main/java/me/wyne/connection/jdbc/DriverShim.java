package me.wyne.connection.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverShim implements Driver {

    private final Driver driver;

    public DriverShim(@NotNull Driver driver) {
        this.driver = driver;
    }

    @Override
    public @Nullable Connection connect(@NotNull String url, @Nullable Properties info) throws SQLException {
        return driver.connect(url, info);
    }

    @Override
    public boolean acceptsURL(@NotNull String url) throws SQLException {
        return driver.acceptsURL(url);
    }

    @Override
    public @NotNull DriverPropertyInfo[] getPropertyInfo(@NotNull String url, @Nullable Properties info) throws SQLException {
        return driver.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion() {
        return driver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return driver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return driver.jdbcCompliant();
    }

    @Override
    public @NotNull Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return driver.getParentLogger();
    }

}
