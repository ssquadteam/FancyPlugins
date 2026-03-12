plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow")
}

group = "com.fancyinnovations"
description = "Minecraft server source code"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
    maven(url = "https://libraries.minecraft.net/")
}

dependencies {
    compileOnly(fileTree("../../strata-cache/server-libraries/libraries-26.1-pre-1") {
        include("**/*.jar")
    })

    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("org.checkerframework:checker-qual:3.49.0")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "net.minecraft.server.Main"
        }
    }

    shadowJar {
        archiveClassifier.set("")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(25)
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}
