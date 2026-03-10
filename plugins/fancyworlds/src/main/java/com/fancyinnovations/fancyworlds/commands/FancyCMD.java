package com.fancyinnovations.fancyworlds.commands;

import com.fancyinnovations.fancyworlds.config.FancyWorldsConfigImpl;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Bukkit;

public abstract class FancyCMD {

    protected final FancyWorldsPlugin plugin;
    protected final ExtendedFancyLogger logger;
    protected final FancyWorldsConfigImpl config;
    protected final Translator translator;
    protected final boolean isFancyDialogsEnabled;

    public FancyCMD() {
        this.plugin = FancyWorldsPlugin.get();
        this.logger = FancyWorldsPlugin.get().getFancyLogger();
        this.config = (FancyWorldsConfigImpl) FancyWorldsPlugin.get().getFancyWorldsConfig();
        this.translator = FancyWorldsPlugin.get().getTranslator();
        this.isFancyDialogsEnabled = Bukkit.getPluginManager().isPluginEnabled("FancyDialogs");
    }

}
