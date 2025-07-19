package de.oliver.fancyholograms;

import com.fancyinnovations.config.featureflags.FeatureFlag;
import com.fancyinnovations.config.featureflags.FeatureFlagConfig;

public class FHFeatureFlags {

    public static final FeatureFlag DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS = new FeatureFlag("disable-holograms-for-bedrock-players", "Do not show holograms to bedrock players", false);

    public static void load() {
        FeatureFlagConfig config = new FeatureFlagConfig(FancyHolograms.get());
        config.addFeatureFlag(DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS);
        config.load();
    }

}
