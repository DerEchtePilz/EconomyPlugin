# EconomyPlugin v2.0.0
[![Join us on Discord](https://img.shields.io/discord/962686449038282753.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/Q7RRjdmERB)

## Table of contents
- [Information](#information)
- [How to use this plugin](#how-to-use-this-plugin)
- [Commands](#commands)
- [Changelogs](#changelogs)
- [What is planned for the future](#what-is-planned-for-the-future)
- [Unfinished features](#unfinished-features-in-v200)
- [Credits](#credits)

## Information
The EconomyPlugin is a plugin for Minecraft! It provides features such as
- Selling items
- Buying items
- A custom permission system
- A trade system
- A discord integration
- A friend system

## How to use this plugin
Using this plugin is pretty simple! Just put it in your server's `plugin` folder and start it.

When the server starts, the plugin will create a folder named `Economy` and within that file the
plugin will create the configuration file `config.json`!

Once the server has started, and you have joined you will be greeted with 50 coins as start balance.

This is given to every player joining the server for the first time. You can, however, increase the amount
of the start balance. More of that within the [Commands](#commands) section!

**!!!!!!  WARNING  !!!!!!**

DO NOT `/reload` YOUR SERVER! THIS **WILL** CAUSE ISSUES, EVEN IF YOU DON'T NOTICE! IF YOU WANT TO UPDATE A PLUGIN `/stop` YOUR SERVER, REPLACE THE `.jar` FILE AND START THE SERVER AGAIN!

## Commands
The EconomyPlugin provides many commands:

| Command        | Description                                                                 | Permission                               |
|----------------|-----------------------------------------------------------------------------|------------------------------------------|
| `/createoffer` | Create an offer which other people can buy                                  | create_offer                             |
| `/canceloffer` | Cancel an offer you created to keep it instead of selling it                | cancel_offer                             |
| `/buy`         | Buy items other people offered                                              | buy_offer                                |
| `/givecoins`   | Give yourself or another player coins                                       | give_coins                               |
| `/takecoins`   | Delete coins from your or another person's bank account                     | take_coins                               |
| `/setcoins`    | Set the coins on your bank account or on the bank account of another player | set_coins                                |
| `/permission`  | Manage permissions                                                          | OP                                       |
| `/config`      | Manage config values or reset the config                                    | modify_config / reset_config             |
| `/trade`       | Trade with other players                                                    | trade                                    |
| `/economyhelp` | Displays every EconomyPlugin command available                              | NONE                                     |
| `/discord`     | Send private messages to players from the discord server                    | discord_search_id / discord_message_user |
| `/friend`      | Manage your friends                                                         | friend                                   |

Command syntax:
- /createoffer <minecraft_item_id> \<config:amount> \<config:price>
- /canceloffer
- /buy [<minecraft_item_id>|special|player]
- /givecoins \<amount> [\<player>]
- /takecoins \<amount>
- /setcoins \<player> \<amount>
- /permission <set|remove|get> \<player> \<permissions>
- /config <interest|itemPrice|itemQuantities|language|reload|reset|startBalance|discord> [\<value>|<minAmount|maxAmount>]
- /trade \<player> \<accept|deny>
- /economyhelp
- /discord (msg|searchId) \<search:query|msg:user_id> [msg:message]
- /friend (add|remove|list|accept|deny) [player]

Usage:
- If something is in `[]` that means that this value is (most of the time) optional
- If something is in `<>` that means that this value is variable and can be any supported value (still has to match command syntax)
- If something has the prefix `config:` that means that these values are dependant from what is in the config

## Changelogs
<table width="100%">
  <thead>
    <tr>
      <th width="10%">Version</th>
      <th width="10%">Minecraft Versions</th>
      <th width="80%">Features</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td valign="top"><b>2.0.0</b></td>
      <td valign="top">1.13 - 1.19</td>
      <td valign="top">
        <p>New Features:</p>
        <ul>
          <li>Added discord integration</li>
          <li>Added german language support</li>
          <li>Added friend system</li>
          <li>Added update checker</li>
          <li>Added permission groups</li>
          <li>Added support for custom permission groups</li>
          <li>Added cooldowns for features like accepting a friend or trade request</li>
        </ul>
        <p>Github changes:</p>
        <ul>
          <li>Added issue templates to use when submitting a bug report or a feature request</li>
        </ul>
        <p>Plugin changes</p>
        <ul>
          <li>Plugin name now contains the plugin version</li>
          <li>Java version change: 17 => 16</li>
        </ul>
        <p>Bug fixes</p>
        <ul>
          <li>Cancel offer menu and buy offer menu are filled wrong</li>
          <li>Fixes <code>NullPointerException</code> that could occur when loading the plugin on a not supported version</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td valign="top"><b>1.1.2</b></td>
      <td valign="top">1.13 - 1.19</td>
      <td valign="top">
        <ul>
          <li>Fixes trade system issue where you could trade with yourself causing item duplication when cancelling the trade</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td valign="top"><b>1.1.1</b></td>
      <td valign="top">1.13 - 1.19</td>
      <td valign="top">
        <ul>
          <li>Fixes trade system issue where items would not be given back when the trade menu gets closed</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td valign="top"><b>1.1.0</b></td>
      <td valign="top">1.13 - 1.19</td>
      <td valign="top">
        <ul>
          <li>Added <code>/trade</code> command</li>
          <li>Added <code>/economyhelp</code> command</li>
          <li>Added trade system</li>
          <li>Added a discord integration (<b>BETA, won't receive support for this</b>)</li>
          <li>Removed <code>/fallback</code> command</li>
          <li>Many bug fixes</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td valign="top"><b>1.0.0</b></td>
      <td valign="top">Version independent</td>
      <td valign="top">
        <ul>
          <li>Added multiple commands</li>
          <li>Added basic economy structure</li>
        </ul>
      </td>
    </tr>
  </tbody>
</table>

## What is planned for the future?
- Further development of the discord integration
- Still going away from a pure economy plugin
- Completing the [wiki](https://github.com/DerEchtePilz/EconomyPlugin/wiki)
  - This should list features and how they work
  - Current features (that probably have to be added):
    - Being able to sell items
    - Being able to cancel an offer
    - Being able to buy items
    - Being able to give coins
    - Being able to take coins
    - Being able to set coins
    - Being able to manage permissions
    - Being able to change config values
    - Being able to trade with players
    - Being able to have a discord integration
    - Being able to have friends

## Unfinished features in v2.0.0
- Discord integration (permanent development because of lack of ideas)
- The wiki... _I should really write it_

## Credits
- A huge "thank you" goes to [@JorelAli](https://jorel.dev/) who did not directly contribute to this plugin but created the [CommandAPI](https://github.com/JorelAli/CommandAPI) which is used in this plugin!