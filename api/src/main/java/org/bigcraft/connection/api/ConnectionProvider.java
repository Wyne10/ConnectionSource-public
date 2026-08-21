package org.bigcraft.connection.api;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Owns the plugin's SQL {@link ConnectionPool}, backed by an ORMLite {@link ConnectionSource}.
 * <p>
 * Closing this provider via {@link #close()} closes the underlying pool; do not
 * close the pool returned by {@link #getConnectionPool()} independently.
 */
public interface ConnectionProvider extends AutoCloseable {

    /**
     * @return the current connection pool, or {@code null} if the pool has not been established
     * (e.g. {@link SqlConfig#isConfigured()} is {@code false} or pool creation failed)
     */
    ConnectionPool<ConnectionSource> getConnectionPool();
    SqlConfig getConfig();

    /**
     * Closes the current connection pool, if any, and opens a new one from the current
     * {@link #getConfig()} values. Does nothing but log a warning if the config is not
     * {@link SqlConfig#isConfigured() configured}.
     */
    void reloadConnectionPool();

    /**
     * @return whether a connection pool exists and is currently active
     */
    boolean isActive();

}
