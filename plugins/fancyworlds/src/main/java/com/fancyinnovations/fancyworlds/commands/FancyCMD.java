package com.fancyinnovations.fancyworlds.commands;

import com.fancyinnovations.fancyworlds.api.FancyWorldsConfig;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.translations.Translator;

public abstract class FancyCMD {

    protected final FancyWorldsPlugin plugin = FancyWorldsPlugin.get();
    protected final ExtendedFancyLogger logger = FancyWorldsPlugin.get().getFancyLogger();
    protected final FancyWorldsConfig config = FancyWorldsPlugin.get().getFancyWorldsConfig();
    protected final Translator translator = FancyWorldsPlugin.get().getTranslator();

}
