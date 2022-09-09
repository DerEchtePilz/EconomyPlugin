# EconomyPlugin v3.0.0
[![Join us on Discord](https://img.shields.io/discord/962686449038282753.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/Q7RRjdmERB)

## Table of contents
- [Information](#information)
- [Changelogs](#changelogs)
- [What is planned for the future](#what-is-planned-for-the-future)
- [Unfinished features](#unfinished-features-in-v300)
- [Frameworks used](#frameworks-used)

## Information
The EconomyPlugin is a plugin for Minecraft! It provides these features:
- Selling items
- Buying items
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
- Unsure, but definitely want to bring back most (but not all) 2.0.0 features

## Unfinished features in v3.0.0
- The auction system enhancements
- Not yet implemented: the friend system

## Frameworks used
- [CommandAPI](https://github.com/JorelAli/CommandAPI) by [@JorelAli](https://jorel.dev/)