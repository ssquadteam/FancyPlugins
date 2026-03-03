package com.fancyinnovations.fancyworlds.api;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.api.worlds.WorldStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public interface FancyWorlds {

    static FancyWorlds get() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("FancyWorlds");
        return (FancyWorlds) plugin;
    }


    WorldStorage getWorldStorage();

    WorldService getWorldService();

}
