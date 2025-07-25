## How to update

v3 is basically a complete rewrite of the plugin. 
It is not compatible with v2 and earlier versions.
Not everything will be migrated automatically, so you will have to do some manual work.

Things that will be migrated automatically:
- Hologram data (holograms.yml)

Things that will **not** be migrated automatically:
- All configurations from config.yml
- All configurations from featureFlags.yml

The API has changed significantly, so you will have to update your code accordingly.
Read more about the new API [here](#new-api).

**Important**: We've decided to drop support for the following Minecraft versions: 1.19.4 - 1.20.4! FancyHolograms v3 will only support Minecraft 1.20.5 and newer.

## New Commands & Translations

In v3, all commands have been rewritten and simplified.
If you're using FancyNpcs, you will notice that the commands are now more consistent with the FancyNpcs commands.
It's now using the [Lamp command framework](https://github.com/Revxrsal/Lamp), which allows for better command handling and easier command creation.

Not only the command structure has changed, but also the responses.
All responses are now configurable. You can find the default responses in the `plugins/FancyHolograms/languages/default.yml` file.
You can create your own language files in the `plugins/FancyHolograms/languages/` folder, by copying the `default.yml` file and renaming it to your desired language code (e.g. `nl.yml`, `pl.yml`, etc.).
Remember to set the `language` option in the `config.yml` file to your language code.

## New Storage System

The storage system has been completely rewritten.
It now uses a more efficient and flexible system that allows for better performance and easier management of holograms.

The file format is now JSON instead of YAML, which allows for better compatibility with other systems and easier parsing.

Many of you are annoyed by the fact that the hologram data was stored in a single file (`holograms.yml`).
If you had a lot of holograms, this file could become huge and difficult to manage.
This has been changed in v3.

The hologram data is no longer stored in a single file, but in multiple files.
The new place for hologram data is in the `plugins/FancyHolograms/data/holograms/` folder.
In this folder, you can create subfolders to organize your holograms.
Every JSON file in this folder will be loaded (recursively).
Each JSON file can contain multiple holograms, which allows for better organization and management of holograms.

Example folder structure:

- `plugins/FancyHolograms/data/holograms/`
  - `rules-hologram.json`
  - `info-hologram.json`
  - `spawn-holograms/`
    - `shop-holograms.json`
    - `quest-holograms.json`
    - `crates/`
      - `crate1.json`
      - `crate2.json`
      - `crate3.json`
  - `event-holograms/`
    - `event1.json`
    - `event2.json`
    - `event3.json`

Looking at one JSON file, it will look like this:

```json
[
    {
      first hologram ...
    },
    {
      second hologram ...
    }
]
```

With each hologram containing the following "components":

```json
{
  "hologram_data": { ... },
  "display_data": { ... },
  "text_data": { ... },
}
```

If the hologram is a block or item hologram, it will have the `block_data` or `item_data` component instead of `text_data`.

The `hologram_data` component contains the basic information about the hologram, such as its name, location and visibility.
The `display_data` component contains the display settings for the hologram, such as its scale, shadow and billboard.
The `text_data` component contains the text settings for the hologram, such as the text itself, text alignment and background color.

## New Configuration

**Disclaimer:** all configuration options do not migrate automatically. You will have to update your configuration files manually.

The feature flags are now stored in the `config.yml` too. You can find them under the `experimental_features` section.

## New API

TODO

## Traits

**Disclaimer:** the trait system is still in beta and may change in the future.

Traits are a new feature in v3 that allows you to extend the functionality of holograms.
A trait is basically a plugin that can be added to a hologram to add new features or functionality.
Traits have access to the hologram's data and lifecycle, allowing them to modify the hologram's behavior or appearance.
Each trait can have its own configuration and can be enabled or disabled individually.

There are several built-in traits that come with FancyHolograms v3:

- `multiple_pages_trait`: Allows you to create multiple pages in a hologram
- `interaction_trait`: Allows you to make clickable holograms (using FancyNpcs)
- `file_content_trait`: Allows you to display the content of a file in a hologram

You can also create your own traits by extending the `HologramTrait` class. 
View the [Javadocs]() for more information about how the `HologramTrait` class is structured.
You can override all the `on` methods, as well as the `load` and `save` methods.

Every trait has a `storage` JDB (JSON Database) object that can be used to store data related to the trait.
The JDB will save the data to the `plugins/FancyHolograms/data/traits/<trait name>/` folder, all paths are relative to this folder.
You can read more about the JDB [here]().
