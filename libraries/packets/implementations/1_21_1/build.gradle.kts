plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev")
}

val minecraftVersion = "1.21.1"

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly(project(":libraries:packets:api"))

    testImplementation(project(":libraries:packets:api"))
    testImplementation(project(":libraries:packets:implementations:1_20_6"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

tasks {
    test {
        useJUnitPlatform()
    }
}