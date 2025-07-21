package org.bigcraft.plugin.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bigcraft.plugin.ConnectionSource;

@Singleton
public class ConnectionCommand {

    private final ConnectionSource plugin;

    @Inject
    public ConnectionCommand(ConnectionSource plugin) {
        this.plugin = plugin;
        registerCommand();
    }

    private void registerCommand() {
        new CommandTree("connection")
                .then(new LiteralArgument("reload")
                        .withPermission("connection.reload")
                        .executes((sender, args) -> {
                            plugin.reload();
                        }))
                .register(plugin);
    }

}
