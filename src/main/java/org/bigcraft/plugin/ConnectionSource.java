package org.bigcraft.plugin;

import com.google.inject.*;
import lombok.Getter;
import me.wyne.wutils.config.Config;
import me.wyne.wutils.log.JulLevel;
import me.wyne.wutils.log.Level;
import me.wyne.wutils.log.Log;
import me.wyne.wutils.log.Log4jFactory;
import org.bigcraft.plugin.module.CommandModule;
import org.bigcraft.plugin.module.PluginModule;
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
                    new CommandModule()
            );
        } catch (CreationException e) {
            log.error("Guice injector creation exception", e);
        }

        initializeConfig();

        try {
            // TODO Start model
        } catch (ConfigurationException | ProvisionException e) {
            log.error("Guice configuration/provision exception", e);
        }
    }

    @Override
    public void onDisable() {
        // TODO Close connection
    }

    private void initializeLogger()
    {
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
            // TODO Reload model
        } catch (ConfigurationException | ProvisionException e) {
            log.error("Guice configuration/provision exception", e);
        }
    }

}
