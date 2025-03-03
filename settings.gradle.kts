rootProject.name = "minecraft-plugins"

include(":plugins:fancyholograms")
include(":plugins:fancyholograms:api")
include(":plugins:fancyholograms:implementation_1_20_4")
include(":plugins:fancyholograms:implementation_1_20_2")
include(":plugins:fancyholograms:implementation_1_20_1")
include(":plugins:fancyholograms:implementation_1_19_4")

include(":libraries:common")

include("libraries:packets:api")
include("libraries:packets:factories")
include("libraries:packets:implementations")
include("libraries:packets:implementations:1_20_6")
include("libraries:packets:implementations:1_21")
include("libraries:packets:implementations:1_21_1")
include("libraries:packets:implementations:1_21_3")
include("libraries:packets:implementations:1_21_4")
include("libraries:packets:test_plugin")