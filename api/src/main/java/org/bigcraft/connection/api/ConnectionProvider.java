package org.bigcraft.connection.api;

import com.j256.ormlite.support.ConnectionSource;
import me.wyne.wutils.jdbc.ConnectionPool;

public interface ConnectionProvider extends AutoCloseable {

    ConnectionPool<ConnectionSource> getConnectionPool();
    SqlConfig getConfig();
    void reloadConnectionPool();
    boolean isActive();

}
