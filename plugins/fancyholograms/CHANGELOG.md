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

## New Commands

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

## New API

## Traits

Traits are a new feature in v3 that allow you to extend the functionality of holograms.
A trait is basically a plugin that can be added to a hologram to add new features or functionality.
Traits have access to the hologram's data and lifecycle, allowing them to modify the hologram's behavior or appearance.
Each trait can have its own configuration and can be enabled or disabled individually.

There are several built-in traits that come with FancyHolograms v3:

- `multiple_pages_trait`: Allows you to create multiple pages in a hologram
- `interaction_trait`: Allows you to make clickable holograms (using FancyNpcs)
- `file_content_trait`: Allows you to display the content of a file in a hologram

You can also create your own traits by extending the `HologramTrait` class. 
View the Javadocs for more information about how the `HologramTrait` class is structured.
You can override all the `on` methods, as well as the `load` and `save` methods.
