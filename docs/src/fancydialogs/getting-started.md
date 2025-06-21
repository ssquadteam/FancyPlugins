---
order: 10
icon: info
---
# Getting started

!!!danger
Be aware, that only Paper and Folia is supported, but the plugin should work on any of its forks (like Purpur or Pufferfish). Spigot, Bukkit, Sponge and Fabric is not supported.
!!!

## Installation

To install FancyDialogs, you need to download the latest version from one of the following sources:

[!button size="s" icon="download" iconAlign="left" text="Modrinth" target="blank"](https://modrinth.com/plugin/fancydialogs/versions)

[!button size="s" icon="download" iconAlign="left" text="Hangar" target="blank"](https://hangar.papermc.io/Oliver/FancyDialogs/versions)

After downloading the plugin, you can install it by placing the downloaded file in the `plugins` folder of your server.

Restart your server and you are ready to go!

To check if the plugin is installed correctly, you can use the command `/fancydialogs version`. If the plugin is installed correctly, it will show you the version of the plugin.

## Create your first dialog

FancyDialogs come with some default dialogs, which you can use to get started. You can find them in the `plugins/FancyDialogs/data/dialogs` folder. 
You can also create your own dialogs by creating a new file in this folder.

FancyDialogs uses a simple JSON format to define dialogs. Here is an example of a simple dialog:

```json
{
  "id": "my_dialog",
  "title": "My Fancy Dialog",
  "canCloseWithEscape": true,
  "body": [
    { "text": "This is my first dialog created with FancyDialogs!" }
  ],
  "inputs": {
    "textFields": [
      {
        "key": "fav_color",
        "order": 1,
        "label": "<color:#ff7300>What is your favorite color?</color>",
        "placeholder": "gold",
        "maxLength": 50,
        "maxLines": 1
      }
    ]
  },
  "buttons": [
    {
      "label": "Show favorite color",
      "tooltip": "Click to show your favorite color",
      "actions": [
        {
          "name": "message",
          "data": "Your favorite color is: <color:{fav_color}>{fav_color}</color>"
        }
      ]
    }
  ]
}
```

The fields in this dialog are as follows:

- `id`: The unique identifier for the dialog, which is used to reference the dialog in commands or other dialogs
- `title`: The title of the dialog, which is displayed at the top of the dialog (supports MiniMessage & PlaceholderAPI)
- `canCloseWithEscape`: Whether the dialog can be closed with the Escape key (default: true)
- `body`: The body of the dialog, which can contain text (and items soon)
  - `text`: The text to display in the body of the dialog (supports MiniMessage & PlaceholderAPI)
- `inputs`: The inputs of the dialog, which can contain text fields
  - `textFields`: A list of text fields that can be used to collect input from the player
    - `key`: The key to use to store the input value (can be used as a placeholder in actions)
    - `order`: The order of the text field in the dialog
    - `label`: The label to display above the text field (supports MiniMessage & PlaceholderAPI)
    - `placeholder`: The initial text to display in the text field
    - `maxLength`: The maximum length of the input
    - `maxLines`: The maximum number of lines for the input (greater than 1 will create a multiline text field)
- `buttons`: The buttons of the dialog, which can contain text, tooltips and actions
  - `label`: The text to display on the button (supports MiniMessage & PlaceholderAPI)
  - `tooltip`: The tooltip to display when hovering over the button (supports MiniMessage & PlaceholderAPI)
  - `actions`: The actions to perform when the button is clicked
    - `name`: The name of the action, which can be one of the following:
      - `message`: Sends a message to the player
      - `console_command`: Executes a command as the console
      - `player_command`: Executes a command as the player
      - `open_dialog`: Opens another dialog
      - `send_to_server`: Sends the player to another server (requires BungeeCord or Velocity)
    - `data`: The data for the action, which depends on the action type (e.g. for `message`, it is the message to send or for `open_dialog`, it is the ID of the dialog to open)