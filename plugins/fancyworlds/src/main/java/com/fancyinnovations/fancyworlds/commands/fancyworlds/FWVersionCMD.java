package com.fancyinnovations.fancyworlds.commands.fancyworlds;

import com.fancyinnovations.fancyworlds.api.FancyWorldsConfig;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.translations.Translator;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class FWVersionCMD {

    public static final FWVersionCMD INSTANCE = new FWVersionCMD();

    private final FancyWorldsPlugin plugin = FancyWorldsPlugin.get();
    private final ExtendedFancyLogger logger = FancyWorldsPlugin.get().getFancyLogger();
    private final FancyWorldsConfig config = FancyWorldsPlugin.get().getFancyWorldsConfig();
    private final Translator translator = FancyWorldsPlugin.get().getTranslator();

    @Command("fancyworlds version")
    @Description("Shows the version of FancyWorlds")
    @CommandPermission("fancyworlds.commands.fancyworlds.version")
    public void version(
            final BukkitCommandActor actor
    ) {
        String version = plugin.getPluginMeta().getVersion();

        translator.translate("commands.fancyworlds.version")
                .withPrefix()
                .replace("version", version)
                .send(actor.sender());
    }

}
