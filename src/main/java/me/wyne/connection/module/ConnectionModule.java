package me.wyne.connection.module;

import com.google.inject.AbstractModule;
import me.wyne.connection.api.ConnectionProvider;
import me.wyne.connection.api.SqlConfig;

public class ConnectionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SqlConfig.class).to(me.wyne.connection.config.SqlConfig.class);
        bind(ConnectionProvider.class).to(me.wyne.connection.ConnectionProvider.class);
    }
}
