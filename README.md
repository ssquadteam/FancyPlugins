# FancyPlugins

This repository contains a collection of plugins, libraries and tools for Minecraft servers.

The monorepo is still work in progress and will be expanded over time.

## Packages

Plugins:
- fancyholograms (v3): create fancy looking holograms
- fancyvisuals: plugin to customize all visual components of the game

Libraries:
- common: common classes and utilities
- packets: packet handling library
- jdb: json database library
- plugin-tests: testing library for plugins

Tools:
- deployment: deploy plugins to platforms like modrinth
- quick-e2e: generate a quick end-to-end environment for testing

Docs: documentation for all plugins, libraries and tools.

Future packages that are planned to be added to the monorepo:
- fancynpcs: https://github.com/FancyMcPlugins/FancyNpcs
- fancywebsite: https://github.com/FancyMcPlugins/FancyWebsite
- (fancymorphs: https://github.com/FancyMcPlugins/FancyMorphs)

## Usage

The monorepo uses Gradle as build system. See [monorepo.md](docs/src/development-guidelines/monorepo.md) for more information.

To see specific usage for each package, see the README.md in the respective package directory.