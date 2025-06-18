plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")

    compileOnly(project(":plugins:fancynpcs:fn-api"))
    compileOnly(project(":libraries:common"))
    compileOnly("org.lushplugins:ChatColorHandler:5.1.6")
}


tasks {
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }
}