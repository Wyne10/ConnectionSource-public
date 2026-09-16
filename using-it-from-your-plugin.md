---
description: >-
  Compile against connectionsource-api, ask for the provider once the plugin has
  enabled, and take the shared pool—as ORMLite DAOs or as raw JDBC connections.
---

# Using it from your plugin

## Adding the dependency

The API artifact is published to Maven Central:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.github.wyne10:connectionsource-api:2.0.0")
}
```

Keep it `compileOnly`: the API classes ship inside the ConnectionSource plugin jar at their real package names, so shading your own copy leaves you with two unrelated `ConnectionProvider` classes and a `ClassCastException`.

The artifact carries four types and nothing else, all in `me.wyne.connection.api`—`CSApi`, `ConnectionProvider`, `ConnectionPool<T>`, and `SqlConfig`. If you work through ORMLite DAOs, add `com.j256.ormlite:ormlite-jdbc:6.1` as a `compileOnly` dependency too, since `ConnectionPool.getSource()` returns an ORMLite type.

Then declare the plugin dependency in `plugin.yml`:

```yaml
depend: [ConnectionSource]
```

Use `softdepend` instead if your plugin can run without a database, and guard every call as shown under [When the pool isn't there](using-it-from-your-plugin.md#when-the-pool-isnt-there). Either way the declaration matters: it makes the server enable ConnectionSource first, and the provider doesn't exist before that.

## Getting the pool

ConnectionSource publishes its `ConnectionProvider` two ways during its own `onEnable`. Both return the same singleton, so pick whichever suits your plugin:

```java
// Static access point.
ConnectionProvider provider = CSApi.getProvider();

// Bukkit services manager, registered at ServicePriority.Normal.
ConnectionProvider provider = Bukkit.getServicesManager()
        .getRegistration(ConnectionProvider.class)
        .getProvider();
```

The provider hands you a `ConnectionPool<ConnectionSource>`—a HikariCP pool with an ORMLite `ConnectionSource` in front of it. Both styles of access work on it.

### With ORMLite

`getSource()` returns the ORMLite `ConnectionSource` that DAOs are built from:

```java
ConnectionSource source = CSApi.getProvider().getConnectionPool().getSource();

TableUtils.createTableIfNotExists(source, PlayerEntity.class);
Dao<PlayerEntity, UUID> dao = DaoManager.createDao(source, PlayerEntity.class);

dao.createOrUpdate(new PlayerEntity(player.getUniqueId(), 42));
```

### With raw JDBC

`getConnection()` takes a connection from the Hikari pool. It's yours to close, and closing it returns it to the pool rather than shutting anything down:

```java
ConnectionPool<ConnectionSource> pool = CSApi.getProvider().getConnectionPool();

try (Connection connection = pool.getConnection();
     PreparedStatement statement = connection.prepareStatement(
             "SELECT balance FROM accounts WHERE uuid = ?")) {
    statement.setString(1, player.getUniqueId().toString());
    // ...
}
```

### With Guice

If your plugin uses Guice, bind the provider in a module and inject it where you need it:

```java
public class ConnectionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ConnectionProvider.class)
                .toInstance(Bukkit.getServicesManager()
                        .getRegistration(ConnectionProvider.class)
                        .getProvider());
    }
}
```

## When the pool isn't there

Three things can go wrong, and all of them are ordinary states rather than exceptions:

| Symptom                                                                     | Meaning                                                         |
| --------------------------------------------------------------------------- | --------------------------------------------------------------- |
| `CSApi.getProvider()` returns `null`, or `getRegistration()` returns `null` | ConnectionSource hasn't enabled yet, or isn't installed at all. |
| `getConnectionPool()` returns `null`                                        | The database isn't configured, or the pool failed to open.      |
| `isActive()` returns `false`                                                | No pool, or the pool has been closed.                           |

Check before you reach for the pool, and degrade the way your plugin should—disable itself, or fall back to file storage:

```java
ConnectionProvider provider = CSApi.getProvider();
if (provider == null || !provider.isActive()) {
    getLogger().severe("ConnectionSource is unavailable, disabling");
    getServer().getPluginManager().disablePlugin(this);
    return;
}
```

## Rules for sharing a pool

The pool belongs to ConnectionSource, and the whole server shares it. Three rules follow:

1. **Never close the pool or the provider.** Both are `AutoCloseable`, so a stray try-with-resources compiles—and takes the database away from every other plugin. ConnectionSource closes the pool in its own `onDisable`. Individual `Connection` objects from `getConnection()` are the exception: always close those.
2.  **Don't cache the pool or the ORMLite `ConnectionSource`.** A `/connection reload` closes the old pool and builds a new one, and a cached reference keeps pointing at the closed one. Fetch it from the provider each time you need it, through a small accessor:

    ```java
    private ConnectionSource source() {
        ConnectionProvider provider = CSApi.getProvider();
        if (provider == null || !provider.isActive())
            throw new IllegalStateException("ConnectionSource is unavailable");
        return provider.getConnectionPool().getSource();
    }
    ```
3. **Namespace your tables.** Every plugin on the server shares one database. Prefix table names with your plugin's name so two plugins don't both claim `players`.

Beyond that, keep database work off the main thread—a pooled connection is still a network round trip, and the server ticks while it waits.

## Reading the server's settings

`provider.getConfig()` exposes the same values the admin filled in, in case your plugin needs to adapt to the database in use:

```java
SqlConfig config = provider.getConfig();
boolean sqlite = config.getJdbcUrl().startsWith("jdbc:sqlite:");
```
