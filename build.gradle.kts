allprojects {
    group = "de.oliver"
    description = "Minecraft plugins of FancyInnovations"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.fancyplugins.de/releases")
        maven(url = "https://jitpack.io")
    }
}
