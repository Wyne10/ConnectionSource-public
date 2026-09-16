package me.wyne.connection.module;

import com.google.inject.AbstractModule;
import me.wyne.connection.ConnectionSourceApi;

public class ApiModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ConnectionSourceApi.class);
    }
}
