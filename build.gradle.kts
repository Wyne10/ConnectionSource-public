import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.codehaus.plexus.util.Os

plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.lombok)
    alias(libs.plugins.runPaper)
    alias(libs.plugins.pluginYml)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}

dependencies {
    compileOnly(libs.paperApi)
    compileOnly(libs.commandApi)

    implementation(project(":api"))
    implementation(libs.guice)
    implementation(libs.hikari)
    implementation(libs.ormLiteJdbc)

    implementation(libs.wutilsConfig)
    implementation(libs.wutilsCommon)
}

tasks {
    val isDebug = findProperty("debug")?.toString()?.toBoolean() ?: false

    shadowJar {
        archiveBaseName.set(findProperty("name").toString())
        archiveClassifier.set("")
        if (!isDebug) {
            relocate("com.google.inject", "me.wyne.connection.shadow.google.guice")
            relocate("com.google.common", "me.wyne.connection.shadow.google.common")
            relocate("me.wyne.wutils", "me.wyne.connection.shadow.wutils")
        }
    }

    runServer {
        val minecraftVersion: String = if (Os.isFamily(Os.FAMILY_WINDOWS) || isDebug) "1.21.3" else "1.16.5"
        val commandApiVersions = mapOf("1.21.3" to "9.7.0", "1.16.5" to "9.4.2")
        val commandApiVersion = commandApiVersions[minecraftVersion] ?: "9.7.0"
        downloadPlugins {
            url("https://download.luckperms.net/1594/bukkit/loader/LuckPerms-Bukkit-5.5.9.jar")
            url("https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/artifact/build/libs/ProtocolLib.jar")
            github("ViaVersion", "ViaVersion", "5.2.1", "ViaVersion-5.2.1.jar")
            github("ViaVersion", "ViaBackwards", "5.2.1", "ViaBackwards-5.2.1.jar")
            github("CommandAPI", "CommandAPI", commandApiVersion, "CommandAPI-${commandApiVersion}.jar")
        }
        minecraftVersion(minecraftVersion)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition", "-DPaper.IgnoreJavaVersion=true")
}

bukkit {
    name = findProperty("name").toString()
    version = getVersion().toString()
    website = findProperty("website").toString()
    author = findProperty("author").toString()
    main = "me.wyne.connection.ConnectionSource"
    apiVersion = "1.16"
    softDepend = listOf("CommandAPI")
    permissions {
        register("connection.*") {
            children = listOf("connection.reload")
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("connection.reload") {
            description = "Allows to reload plugin"
        }
    }
}