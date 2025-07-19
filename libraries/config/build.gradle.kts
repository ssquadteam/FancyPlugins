plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
}

group = "de.oliver"
description = "Library for plugin configuration"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.fancyinnovations.com/releases")
}

dependencies {
    implementation("org.jetbrains:annotations:26.0.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.12.2")
    testImplementation("org.junit.platform:junit-platform-console-standalone:1.12.2")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    test {
        useJUnitPlatform()
    }
}
