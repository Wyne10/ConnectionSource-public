package org.bigcraft.connection.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import lombok.SneakyThrows;
import org.bigcraft.connection.ConnectionSource;
import org.bigcraft.connection.api.ConnectionProvider;
import org.bigcraft.connection.api.entity.PlayerEntity;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public class PlayerListener implements Listener, AutoCloseable {

    private final Set<UUID> playerCache = new HashSet<>();
    private Dao<PlayerEntity, UUID> playerDao;

    private final ConnectionProvider connectionProvider;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public PlayerListener(ConnectionSource plugin, ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @SneakyThrows
    public void load() {
        playerCache.clear();
        playerDao = null;
        if (connectionProvider.isActive()) {
            playerDao = DaoManager.createDao(connectionProvider.getConnectionPool().getSource(), PlayerEntity.class);
            TableUtils.createTableIfNotExists(connectionProvider.getConnectionPool().getSource(), PlayerEntity.class);
            loadPlayerCache();
        }
    }

    @SneakyThrows
    private void loadPlayerCache() {
        playerDao.queryForAll()
                .forEach(player -> playerCache.add(player.getUuid()));
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (playerDao == null)
            return;
        if (playerCache.contains(event.getPlayer().getUniqueId()))
            return;
        playerCache.add(event.getPlayer().getUniqueId());
        executor.execute(() -> {
            try {
                if (playerDao != null)
                    playerDao.create(new PlayerEntity(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
            } catch (Exception e) {
                ConnectionSource.getInstance().getLog().error("An exception occurred trying to create player {} in database", event.getPlayer().getName(), e);
            }
        });
    }

    @SneakyThrows
    @Override
    public void close() {
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS))
            executor.shutdownNow();
    }
}
