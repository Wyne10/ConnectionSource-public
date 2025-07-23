package org.bigcraft.connection.module;

import com.google.inject.AbstractModule;
import org.bigcraft.connection.listener.PlayerListener;

public class ListenerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PlayerListener.class);
    }
}
