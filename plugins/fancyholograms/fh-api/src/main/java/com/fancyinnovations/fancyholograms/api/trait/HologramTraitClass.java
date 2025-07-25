package com.fancyinnovations.fancyholograms.api.trait;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HologramTraitClass {
    /**
     * The name of the trait. This is used to identify the trait in the system.
     *
     * @return The name of the trait.
     */
    String traitName();

    /**
     * Whether the trait is a default trait or not. Default traits are automatically attached to holograms.
     *
     * @return The description of the trait.
     */
    boolean defaultTrait() default false;
}
