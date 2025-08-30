package com.fancyinnovations.fancyholograms.trait.builtin;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitClass;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ApiStatus.Experimental
@HologramTraitClass(traitName = "multiple_pages_trait")
public class MultiplePagesTrait extends HologramTrait {

    private static final Configuration DEFAULT_CONFIG = new Configuration(
            Mode.CYCLE,
            1000,
            List.of(
                    new Page(List.of("Page 1", "Line 1", "Line 2")),
                    new Page(List.of("Page 2", "Line 1", "Line 2"))
            ));

    private Configuration config;
    private int currentPageIdx;

    public MultiplePagesTrait() {
        this.currentPageIdx = 0;
    }

    @Override
    public void onAttach() {
        if (!(hologram.getData() instanceof TextHologramData td)) {
            throw new IllegalStateException("Hologram must be text hologram to use MultiplePagesTrait");
        }

        load();

        hologramThread.scheduleWithFixedDelay(() -> {
            Page currentPage = config.pages().get(currentPageIdx);
            td.setText(new ArrayList<>(currentPage.lines()));

            currentPageIdx = switch (config.mode()) {
                case CYCLE -> (currentPageIdx + 1) % config.pages().size();
                case RANDOM -> (int) (Math.random() * config.pages().size());
                default -> currentPageIdx;
            };
        }, 0, config.cycleDelay(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void save() {
        try {
            storage.set(hologram.getData().getName(), config);
        } catch (IOException e) {
            logger.error("Failed to save configuration for MultiplePagesTrait", ThrowableProperty.of(e));
        }
    }

    @Override
    public void load() {
        try {
            config = storage.get(hologram.getData().getName(), Configuration.class);
        } catch (IOException e) {
            logger.error("Failed to load configuration for MultiplePagesTrait", ThrowableProperty.of(e));
        }
        if (config == null) {
            config = DEFAULT_CONFIG;
            save();
        }
    }

    enum Mode {
        MANUAL,
        CYCLE,
        RANDOM,
    }

    record Configuration(
            Mode mode,
            long cycleDelay,
            List<Page> pages
    ) {

    }

    record Page(List<String> lines) {

        public void addLine(String line) {
            lines.add(line);
        }

        public void removeLine(int index) {
            lines.remove(index);
        }
    }
}
