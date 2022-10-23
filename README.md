# EconomyPlugin v3.1.0
[![Join us on Discord](https://img.shields.io/discord/962686449038282753.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/Q7RRjdmERB)

## Table of contents
- [Information](#information)
- [Changelogs](#changelogs)
- [What is planned for the future](#what-is-planned-for-the-future)
- [Unfinished features](#unfinished-features-in-v310)
- [Frameworks used](#frameworks-used)

## Information
The EconomyPlugin is a plugin for Minecraft! It provides these features:
- Selling items
- Buying items
- A friend system
- A custom permission system

## Changelogs
<table width="100%">
  <thead>
    <tr>
      <th width="10%">Version</th>
      <th width="20%">Minecraft Versions</th>
      <th width="70%">Features</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td valign="top"><b>3.1.0</b></td>
      <td valign="top">1.13 - 1.19.2</td>
      <td valign="top">
        <p>Plugin Changes</p>
        <ul>
            <li>Made it possible to modify the interest rate, the minimum required days for interest and the start balance</li>
            <li>Added command: <code>/economy coins <b>baltop</b></code></li>
            <li>Changed <code>offers</code> subcommand to <code>auction</code></li>
            <li>Made it possible to sell items with meta on it (e.g. Enchantments, damaged items) without that data being lost <i>(an actual new feature that hasn't been seen before in this plugin!!)</i></li>
        </ul>
        <p>Re-added features</p>
        <ul>
            <li>Added the friend system</li>
        </ul>
        <p>Technical Changes</p>
        <ul>
            <li>Moved balances from PersistantDataContainers to a database</li>
        </ul>
        <p>Bug Fixes</p>
        <ul>
            <li>Fixed a bug where the customer would get the earned coins if the seller was online</li>
            <li>Removed debug messages that were left over from 3.0.0 testing</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td valign="top"><b>3.0.1</b></td>
      <td valign="top">1.13 - 1.19.2</td>
      <td valign="top">
        <p>Bug Fixes</p>
        <ul>
            <li>Fixed the update system -> now checks for the correct version and throws no errors</li>
            <li>Nothing else :) Will continue with more 2.0.0 features when I have the time</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td valign="top"><b>3.0.0</b></td>
      <td valign="top">1.13 - 1.19.2</td>
      <td valign="top">
        <ul>
          <li>Improved auction system (now has a duration after which items expire)</li>
          <li>Removed discord integration (wasn't quite going the way I wanted)</li>
          <li>Removed multiple language support (now English only)</li>
        </ul>
        <p><b>Features not yet implemented, but planned to be re-added:</b></p>
        <ul>
          <li>Permission groups</li>
          <li>Friend system</li>
        </ul>
      </td>
    </tr>
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
After being almost finished with re-adding version 2.0.0 features, I do not have a specific roadmap.

The only missing 2.0.0 feature are permission groups, which are planned to be added in version 3.2.0.

In [this](#unfinished-features-in-v310) section it is stated, that auction system enhancements are missing. I do plan to add some, but I do not really have an idea yet, which is why I will put them in 3.2.0 as well.

Furthermore, I want to utilize my new InventoryAPI I have made which could allow for faster inventory changes in the future. Inventories affected from this would be the auction menu and the cancel menu.

## Unfinished features in v3.1.0
- The auction system enhancements

## Frameworks used
- [CommandAPI](https://github.com/JorelAli/CommandAPI) by [@JorelAli](https://jorel.dev/)