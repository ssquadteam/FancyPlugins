package de.oliver.fancyholograms.storage.json;

import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.storage.json.model.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

public class JsonAdapter {

    public static JsonHologramData hologramDataToJson(HologramData data) {
        return new JsonHologramData(
                data.getName(),
                data.getType(),
                new JsonLocation(
                        data.getLocation().getWorld().getName(),
                        data.getLocation().getX(),
                        data.getLocation().getY(),
                        data.getLocation().getZ(),
                        data.getLocation().getYaw(),
                        data.getLocation().getPitch()
                ),
                data.getVisibilityDistance(),
                data.getVisibility(),
                data.getLinkedNpcName()
        );
    }

    public static JsonDisplayHologramData displayHologramDataToJson(DisplayHologramData data) {
        return new JsonDisplayHologramData(
                new JsonVec3f(
                        data.getScale().x(),
                        data.getScale().y(),
                        data.getScale().z()
                ),
                new JsonVec3f(
                        data.getTranslation().x(),
                        data.getTranslation().y(),
                        data.getTranslation().z()
                ),
                data.getShadowRadius(),
                data.getShadowStrength(),
                data.getBrightness() == null ? null : new JsonBrightness(
                        data.getBrightness().getBlockLight(),
                        data.getBrightness().getSkyLight()
                ),
                data.getBillboard()
        );
    }

    public static JsonTextHologramData textHologramDataToJson(TextHologramData data) {
        return new JsonTextHologramData(
                data.getText(),
                data.hasTextShadow(),
                data.isSeeThrough(),
                data.getTextAlignment(),
                data.getTextUpdateInterval(),
                data.getBackground() == null ? "" : "#"+Integer.toHexString(data.getBackground().asARGB())
        );
    }

    public static JsonBlockHologramData blockHologramDataToJson(BlockHologramData data) {
        return new JsonBlockHologramData(
                data.getBlock().name()
        );
    }

    public static JsonItemHologramData itemHologramDataToJson(ItemHologramData data) {
        return new JsonItemHologramData(
                new String(data.getItemStack().serializeAsBytes())
        );
    }

    public static JsonDataUnion toUnion(TextHologramData data) {
        JsonHologramData hologramData = hologramDataToJson(data);
        JsonDisplayHologramData displayHologramData = displayHologramDataToJson(data);
        JsonTextHologramData textHologramData = textHologramDataToJson(data);

        return new JsonDataUnion(
                hologramData,
                displayHologramData,
                textHologramData,
                null,
                null
        );
    }

    public static JsonDataUnion toUnion(ItemHologramData data) {
        JsonHologramData hologramData = hologramDataToJson(data);
        JsonDisplayHologramData displayHologramData = displayHologramDataToJson(data);
        JsonItemHologramData itemHologramData = itemHologramDataToJson(data);

        return new JsonDataUnion(
                hologramData,
                displayHologramData,
                null,
                itemHologramData,
                null
        );
    }

    public static JsonDataUnion toUnion(BlockHologramData data) {
        JsonHologramData hologramData = hologramDataToJson(data);
        JsonDisplayHologramData displayHologramData = displayHologramDataToJson(data);
        JsonBlockHologramData blockHologramData = blockHologramDataToJson(data);

        return new JsonDataUnion(
                hologramData,
                displayHologramData,
                null,
                null,
                blockHologramData
        );
    }

    public static HologramData fromJson(JsonDataUnion data) {
        Location loc = new Location(
                Bukkit.getWorld(data.hologram_data().location().world()),
                data.hologram_data().location().x(),
                data.hologram_data().location().y(),
                data.hologram_data().location().z()
        );
        Vector3f scale = new Vector3f(
                data.display_data().scale().x(),
                data.display_data().scale().y(),
                data.display_data().scale().z()
        );
        Vector3f translation = new Vector3f(
                data.display_data().translation().x(),
                data.display_data().translation().y(),
                data.display_data().translation().z()
        );

        Display.Brightness brightness = null;
        if (data.display_data().brightness() != null && data.display_data().brightness().sky_light() != null) {
            brightness = new Display.Brightness(
                    data.display_data().brightness().block_light(),
                    data.display_data().brightness().sky_light()
            );
        }

        HologramData hologramData = switch (data.hologram_data().type()) {
            case TEXT -> new TextHologramData(data.hologram_data().name(), loc)
                    .setText(data.text_data().text()) // text data
                    .setBackground(data.text_data().background_color())
                    .setTextAlignment(data.text_data().text_alignment())
                    .setTextShadow(data.text_data().text_shadow())
                    .setSeeThrough(data.text_data().see_through())
                    .setTextUpdateInterval(data.text_data().text_update_interval())
                    .setBillboard(data.display_data().billboard()) // display data
                    .setScale(scale)
                    .setTranslation(translation)
                    .setBrightness(brightness)
                    .setShadowRadius(data.display_data().shadow_radius())
                    .setShadowStrength(data.display_data().shadow_strength())
                    .setVisibilityDistance(data.hologram_data().visibilityDistance()) // hologram data
                    .setVisibility(data.hologram_data().visibility())
                    .setLinkedNpcName(data.hologram_data().linkedNpcName());

            case ITEM -> new ItemHologramData(data.hologram_data().name(), loc)
                    .setItemStack(ItemStack.deserializeBytes(data.item_data().item().getBytes())) // item data
                    .setBillboard(data.display_data().billboard()) // display data
                    .setScale(scale)
                    .setTranslation(translation)
                    .setBrightness(brightness)
                    .setShadowRadius(data.display_data().shadow_radius())
                    .setShadowStrength(data.display_data().shadow_strength())
                    .setVisibilityDistance(data.hologram_data().visibilityDistance()) // hologram data
                    .setVisibility(data.hologram_data().visibility())
                    .setLinkedNpcName(data.hologram_data().linkedNpcName());
            case BLOCK -> new BlockHologramData(data.hologram_data().name(), loc)
                    .setBlock(Material.getMaterial(data.block_data().block_material())) // block data
                    .setBillboard(data.display_data().billboard()) // display data
                    .setScale(scale)
                    .setTranslation(translation)
                    .setBrightness(brightness)
                    .setShadowRadius(data.display_data().shadow_radius())
                    .setShadowStrength(data.display_data().shadow_strength())
                    .setVisibilityDistance(data.hologram_data().visibilityDistance()) // hologram data
                    .setVisibility(data.hologram_data().visibility())
                    .setLinkedNpcName(data.hologram_data().linkedNpcName());
        };

        return hologramData;
    }
}
