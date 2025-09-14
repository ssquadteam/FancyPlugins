package com.fancyinnovations.fancyholograms.api.data.builder;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.data.property.Visibility;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import org.bukkit.entity.Display;
import org.joml.Vector3f;

public abstract class HologramBuilder {

    protected DisplayHologramData data;

    protected HologramBuilder() {
    }

    /**
     * Builds and returns a new Hologram instance using the current configuration
     * in the HologramBuilder.
     *
     * @return a new instance of Hologram created based on the configured data
     */
    public Hologram build() {
        return FancyHolograms.get().getHologramFactory().apply(data);
    }

    /**
     * Builds a new Hologram instance using the current configuration in the HologramBuilder
     * and registers it.
     *
     * @return a new instance of Hologram that has been registered with the registry
     */
    public Hologram buildAndRegister() {
        Hologram hologram = build();
        FancyHolograms.get().getRegistry().register(hologram);
        return hologram;
    }

    // The following methods are setters for the HologramData class

    /**
     * Sets the visibility distance for the hologram
     *
     * @param distance the visibility distance in blocks
     */
    public HologramBuilder visibilityDistance(int distance) {
        data.setVisibilityDistance(distance);
        return this;
    }

    /**
     * Sets the visibility mode for the hologram
     *
     * @param visibility the visibility mode
     */
    public HologramBuilder visibility(Visibility visibility) {
        data.setVisibility(visibility);
        return this;
    }

    /**
     * Sets whether the hologram is persistent (remains after server restarts)
     * Default is true.
     *
     * @param persistent true if the hologram should be persistent, false otherwise
     */
    public HologramBuilder persistent(boolean persistent) {
        data.setPersistent(persistent);
        return this;
    }

    /**
     * Sets the name of the NPC to which the hologram is linked.
     * It will become the NPC's display name.
     *
     * @param linkedNpcName the name of the NPC to link to
     */
    public HologramBuilder linkedNpcName(String linkedNpcName) {
        data.setLinkedNpcName(linkedNpcName);
        return this;
    }

    /**
     * Adds a trait to the hologram.
     *
     * @param trait the class of the trait to add
     */
    public final HologramBuilder trait(Class<? extends HologramTrait> trait) {
        data.addTrait(trait);

        return this;
    }

    // The following methods are specific to the DisplayHologramData class

    /**
     * Sets the display billboard mode for the hologram.
     *
     * @param billboard the billboard mode
     */
    public HologramBuilder billboard(Display.Billboard billboard) {
        data.setBillboard(billboard);
        return this;
    }

    /**
     * Sets the display transform for the hologram.
     *
     * @param x the factor to scale on the x-axis
     * @param y the factor to scale on the y-axis
     * @param z the factor to scale on the z-axis
     */
    public HologramBuilder scale(float x, float y, float z) {
        data.setScale(new Vector3f(x, y, z));
        return this;
    }

    /**
     * Moves the hologram by the specified amounts along each axis.
     *
     * @param x the amount to move along the x-axis
     * @param y the amount to move along the y-axis
     * @param z the amount to move along the z-axis
     */
    public HologramBuilder translation(float x, float y, float z) {
        data.setTranslation(new Vector3f(x, y, z));
        return this;
    }

    /**
     * Sets the brightness for the hologram.
     *
     * @param blockLight the block light level (0-15)
     * @param skyLight   the skylight level (0-15)
     */
    public HologramBuilder brightness(int blockLight, int skyLight) {
        data.setBrightness(new Display.Brightness(blockLight, skyLight));
        return this;
    }

    /**
     * Sets the shadow properties for the hologram.
     *
     * @param shadowRadius the radius of the shadow
     */
    public HologramBuilder shadowRadius(float shadowRadius) {
        data.setShadowRadius(shadowRadius);
        return this;
    }

    /**
     * Sets the shadow strength for the hologram.
     *
     * @param shadowStrength the strength of the shadow
     */
    public HologramBuilder shadowStrength(float shadowStrength) {
        data.setShadowStrength(shadowStrength);
        return this;
    }

    /**
     * Sets the interpolation duration for the hologram.
     *
     * @param interpolationDuration the duration in ticks
     */
    public HologramBuilder interpolationDuration(int interpolationDuration) {
        data.setInterpolationDuration(interpolationDuration);
        return this;
    }
}
