plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow")
}

group = "com.fancyinnovations"
description = "Tool to decompile Minecraft's server code and extract the source code"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.fancyinnovations.com/releases")
}

dependencies {
    implementation("org.vineflower:vineflower:1.11.2")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("de.oliver.FancyAnalytics:logger:0.0.8")
    implementation("org.jetbrains:annotations:26.1.0")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "com.fancyinnovations.strata.Main"
        }
    }

    shadowJar {
        archiveClassifier.set("")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(21)
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
