package org.bigcraft.connection.api;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wraps a pooled SQL data source, exposing both a raw {@link Connection} and the
 * pool's native source object of type {@code T} (e.g. a {@code HikariDataSource}
 * or an ORMLite {@code ConnectionSource}).
 * <p>
 * Implementations own the underlying pool: closing this instance via
 * {@link #close()} shuts the pool down and invalidates any connections or source
 * objects previously handed out. A {@link Connection} returned by
 * {@link #getConnection()} is owned by the caller and must be closed by the
 * caller to return it to the pool.
 *
 * @param <T> the type of the underlying native connection source
 */
public interface ConnectionPool<T> extends AutoCloseable {

    /**
     * @return whether the underlying data source is currently running
     */
    boolean isActive();

    /**
     * Not every implementation supports this, see the implementing class.
     *
     * @return a new pooled JDBC connection; the caller is responsible for closing it
     * @throws SQLException if a connection could not be obtained
     * @throws UnsupportedOperationException if this implementation does not expose raw JDBC connections
     */
    @NotNull Connection getConnection() throws SQLException;

    /**
     * @return the underlying native connection source backing this pool
     */
    @NotNull T getSource();

}
