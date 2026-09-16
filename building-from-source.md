---
description: >-
  Clone with submodules, build the shaded plugin jar, run a test server, and
  publish the API to Maven Central.
---

# Building from source

## What you need

Git, a JDK to run Gradle with, and nothing else—the Gradle wrapper fetches Gradle itself, and the build provisions the JDKs it actually compiles and runs with: Java 16 for compilation, and a JetBrains Runtime 21 for the test server.

## Clone

`gradle/libs.versions.toml` is a symlink into the `libs` submodule, which holds the version catalog shared across these plugins. A clone without it fails while Gradle configures the build:

```bash
git clone --recurse-submodules https://github.com/Wyne10/ConnectionSource.git
```

If you already cloned without `--recurse-submodules`, run `git submodule update --init`.

## Build

```bash
./gradlew build
```

The shaded plugin jar lands in `build/libs/ConnectionSource-<version>.jar`, ready to drop into a server's `plugins/` folder. The build relocates Guice, Guava, and WUtils under `me.wyne.connection.shadow`, so the plugin can't collide with another plugin bundling the same libraries.

| Command                        | What it does                                                                          |
| ------------------------------ | ------------------------------------------------------------------------------------- |
| `./gradlew build -Pdebug=true` | Builds without relocating anything, which keeps stack traces readable while debugging |
| `./gradlew :api:build`         | Builds the consumer API module on its own                                             |
| `./gradlew clean build`        | Rebuilds from scratch                                                                 |

The project version lives in `gradle.properties` and names both the jar and the published API artifact.

## Run a test server

```bash
./gradlew runServer
```

This downloads Paper 1.16.5 along with CommandAPI, LuckPerms, ProtocolLib, ViaVersion, and ViaBackwards, then starts a server in `run/` with the freshly built plugin installed. Add `-Pdebug=true` to run on 1.21.3 instead, with relocation off.

The server's copy of the config is `run/plugins/ConnectionSource/config.yml`. Point it at a database and run [`/connection reload confirm`](commands-and-permissions.md) to exercise the pool without restarting.

## Publish the API

The `api` module is the only thing published; the plugin jar itself is distributed as a jar, not as a dependency.

To try a release locally, into `~/.m2`:

```bash
./gradlew :api:publishToMavenLocal
```
