plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("1.21.6-R0.1-SNAPSHOT")
    compileOnly(project(":libraries:packets:packets-api"))

//    compileOnly("com.fancyinnovations:fancymc:1.21.6-pre2")

    testImplementation(project(":libraries:packets"))
    testImplementation(project(":libraries:packets:packets-api"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.12.2")
    testImplementation("org.junit.platform:junit-platform-console-standalone:1.12.2")
}

tasks {
    test {
        useJUnitPlatform()
    }
}