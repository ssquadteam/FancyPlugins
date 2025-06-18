package com.fancyinnovations.fancydialogs.registry;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.dialog.DialogImpl;
import com.fancyinnovations.fancydialogs.storage.DialogStorage;

import java.io.File;
import java.util.List;

public class DefaultDialogs {

    private static final DialogRegistry registry = FancyDialogsPlugin.get().getDialogRegistry();
    private static final DialogStorage storage = FancyDialogsPlugin.get().getDialogStorage();

    public static void registerDefaultDialogs() {
        File dialogsFolder = new File("plugins/FancyDialogs/data/dialogs");
        if (dialogsFolder.exists()) {
            return;
        }
        dialogsFolder.mkdirs();

        welcomeToFancyDialogsDialog();
        welcomeDialog();
        quickActions();
    }

    private static void welcomeToFancyDialogsDialog() {
        DialogData data = new DialogData(
                "welcome_to_fancydialogs",
                "<u><b><color:#ff7300>Welcome to FancyDialogs!</color></b></u>",
                false,
                List.of(
                        new DialogBodyData("<color:#ffd199><i>The simple and lightweight dialog plugin for your server!<i></color>"),
                        new DialogBodyData(""),
                        new DialogBodyData("This dialog is a demonstration of how to use FancyDialogs to create interactive and user-friendly dialogs."),
                        new DialogBodyData("FancyDialogs supports <rainbow>MiniMessages</rainbow> and PlaceholderAPI"),
                        new DialogBodyData("Explore more features in the documentation (<click:open_url:'https://docs.fancyinnovations.com/fancyholograms/'><u>click here</u></click>)."),
                        new DialogBodyData("<gradient:#ff7300:#ffd199:#ff7300>Enjoy using FancyDialogs :D</gradient>")
                ),
                List.of(
                        new DialogButton(
                                "<color:#ff4f19>Close</color>",
                                "<color:#ff4f19>Enjoy using FancyDialogs</color>",
                                "close_dialog",
                                ""
                        ),
                        new DialogButton(
                                "<color:#ffd000>Run command</color>",
                                "<color:#ff4f19>Click to give yourself an apple :)</color>",
                                "run_command:",
                                "give {player} apple 1"
                        )
                )
        );

        register(data);
    }

    private static void welcomeDialog() {
        DialogData data = new DialogData(
                "welcome",
                "<b><color:#00ff5e>Welcome to {server_name}!</color></b>",
                false,
                List.of(
                        new DialogBodyData("<color:#a8ffb4><i>The best Minecraft server on earth!<i></color>"),
                        new DialogBodyData(""),
                        new DialogBodyData("We are glad to have you here!"),
                        new DialogBodyData("If you have any questions, feel free to ask our staff members.")
                ),
                List.of(
                        new DialogButton(
                                "<color:red>Read the rules</color>",
                                "<color:red>Click to read our rules!</color>",
                                "message",
                                "<click:open_url:'{LINK TO RULES}'>Visit our rules</click>!"
                        ),
                        new DialogButton(
                                "<color:#00ff5e>Start playing</color>",
                                "<color:#00ff5e>Click to start playing!</color>",
                                "close_dialog",
                                ""
                        ),
                        new DialogButton(
                                "<color:#1787ff>Join our Discord</color>",
                                "<color:#1787ff>Click to join our Discord server!</color>",
                                "message",
                                "<click:open_url:'{LINK TO DISC SERVER}'>Join our Discord server</click>!"
                        ),
                        new DialogButton(
                                "<color:#ffee00>Visit our website</color>",
                                "<color:#ffee00>Click to visit our website!</color>",
                                "message",
                                "<click:open_url:'{LINK TO WEBSITE}'>Visit our website</click>!"
                        )
                )
        );

        register(data);
    }

    private static void quickActions() {
        DialogData data = new DialogData(
                "quick_actions",
                "<u><b><color:gold>Quick Actions</color></b></u>",
                false,
                List.of(
                        new DialogBodyData("Here you can quickly access some of the most important features of our server.")
                ),
                List.of(
                        new DialogButton(
                                "<color:#ffee00>Visit our website</color>",
                                "<color:#ffee00>Click to visit our website!</color>",
                                "message",
                                "<click:open_url:'{LINK TO WEBSITE}'>Visit our website</click>!"
                        ),
                        new DialogButton(
                                "<color:#ffee00>Read the rules</color>",
                                "<color:#ffee00>Click to read our rules!</color>",
                                "message",
                                "<click:open_url:'{LINK TO RULES}'>Visit our rules</click>!"
                        ),
                        new DialogButton(
                                "<color:#ffee00>Join our Discord</color>",
                                "<color:#ffee00>Click to join our Discord server!</color>",
                                "message",
                                "<click:open_url:'{LINK TO DISCORD}'>Join our Discord server</click>!"
                        ),
                        new DialogButton(
                                "<color:#ffee00>Support us</color>",
                                "<color:#ffee00>Click to support us!</color>",
                                "message",
                                "<click:open_url:'{LINK TO SUPPORT}'>Support us</click>!"
                        ),
                        new DialogButton(
                                "<color:red>Close</color>",
                                "<color:red>Click to close this dialog!</color>",
                                "close_dialog",
                                ""
                        )
                )
        );

        register(data);
    }

    private static void register(DialogData dialogData) {
        DialogImpl dialog = new DialogImpl(dialogData.id(), dialogData);
        storage.save(dialogData);
        registry.register(dialog);
    }

}
