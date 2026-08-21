package org.bigcraft.connection.api;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool<T> extends AutoCloseable {

    boolean isActive();

    /**
     * Not every implementation supports this, see the implementing class.
     */
    @NotNull Connection getConnection() throws SQLException;

    @NotNull T getSource();

}
