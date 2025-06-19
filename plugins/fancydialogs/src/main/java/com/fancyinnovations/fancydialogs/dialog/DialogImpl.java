package com.fancyinnovations.fancydialogs.dialog;

import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import de.oliver.fancysitula.api.dialogs.FS_CommonDialogData;
import de.oliver.fancysitula.api.dialogs.FS_DialogAction;
import de.oliver.fancysitula.api.dialogs.actions.FS_CommonButtonData;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogActionButton;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogCustomAction;
import de.oliver.fancysitula.api.dialogs.body.FS_DialogBody;
import de.oliver.fancysitula.api.dialogs.body.FS_DialogTextBody;
import de.oliver.fancysitula.api.dialogs.types.FS_MultiActionDialog;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.factories.FancySitula;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogImpl extends Dialog {

    private FS_MultiActionDialog fsDialog;

    public DialogImpl(String id, DialogData data) {
        super(id, data);
        init();
    }

    private void init() {
        List<FS_DialogBody> body = new ArrayList<>();
        for (DialogBodyData bodyData : data.body()) {
            FS_DialogTextBody fsDialogTextBody = new FS_DialogTextBody(
                    bodyData.text(),
                    200 // default text width
            );
            body.add(fsDialogTextBody);
        }

        List<FS_DialogActionButton> actions = new ArrayList<>();
        for (DialogButton button : data.buttons()) {
            FS_DialogActionButton fsDialogActionButton = new FS_DialogActionButton(
                    new FS_CommonButtonData(
                            button.label(),
                            button.tooltip(),
                            150 // default button width
                    ),
                    new FS_DialogCustomAction(
                            "fancydialogs_dialog_action",
                            Map.of(
                                    "dialog_id", id,
                                    "button_id", button.id()
                            )
                    )
            );
            actions.add(fsDialogActionButton);
        }

        this.fsDialog = new FS_MultiActionDialog(
                new FS_CommonDialogData(
                        data.title(),
                        data.title(),
                        data.canCloseWithEscape(),
                        false,
                        FS_DialogAction.CLOSE,
                        body,
                        List.of(
//                                new FS_DialogInput(
//                                        "input1",
//                                        new FS_DialogTextInput(
//                                                200,
//                                                "Enter something",
//                                                true,
//                                                "default text",
//                                                100,
//                                                null
//                                        )
//                                ),
//                                new FS_DialogInput(
//                                        "input2",
//                                        new FS_DialogBooleanInput(
//                                                "input2",
//                                                false,
//                                                "true",
//                                                "false"
//                                        )
//                                ),
//                                new FS_DialogInput(
//                                        "input3",
//                                        new FS_DialogNumberRangeInput(
//                                                200,
//                                                "Number Input",
//                                                "options.generic_value",
//                                                0,
//                                                100,
//                                                50.f,
//                                                1.0f
//                                        )
//                                ),
//                                new FS_DialogInput(
//                                        "input4",
//                                        new FS_DialogSingleOptionInput(
//                                                200,
//                                                List.of(
//                                                        new FS_DialogSingleOptionInput.Entry("option1", "Option 1", true),
//                                                        new FS_DialogSingleOptionInput.Entry("option2", "Option 2", false),
//                                                        new FS_DialogSingleOptionInput.Entry("option3", "Option 3", false)
//                                                ),
//                                                "Select an option",
//                                                true
//                                        )
//                                )
                        )
                ),
                actions, // actions
                null,
                2
        );
    }

    @Override
    public void open(Player player) {
        FancySitula.PACKET_FACTORY
                .createShowDialogPacket(fsDialog)
                .send(new FS_RealPlayer(player));
    }

    @Override
    public void close(Player player) {
        FancySitula.PACKET_FACTORY
                .createClearDialogPacket()
                .send(new FS_RealPlayer(player));
    }

}
