---
visibility: public
authors:
- name: Oliver Schlüter
  email: oliver@fancyplugins.de
  link: https://github.com/OliverSchlueter
  avatar: https://avatars.githubusercontent.com/u/79666085?v=4

description: The reasons why we switched to a monorepo for our Minecraft plugins and libraries.
---

![](../static/1_21_5-update.png)

# 1.21.5 Update

In this article, we will talk about the changes in the 1.21.5 update, what that means for our plugins and how we will benefit from the Paper hardfork.

## Changes in 1.21.5 :icon-log:

### Game changes

**New Blocks and Features**

- Vegetation: Introduction of Firefly Bush, Leaf Litter, Wildflowers, Bush, Short Dry Grass, Tall Dry Grass, and Cactus Flower blocks, enhancing the natural diversity of various biomes. ￼
- Fallen Trees: New fallen tree generation adds to the realism of forested areas. ￼
- Ambient Sounds: Desert and Badlands biomes now feature new ambient block sounds, providing a more immersive environment.

**Farm Animals Variants**

Pigs, Cows, and Chickens now have warm and cold variants, determined by the biome in which they spawn. For instance, cold variants appear in biomes like Frozen Peaks and Snowy Plains, while warm variants are found in Deserts and Jungles. When bred, offspring randomly inherit one of the parent’s variants, irrespective of the current biome. Additionally, Chickens lay new types of eggs corresponding to their variants: Blue Eggs for cold Chickens and Brown Eggs for warm Chickens.

**Sheep Wool Color** 

Sheep wool color now depends on the biome:
- Temperate biomes: Commonly white; uncommon colors include black, gray, light gray, and brown; rare chance for pink.
- Cold biomes: Commonly black; uncommon colors are gray, light gray, white, and brown; rare chance for pink.
- Warm biomes: Commonly brown; uncommon colors are black, white, gray, and light gray; rare chance for pink.

**Spawn Eggs**

Visuals for Spawn Eggs have been overhauled to improve readability and accessibility, with designs reflecting the personality and size of the respective mobs.

### Packet changes

Not much has changed in the packet system.

Removed packets:
- `ClientboundAddExperienceOrb`

Added packets:
- `ClientboundTestInstanceBlocksStatus`
- `ServerboundSetTestBlock`
- `ServerboundTestInstanceBlockAction`

## Changes in the plugins :icon-megaphone:

### FancyNpcs

FancyNpcs now supports 1.21.5. Update to version [2.5.0](https://modrinth.com/plugin/fancynpcs/version/2.5.0) to get the new features and changes.

**New Attributes:**

| Npc Type | Attr. Name | Possible Values       |
|----------|------------|-----------------------|
| Pig      | variant    | temperate, cold, warm |
| Cow      | variant    | temperate, cold, warm |
| Chicken  | variant    | temperate, cold, warm |


### FancyHolograms

FancyHolograms now supports 1.21.5. Update to version [2.5.0](https://modrinth.com/plugin/fancyholograms/version/2.5.0) to get the new features and changes.

There are no other changes in FancyHolograms.

## Paper Hardfork :icon-repo-forked:

You can read about the reasons and changes in this article on the PaperMC forum: [The future of Paper - Hard fork](https://forums.papermc.io/threads/the-future-of-paper-hard-fork.1451/).
It basically allows the Paper team to work on adding support for new versions after without having to wait on Spigot to update. This means that Paper is able to also update to snapshots, pre-releases and release candidates.

This is perfect for us, because we can as well update our plugins faster to new versions and snapshots faster. Once the Paper team has updated to a new version (realase or snapshot), we can immediately start working on updating our plugins.


## Conclusion

We are excited about the new changes and the Paper hardfork. We are looking forward to updating our plugins to the new version and snapshots faster than before.