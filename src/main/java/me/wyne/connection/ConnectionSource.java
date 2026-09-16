package me.wyne.connection;

import com.google.inject.*;
import com.j256.ormlite.logger.Logger;
import lombok.Getter;
import me.wyne.wutils.common.plugin.LevelWrapper;
import me.wyne.wutils.common.plugin.LoggerWrapper;
import me.wyne.wutils.config.Config;
import me.wyne.connection.config.SqlConfig;
import me.wyne.connection.module.ApiModule;
import me.wyne.connection.module.CommandModule;
import me.wyne.connection.module.ConnectionModule;
import me.wyne.connection.module.PluginModule;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
public class ConnectionSource extends JavaPlugin {

    @Getter private static ConnectionSource instance;
    @Getter private org.slf4j.Logger log;

    private Injector injector;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getConfig().setDefaults(new MemoryConfiguration());

        initializeLogger();

        try {
            injector =  Guice.createInjector(
                    Stage.PRODUCTION,
                    new PluginModule(this),
                    new CommandModule(),
                    new ConnectionModule(),
                    new ApiModule()
            );
        } catch (CreationException e) {
            log.error("Guice injector creation exception", e);
        }

        initializeConfig();

        try {
            injector.getInstance(SqlConfig.class).registerDriver();
            injector.getInstance(ConnectionProvider.class).reloadConnectionPool();
        } catch (ConfigurationException | ProvisionException e) {
            log.error("Guice configuration/provision exception", e);
        }
    }

    @Override
    public void onDisable() {
        injector.getInstance(ConnectionProvider.class).close();
    }

    private void initializeLogger()
    {
        Logger.setGlobalLogLevel(com.j256.ormlite.logger.Level.INFO);
        log = new LoggerWrapper(getSLF4JLogger(), LevelWrapper.valueOf(getConfig().getString("logLevel", "INFO")));
        Config.global.logger = log;
    }

    private void initializeConfig()
    {
        Config.global.setConfigGenerator(this, "config.yml");
        Config.global.generateConfig();
        reloadConfig();
        getConfig().setDefaults(new MemoryConfiguration());
        Config.global.reloadConfig(getConfig());
    }

    public void reload()
    {
        reloadConfig();
        initializeLogger();
        getConfig().setDefaults(new MemoryConfiguration());
        Config.global.reloadConfig(getConfig());
        try {
            injector.getInstance(SqlConfig.class).registerDriver();
            injector.getInstance(ConnectionProvider.class).reloadConnectionPool();
        } catch (ConfigurationException | ProvisionException e) {
            log.error("Guice configuration/provision exception", e);
        }
    }

}
