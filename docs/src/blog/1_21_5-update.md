---
visibility: hidden
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

There are some new nature blocks added to the game:
- Bush
- Firefly Bush (when it's night, the fireflies will light up)
- Cactus Flower (can generate on cacti)
- Leaf Litter
- Wildflowers
- Short & Tall Dry Grass

New variants for some mobs based on biome:
- Chicken (Temperate, Cold, Warm)
- Cow (Temperate, Cold, Warm)
- Pig (Temperate, Cold, Warm)

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

### FancyHolograms

## Paper Hardfork :icon-repo-forked:

You can read about the reasons and changes in this article on the PaperMC forum: [The future of Paper - Hard fork](https://forums.papermc.io/threads/the-future-of-paper-hard-fork.1451/).
It basically allows the Paper team to work on adding support for new versions after without having to wait on Spigot to update. This means that Paper is able to also update to snapshots, pre-releases and release candidates.

This is perfect for us, because we can as well update our plugins faster to new versions and snapshots faster. Once the Paper team has updated to a new version (realase or snapshot), we can immediately start working on updating our plugins.


## Conclusion

We are excited about the new changes and the Paper hardfork. We are looking forward to updating our plugins to the new version and snapshots faster than before.