plugins {
    id("java-library")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly(project(":libraries:packets:api"))
    compileOnly(project(":libraries:packets:implementations:1_20_6"))
    compileOnly(project(":libraries:packets:implementations:1_21_3"))
    compileOnly(project(":libraries:packets:implementations:1_21_4"))
    compileOnly(project(":libraries:packets:implementations:1_21_5"))
}

tasks {
    java {
        withSourcesJar()
        withJavadocJar()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21

    }
}