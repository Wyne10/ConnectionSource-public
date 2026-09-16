package me.wyne.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.wyne.connection.api.CSApi;
import me.wyne.connection.api.ConnectionProvider;
import org.bukkit.plugin.ServicePriority;

@Singleton
public class ConnectionSourceApi {

    @Inject
    public ConnectionSourceApi(ConnectionSource plugin, ConnectionProvider connectionProvider) {
        plugin.getServer().getServicesManager().register(ConnectionProvider.class, connectionProvider, plugin, ServicePriority.Normal);
        CSApi.setProvider(connectionProvider);
    }

}
