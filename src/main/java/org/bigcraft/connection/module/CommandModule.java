package org.bigcraft.connection.module;

import com.google.inject.AbstractModule;
import org.bigcraft.connection.ConnectionSource;
import org.bigcraft.connection.command.ConnectionCommand;

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
