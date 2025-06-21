package com.fancyinnovations.fancydialogs.api.data.inputs;

import java.util.ArrayList;
import java.util.List;

public record DialogInputs(
        List<DialogTextField> textFields
) {

    public static final DialogInputs EMPTY = new DialogInputs(List.of());

    public List<DialogInput> all() {
        List<DialogInput> all = new ArrayList<>();
        if (textFields != null) {
            all.addAll(textFields);
        }

        all.sort((o1, o2) -> {
            if (o1.getOrder() == o2.getOrder()) {
                return 0;
            }
            return o1.getOrder() < o2.getOrder() ? -1 : 1;
        });

        return all;
    }

}
