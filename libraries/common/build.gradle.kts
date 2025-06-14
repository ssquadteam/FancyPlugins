plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
}

group = "de.oliver"
version = findProperty("fancylibVersion") as String
description = "Library for all Fancy plugins"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.fancyinnovations.com/releases")
}

dependencies {
    compileOnly("dev.folia:folia-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("de.oliver.FancyAnalytics:logger:0.0.6")

    // database drivers and connection pooling
    compileOnly("org.xerial:sqlite-jdbc:3.49.1.0")
    compileOnly("mysql:mysql-connector-java:8.0.33")
    compileOnly("org.postgresql:postgresql:42.7.4")
    compileOnly("com.zaxxer:HikariCP:6.2.1")

    // JSON processing
    compileOnly("com.google.code.gson:gson:2.13.1")

    // testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.12.2")
    testImplementation("org.junit.platform:junit-platform-console-standalone:1.12.2")
    testImplementation("com.google.code.gson:gson:2.13.1")
}

tasks {
    publishing {
        repositories {
            maven {
                name = "fancyinnovationsReleases"
                url = uri("https://repo.fancyinnovations.com/releases")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }

            maven {
                name = "fancyinnovationsSnapshots"
                url = uri("https://repo.fancyinnovations.com/snapshots")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = "de.oliver"
                artifactId = "FancyLib"
                version = findProperty("fancylibVersion") as String
                from(project.components["java"])
            }
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
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

    test {
        useJUnitPlatform()
    }
}
