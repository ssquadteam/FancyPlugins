package com.fancyinnovations.fancyworlds.commands.fancyworlds;

import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancylib.translations.message.SimpleMessage;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class FWConfigCMD extends FancyContext {

    public static final FWConfigCMD INSTANCE = new FWConfigCMD();

    @Command("fancyworlds config reload")
    @Description("Reloads the config of FancyWorlds.")
    @CommandPermission("fancyworlds.commands.fancyworlds.config.reload")
    public void configReload(
            final BukkitCommandActor actor
    ) {
        if (actor.isPlayer()) {
            SimpleMessage question = (SimpleMessage) translator.translate("commands.fancyworlds.config.reload.confirmation");
            new ConfirmationDialog(question.getMessage())
                    .withTitle("Confirm reload")
                    .withOnConfirm(() -> configReloadImpl(actor))
                    .withOnCancel(() -> translator.translate("commands.fancyworlds.config.reload.cancelled").withPrefix().send(actor.sender()))
                    .ask(actor.asPlayer());
        } else {
            configReloadImpl(actor);
        }
    }

    private void configReloadImpl(
            final BukkitCommandActor actor
    ) {
        config.reload();
        logger.setCurrentLevel(LogLevel.valueOf(config.getLogLevel()));
        plugin.registerTranslator();

        translator.translate("commands.fancyworlds.config.reload.success")
                .withPrefix()
                .send(actor.sender());
    }

}
