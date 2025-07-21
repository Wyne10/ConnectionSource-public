package org.bigcraft.connection.module;

import com.google.inject.AbstractModule;
import org.bigcraft.connection.ConnectionSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginModule extends AbstractModule {

    private final ConnectionSource plugin;

    public PluginModule(ConnectionSource plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(ConnectionSource.class)
                .toInstance(plugin);
        bind(JavaPlugin.class)
                .toInstance(plugin);
        bind(FileConfiguration.class)
                .toInstance(plugin.getConfig());
    }

}
