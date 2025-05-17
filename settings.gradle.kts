rootProject.name = "minecraft-plugins"

include(":plugins:fancynpcs:")
include(":plugins:fancynpcs:fn-api")
include(":plugins:fancynpcs:implementation_1_21_5")
include(":plugins:fancynpcs:implementation_1_21_4")
include(":plugins:fancynpcs:implementation_1_21_3")
include(":plugins:fancynpcs:implementation_1_21_1")
include(":plugins:fancynpcs:implementation_1_20_6")
include(":plugins:fancynpcs:implementation_1_20_4")
include(":plugins:fancynpcs:implementation_1_20_2")
include(":plugins:fancynpcs:implementation_1_20_1")
include(":plugins:fancynpcs:implementation_1_20")
include(":plugins:fancynpcs:implementation_1_19_4")

include(":plugins:fancyholograms-v2")
include(":plugins:fancyholograms-v2:api")
include(":plugins:fancyholograms-v2:implementation_1_20_4")
include(":plugins:fancyholograms-v2:implementation_1_20_2")
include(":plugins:fancyholograms-v2:implementation_1_20_1")
include(":plugins:fancyholograms-v2:implementation_1_19_4")

include(":plugins:fancyholograms")
include(":plugins:fancyholograms:fh-api")
include(":plugins:fancyholograms:implementation_1_20_4")
include(":plugins:fancyholograms:implementation_1_20_2")
include(":plugins:fancyholograms:implementation_1_20_1")
include(":plugins:fancyholograms:implementation_1_19_4")

include(":plugins:fancyvisuals")

include(":libraries:common")
include(":libraries:jdb")
include(":libraries:plugin-tests")

include(":libraries:packets:packets-api")
include(":libraries:packets:factories")
include(":libraries:packets:implementations")
include(":libraries:packets:implementations:1_20_6")
include(":libraries:packets:implementations:1_21")
include(":libraries:packets:implementations:1_21_1")
include(":libraries:packets:implementations:1_21_3")
include(":libraries:packets:implementations:1_21_4")
include(":libraries:packets:implementations:1_21_5")
include(":libraries:packets:test_plugin")


include(":tools:deployment")
include(":tools:quick-e2e")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}