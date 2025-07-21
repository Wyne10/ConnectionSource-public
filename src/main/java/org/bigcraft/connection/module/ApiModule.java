package org.bigcraft.connection.module;

import com.google.inject.AbstractModule;
import org.bigcraft.connection.ConnectionSourceApi;

public class ApiModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ConnectionSourceApi.class);
    }
}
