---
description: >-
  One command, /connection reload, its confirmation step, and the two
  permissions that guard it.
---

# Commands and permissions

## Commands

| Command                      | Permission          | What it does                                                                                                              |
| ---------------------------- | ------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| `/connection reload`         | `connection.reload` | Reloads the config and rebuilds the pool—unless a pool is already active, in which case it warns and asks you to confirm. |
| `/connection reload confirm` | `connection.reload` | Reloads unconditionally, active pool or not.                                                                              |

A reload re-reads `config.yml`, re-applies `logLevel`, registers the configured JDBC driver if that driver isn't registered yet, closes the current pool, and opens a new one from the new settings.

## Why the confirmation exists

Run `/connection reload` while the pool is live and you get this instead of a reload:

```
Connection source is already active.
Are you sure you want to reload the connection source? It may cause data loss in dependant plugins.
To confirm, type /connection reload confirm
```

The warning is about the other plugins on the server. A reload closes the pool they're sharing, and any plugin holding a reference to it—or to the ORMLite `ConnectionSource` it exposes—is left with a closed one. Writes in flight at that moment can fail. The safe sequence is to stop what's writing first, or to restart the server instead.

Consumer plugins can avoid the problem by fetching the pool from the provider rather than caching it. See [Rules for sharing a pool](using-it-from-your-plugin.md#rules-for-sharing-a-pool).

## Permissions

| Permission          | Default  | Grants                                                              |
| ------------------- | -------- | ------------------------------------------------------------------- |
| `connection.reload` | Operator | Both forms of `/connection reload`.                                 |
| `connection.*`      | Operator | Every ConnectionSource permission, which means `connection.reload`. |

## When CommandAPI is missing

The command is registered through [CommandAPI](https://commandapi.jorel.dev), a soft dependency. If it isn't installed, ConnectionSource logs `CommandAPI not found, commands are not registered` at startup and runs without the command—the pool works as usual, and config changes take effect on the next server restart.
