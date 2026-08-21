package org.bigcraft.connection;

import com.google.inject.*;
import com.j256.ormlite.logger.Logger;
import lombok.Getter;
import me.wyne.wutils.config.Config;
import me.wyne.wutils.log.JulLevel;
import me.wyne.wutils.log.Level;
import me.wyne.wutils.log.Log;
import me.wyne.wutils.log.Log4jFactory;
import org.bigcraft.connection.config.SqlConfig;
import org.bigcraft.connection.module.ApiModule;
import org.bigcraft.connection.module.CommandModule;
import org.bigcraft.connection.module.ConnectionModule;
import org.bigcraft.connection.module.PluginModule;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.Executors;

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
        Log.global = Log.builder()
                .setLogger(getLogger())
                .setLevel(JulLevel.valueOf(getConfig().getString("logLevel", "INFO")).getLevel())
                .setLogDirectory(new File(getDataFolder(), "log"))
                .setFileWriteExecutor(Executors.newSingleThreadExecutor())
                .build();
        Log.global.deleteOlderLogs();

        log = Log4jFactory.createLogger(
                this,
                Log4jFactory.DEFAULT_FILE_MESSAGE_PATTERN,
                Level.valueOf(getConfig().getString("logLevel", "INFO")),
                new File(getDataFolder(), "log").getPath(),
                Log.global
        );
    }

    private void initializeConfig()
    {
        Config.global.log = log;
        Config.global.setConfigGenerator(this, "config.yml");
        Config.global.generateConfig();
        reloadConfig();
        getConfig().setDefaults(new MemoryConfiguration());
        Config.global.reloadConfig(getConfig());
    }

    public void reload()
    {
        reloadConfig();
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
