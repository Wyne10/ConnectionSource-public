package me.wyne.connection.module;

import com.google.inject.AbstractModule;
import me.wyne.connection.ConnectionSource;
import me.wyne.connection.command.ConnectionCommand;

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
