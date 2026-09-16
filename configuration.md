---
description: >-
  The database credentials, the JDBC driver to download, and the log level—the
  whole of plugins/ConnectionSource/config.yml.
---

# Configuration

Everything lives in `plugins/ConnectionSource/config.yml`, which the plugin generates on first startup:

```yaml
# OFF/FATAL/ERROR/WARN/INFO/DEBUG/TRACE/ALL
logLevel: INFO


#####
# SQL
#####
sql:
  # Load and register JDBC driver, options: NONE, H2_V1, H2_V2, MYSQL, MARIADB, POSTGRESQL, SQLITE
  driver: 'NONE'
  jdbcUrl: 'jdbc:mysql://localhost:3306/test'
  username: ''
  password: ''
```

| Key            | What it does                                                                                                      |
| -------------- | ----------------------------------------------------------------------------------------------------------------- |
| `logLevel`     | Threshold for the plugin's own logging. `DEBUG` and `TRACE` are for diagnosing a connection that refuses to open. |
| `sql.driver`   | Which JDBC driver to download and register. See [Drivers](configuration.md#drivers).                              |
| `sql.jdbcUrl`  | The JDBC URL of your database, including host, port, and database name.                                           |
| `sql.username` | Database user.                                                                                                    |
| `sql.password` | Password for that user. May be empty.                                                                             |

## A worked example

A MySQL database named `minecraft` on the same machine as the server:

```yaml
sql:
  driver: 'MYSQL'
  jdbcUrl: 'jdbc:mysql://localhost:3306/minecraft'
  username: 'minecraft'
  password: 'a-real-password'
```

Apply it with `/connection reload confirm`, or by restarting the server.

ConnectionSource logs through the server's own logger, so its output goes to the console and `logs/latest.log` behind a `[ConnectionSource]` prefix. It keeps no log files of its own.

## Drivers

`sql.driver` names the driver ConnectionSource downloads from Maven Central and registers with `java.sql.DriverManager`:

| Value        | Artifact                                      | Typical URL                            |
| ------------ | --------------------------------------------- | -------------------------------------- |
| `NONE`       | Nothing is downloaded or registered           | —                                      |
| `H2_V1`      | `com.h2database:h2:1.4.200`                   | `jdbc:h2:./database`                   |
| `H2_V2`      | `com.h2database:h2:2.4.240`                   | `jdbc:h2:./database`                   |
| `MYSQL`      | `com.mysql:mysql-connector-j:26.7.0`          | `jdbc:mysql://host:3306/database`      |
| `MARIADB`    | `org.mariadb.jdbc:mariadb-java-client:3.5.10` | `jdbc:mariadb://host:3306/database`    |
| `POSTGRESQL` | `org.postgresql:postgresql:42.7.13`           | `jdbc:postgresql://host:5432/database` |
| `SQLITE`     | `org.xerial:sqlite-jdbc:3.53.2.1`             | `jdbc:sqlite:./database.db`            |

Pick `H2_V1` over `H2_V2` only to keep reading data files written by H2 1.x—the two formats aren't interchangeable.

The jar lands in `libraries/<group>/<artifact>/<version>/` under the **server's working directory**, not under the plugin folder. ConnectionSource downloads it once and reuses it on later starts.

Leave `driver` at `NONE` when something else on the server already registers a driver for your URL—another plugin, or a driver on the server classpath. If nothing does, opening the pool fails with a `No suitable driver` error.

## When the plugin considers itself configured

ConnectionSource opens the pool only when **both `sql.jdbcUrl` and `sql.username` are non-empty**. Otherwise it logs `SQL connection is not configured`, starts without a pool, and every consumer plugin sees a provider with no pool behind it.

{% hint style="warning" %}
This catches out SQLite and H2, which don't need a user. Put any non-empty value in `sql.username` for those—`sa` is the conventional choice—or the pool never opens.
{% endhint %}

## Applying changes

Edit `config.yml`, then either run [`/connection reload`](commands-and-permissions.md) or restart the server. A reload re-reads the config, registers the driver if the name changed, closes the old pool, and opens a new one.

## Files the plugin writes

| Path                                           | Contents                                                                                                      |
| ---------------------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| `plugins/ConnectionSource/config.yml`          | Your config. Regenerated on each startup: keys added by a new version are merged in and your values are kept. |
| `plugins/ConnectionSource/defaults/config.yml` | The generated defaults the merge works from. Don't edit it.                                                   |
| `plugins/ConnectionSource/backups/`            | A copy of `config.yml` from before each regeneration.                                                         |
| `libraries/` (server root)                     | Downloaded JDBC driver jars.                                                                                  |
