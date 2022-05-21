# EconomyPlugin v1.0.0
[![Join us on Discord](https://img.shields.io/discord/962686449038282753.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/Q7RRjdmERB)

## Table of contents
- [Information](#information)
- [How to use this plugin](#how-to-use-this-plugin)
- [Commands](#commands)
    - [Which commands do exist](#which-commands-do-exist)
    - [Command syntax](#command-syntax)
    - [What does what mean](#what-does-what-mean)
- [What is planned for the future](#what-is-planned-for-the-future)
- [Unfinished features](#unfinished-features-in-v100)
- [Credits](#credits)

## Information
The EconomyPlugin is a plugin for Minecraft! It provides features such as
- Selling items
- Buying items
- A custom permission system

While it may not be much this version, it was quite time-consuming programming all this 
because features like bank accounts and commands took a lot of time!

## How to use this plugin
Using this plugin is pretty simple! Just put it in your server's `plugin` folder and start it.

When the server starts, the plugin will create a folder named `Economy` and within that file the
plugin will create the configuration file `config.json`!

Once the server has started, and you have joined you will be greeted with 50 coins as start balance.

This is given to every player joining the server for the first time. You can, however, increase the amount
of the start balance. More of that within the [Commands](#commands) section!

## Commands
The EconomyPlugin provides many commands. And they are special because they have been registered as Minecraft commands which means you can execute them with `minecraft:commandname`. This was done using the [CommandAPI](https://github.com/JorelAli/CommandAPI/).

#### Which commands do exist?
- `/createoffer`  Create an offer which other people can buy (Permission: create_offer)
- `/canceloffer`  Cancel an offer you created to keep it instead of selling it (Permission: cancel_offer)
- `/buy`  Buy items other people offered (Permission: buy_offer)
- `/givecoins`  Give yourself or another player coins (Permission: give_coins)
- `/takecoins` Delete coins from your bank account (Permission: take_coins)
- `/setcoins` Set the coins on your bank account or on the bank account of another player (Permission: set_coins)
- `/permission` Manage permissions (Permission: OP)
- `/config` Manage config values (Permission: modify_config, reset_config)

Additionally, one more command `/fallback` was created because the CommandAPI does only support Minecraft 1.16.5 - 1.18.2. When the plugin detects a version below that it will stop registering
CommandAPI commands but instead will enable `/fallback`.

#### Command syntax:
- /createoffer <minecraft_item_id> \<config:amount> \<config:price>
- /canceloffer
- /buy [<minecraft_item_id>|special|player]
- /givecoins \<amount> [\<player>]
- /takecoins \<amount>
- /setcoins \<player> \<amount>
- /permission <set|remove|get> \<player> \<permission>
- /config <interest|itemPrice|itemQuantities|language|reload|reset|startBalance> [\<value>|<minAmount|maxAmount>]

#### What does what mean:
- If something is in `[]` that means that this value is (most of the time) optional
- If something is in `<>` that meas that this value is variable and has no tab completion
- If something has the prefix `config:` that means that these values are dependant from what is in the config

## What is planned for the future?
- More player-specific stuff
- Making the plugin modular (I do not want to tell more for now, but I think it will be great)
- Going away from a pure EconomyPlugin as there are many good ones

## Unfinished features in v1.0.0
- Multiple language support => Currently the only language that has translations is English

## Credits
- A huge "thank you" goes to [@JorelAli](https://jorel.dev/) who did not directly contribute to this plugin but created the [CommandAPI](https://github.com/JorelAli/CommandAPI) which is used in this plugin!