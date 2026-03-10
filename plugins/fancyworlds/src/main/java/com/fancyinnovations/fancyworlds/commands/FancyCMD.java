package com.fancyinnovations.fancyworlds.commands;

import com.fancyinnovations.fancyworlds.config.FancyWorldsConfigImpl;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.translations.Translator;

public abstract class FancyCMD {

    protected final FancyWorldsPlugin plugin;
    protected final ExtendedFancyLogger logger;
    protected final FancyWorldsConfigImpl config;
    protected final Translator translator;

    public FancyCMD() {
        this.plugin = FancyWorldsPlugin.get();
        this.logger = FancyWorldsPlugin.get().getFancyLogger();
        this.config = (FancyWorldsConfigImpl) FancyWorldsPlugin.get().getFancyWorldsConfig();
        this.translator = FancyWorldsPlugin.get().getTranslator();
    }

}
