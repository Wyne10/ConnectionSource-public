package org.bigcraft.connection.jdbc;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DriverLibrary {
    NONE("", "", "", ""),
    H2_V1(
            "com.h2database",
            "h2",
            "1.4.200",
            "org.h2.Driver"
    ),
    H2_V2(
            "com.h2database",
            "h2",
            "2.4.240",
            "org.h2.Driver"
    ),
    MYSQL(
            "com.mysql",
            "mysql-connector-j",
            "26.7.0",
            "com.mysql.cj.jdbc.NonRegisteringDriver"
    ),
    MARIADB(
            "org.mariadb.jdbc",
            "mariadb-java-client",
            "3.5.10",
            "org.mariadb.jdbc.Driver"
    ),
    POSTGRESQL(
            "org.postgresql",
            "postgresql",
            "42.7.13",
            "org.postgresql.Driver"
    ),
    SQLITE(
            "org.xerial",
            "sqlite-jdbc",
            "3.53.2.1",
            "org.sqlite.JDBC"
    );

    private final Path filenamePath;
    private final URL mavenRepoURL;
    private final String driverClass;
    private boolean isRegistered;
    // Held for the lifetime of the JVM. The registered driver keeps loading classes from
    // this loader lazily, so closing it would break the driver at first use.
    private URLClassLoader loader;

    DriverLibrary(@NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String driverClass) {
        if (this.name().equals("NONE"))
            isRegistered = true;

        String mavenPath = String.format("%s/%s/%s/%s-%s.jar",
                groupId.replace(".", "/"),
                artifactId,
                version,
                artifactId,
                version
        );

        this.filenamePath = Path.of("libraries/" + mavenPath);
        this.driverClass = driverClass;

        try {
            this.mavenRepoURL = new URL("https://repo1.maven.org/maven2/" + mavenPath);
        } catch (MalformedURLException e) {
            throw new RuntimeException("An exception occurred trying to format maven path to URL", e);
        }
    }

    private @NotNull URL getClassLoaderURL() throws IOException {
        if (!Files.exists(this.filenamePath)) {
            Path parent = this.filenamePath.getParent();
            Files.createDirectories(parent);
            // Download to a temporary file and move it into place, so an interrupted
            // download cannot leave a partial jar that later calls would accept as valid.
            Path temp = Files.createTempFile(parent, this.filenamePath.getFileName().toString(), ".part");
            try (InputStream in = this.mavenRepoURL.openStream()) {
                Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
                moveIntoPlace(temp, this.filenamePath);
            } finally {
                Files.deleteIfExists(temp);
            }
        }

        return this.filenamePath.toUri().toURL();
    }

    private static void moveIntoPlace(@NotNull Path source, @NotNull Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public synchronized void registerDriver() throws IOException, ClassNotFoundException, SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (isRegistered)
            return;

        loader = new URLClassLoader(new URL[]{getClassLoaderURL()}, DriverLibrary.class.getClassLoader());
        DriverManager.registerDriver(new DriverShim((Driver) loader.loadClass(driverClass).getConstructor().newInstance()));
        isRegistered = true;
    }

}
