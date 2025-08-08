package org.bigcraft.connection.module;

import com.google.inject.AbstractModule;
import org.bigcraft.connection.api.ConnectionProvider;
import org.bigcraft.connection.api.SqlConfig;

public class ConnectionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SqlConfig.class).to(org.bigcraft.connection.config.SqlConfig.class);
        bind(ConnectionProvider.class).to(org.bigcraft.connection.ConnectionProvider.class);
    }
}
