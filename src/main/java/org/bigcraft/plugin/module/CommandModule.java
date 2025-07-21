package org.bigcraft.plugin.module;

import com.google.inject.AbstractModule;
import org.bigcraft.plugin.ConnectionSource;
import org.bigcraft.plugin.command.ConnectionCommand;

public class CommandModule extends AbstractModule {
    @Override
    protected void configure() {
        try {
            Class.forName("dev.jorel.commandapi.CommandAPI");
            bind(ConnectionCommand.class);
        } catch (ClassNotFoundException e) {
            ConnectionSource.getInstance().getLog().warn("CommandAPI not found, commands are not registered");
        }
    }
}
