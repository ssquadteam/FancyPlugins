package com.fancyinnovations.fancyholograms.api;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The controller for holograms, responsible for showing and hiding them to players.
 */
public interface HologramController {

    /**
     * Shows the hologram to the given players if they should see it, and it is not yet shown to them.
     */
    @ApiStatus.Internal
    void showHologramTo(@NotNull final Hologram hologram, @NotNull final Player... players);

    /**
     * Hides the hologram from the given players if they should not see it, and it is shown to them.
     */
    @ApiStatus.Internal
    void hideHologramFrom(@NotNull final Hologram hologram, @NotNull final Player... players);

    /**
     * Returns whether the given player should see the hologram.
     */
    @ApiStatus.Internal
    boolean shouldSeeHologram(@NotNull final Hologram hologram, @NotNull final Player player);

    /**
     * Updates hologram data such as text, background, etc. for the given players.
     * Be aware that some data changes require the hologram to be fully respawned.
     */
    @ApiStatus.Internal
    void updateHologramData(@NotNull final Hologram hologram, @NotNull final Player... players);

    /**
     * Spawns the hologram to the given players if they should see it, and it is not yet shown to them.
     * Hide the hologram from the players that should not see it.
     */
    void refreshHologram(@NotNull final Hologram hologram, @NotNull final Player... players);

    default void refreshHologram(@NotNull final Hologram hologram, @NotNull final Collection<? extends Player> players) {
        refreshHologram(hologram, players.toArray(new Player[0]));
    }

}
