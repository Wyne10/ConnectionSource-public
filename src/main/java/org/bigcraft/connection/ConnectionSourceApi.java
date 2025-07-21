package org.bigcraft.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bigcraft.connection.api.CSApi;
import org.bigcraft.connection.api.ConnectionProvider;
import org.bukkit.plugin.ServicePriority;

@Singleton
public class ConnectionSourceApi {

    @Inject
    public ConnectionSourceApi(ConnectionSource plugin, ConnectionProvider connectionProvider) {
        plugin.getServer().getServicesManager().register(ConnectionProvider.class, connectionProvider, plugin, ServicePriority.Normal);
        CSApi.setProvider(connectionProvider);
    }

}
