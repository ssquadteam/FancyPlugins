package com.fancyinnovations.fancyworlds.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public interface FancyWorlds {

    static FancyWorlds get() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("FancyWorlds");
        return (FancyWorlds) plugin;
    }

}
