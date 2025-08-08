package org.bigcraft.connection.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bigcraft.connection.ConnectionSource;
import org.bigcraft.connection.ConnectionProvider;

@Singleton
public class ConnectionCommand {

    private final ConnectionSource plugin;
    private final ConnectionProvider connectionProvider;

    @Inject
    public ConnectionCommand(ConnectionSource plugin, ConnectionProvider connectionProvider) {
        this.plugin = plugin;
        this.connectionProvider = connectionProvider;
        registerCommand();
    }

    private void registerCommand() {
        new CommandTree("connection")
                .then(new LiteralArgument("reload")
                        .withPermission("connection.reload")
                        .executes((sender, args) -> {
                            if (connectionProvider.isActive()) {
                                sender.sendMessage("Connection source is already active.");
                                sender.sendMessage("Are you sure you want to reload the connection source? It may cause data loss in dependant plugins.");
                                sender.sendMessage("To confirm, type /connection reload confirm");
                            } else {
                                plugin.reload();
                                sender.sendMessage("Reloaded connection source");
                                plugin.getLog().info("Reloaded connection source");
                            }
                        })
                        .then(new LiteralArgument("confirm")
                                .withPermission("connection.reload")
                                .executes((sender, args) -> {
                                    plugin.reload();
                                    sender.sendMessage("Reloaded connection source");
                                    plugin.getLog().info("Reloaded connection source");
                                })))
                .register(plugin);
    }

}
