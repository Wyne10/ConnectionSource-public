plugins {
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.35.0"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnlyApi(libs.ormLiteJdbc)
}

java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}

tasks.named("publish") {
    dependsOn("publishToMavenLocal")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(findProperty("centralGroup").toString(), "connectionsource-api", version.toString())

    pom {
        name.set("ConnectionSource API")
        description.set("Consumer API for the ConnectionSource Bukkit/Paper plugin, which owns a single JDBC connection pool for the whole server and shares it with every plugin that needs a database.")
        inceptionYear.set("2025")
        url.set("https://github.com/Wyne10/ConnectionSource")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("Wyne10")
                name.set("Wyne")
                email.set("izmodenov1997@gmail.com")
                organization.set("BigTeam")
                organizationUrl.set("https://github.com/NeverMined-Entertainment")
            }
        }
        scm {
            url.set("https://github.com/Wyne10/ConnectionSource")
            connection.set("scm:git:git://github.com/Wyne10/ConnectionSource.git")
            developerConnection.set("scm:git:ssh://git@github.com/Wyne10/ConnectionSource.git")
        }
    }
}
