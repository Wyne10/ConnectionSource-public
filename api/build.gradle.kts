plugins {
    id("java")
    id("maven-publish")
}

java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}

dependencies {
    compileOnly(libs.ormLiteJdbc)
    compileOnly(libs.wutilsJdbc)
}

publishing {
    repositories {
        val repoUrl = findProperty("myMavenRepoWriteUrl").toString()
        if (repoUrl.isNotEmpty()) {
            maven {
                url = uri(repoUrl)

                credentials {
                    username = findProperty("myMavenRepoWriteUsername").toString()
                    password = findProperty("myMavenRepoWritePassword").toString()
                }
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = findProperty("group").toString()
            artifactId = "ConnectionSource-api"
            version = findProperty("version").toString()

            from(components["java"])
        }
    }
}