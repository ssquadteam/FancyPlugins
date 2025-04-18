---
icon: dot
order: 500
---

# Minecraft Servers

This guide will help you set up FancyAnalytics on your Minecraft server. The plugin already has many default metrics.

## Installation

To install FancyAnalytics on your server, you need to follow these steps:

1. Download the latest release from the [download page](https://fancyanalytics.net/downloads)
2. Put the downloaded jar file in the ``plugins`` folder of your server
3. Restart your server
4. Run the command `/fancyanalytics version` to confirm that the plugin is installed

## Configuration

To use FancyAnalytics, you need to create an account on the [FancyAnalytics website](https://fancyanalytics.net/register) and create a new project. 
After creating the project, you need to put the project ID and the API key in the `config.yml` file of the plugin.

```yaml
project_id: "your project id"
api_key: "your api key",
enable_default_events: false
```

## Usage

All you need to do is wait for the data to be collected, and then you can see it on the website. This can take up to 2 minutes.
