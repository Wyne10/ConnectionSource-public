---
description: >-
  One JDBC connection pool, owned by one plugin, shared by every plugin on the
  server—instead of a pool, a driver jar and a copy of your credentials per
  plugin.
---

# ConnectionSource

ConnectionSource is a Bukkit/Paper plugin that owns a single JDBC connection pool for the whole server and hands it to every other plugin that needs a database.

Without it, each plugin that stores data in SQL opens its own pool. A server running 10 such plugins ends up with 10 pools, 10 sets of idle connections against the same database, 10 copies of the same credentials in 10 config files, and 10 driver jars shaded into 10 plugin jars. ConnectionSource replaces that with one pool, one place to put the credentials, and one driver download shared by everyone.

## What it gives you

* **One pool for the server.** The pool is [HikariCP](https://github.com/brettwooldridge/HikariCP) with an [ORMLite](https://ormlite.com) `ConnectionSource` on top, so a consumer can work through ORMLite DAOs or take a raw `java.sql.Connection`—whichever that plugin prefers.
* **One place to configure the database.** The server admin fills in `plugins/ConnectionSource/config.yml` once. See [Configuration](configuration.md).
* **No driver jars to ship.** ConnectionSource downloads the JDBC driver for MySQL, MariaDB, PostgreSQL, SQLite or H2 from Maven Central at runtime and registers it with `DriverManager`.
* **A small API to consume.** Consumers compile against `io.github.wyne10:connectionsource-api` from Maven Central and fetch the pool through `CSApi` or the Bukkit services manager. See [Using it from your plugin](using-it-from-your-plugin.md).
* **A reload command.** `/connection reload` rebuilds the pool after a config change, with a confirmation step because dependent plugins hold references to the pool it replaces. See [Commands and permissions](commands-and-permissions.md).

## Requirements

|          |                                                                           |
| -------- | ------------------------------------------------------------------------- |
| Server   | Bukkit/Paper 1.16 or later                                                |
| Java     | 16 or later                                                               |
| Optional | [CommandAPI](https://docs.commandapi.dev/), for the `/connection` command |

CommandAPI is a soft dependency. Without it the plugin still provides the pool; only the command is missing, so config changes need a server restart instead of a reload.

## How it fits together

1. ConnectionSource enables, reads its config, and registers the configured JDBC driver.
2. It opens the pool and publishes the `ConnectionProvider` through `CSApi` and the Bukkit services manager.
3. Consumer plugins enable after it, ask for the provider, and take the pool from it.
4. On server shutdown ConnectionSource closes the pool. Consumers never close it themselves.

To compile the plugin yourself, see [Building from source](building-from-source.md).
