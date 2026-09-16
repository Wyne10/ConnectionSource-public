plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "ConnectionSource"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io/")
        }
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        }
    }
}
include("api")