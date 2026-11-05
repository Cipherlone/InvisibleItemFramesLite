# InvisibleItemFramesLite

> Forked from [InvisibleItemFrames](https://github.com/tiffany352/InvisibleItemFrames) by [Tiffany Bennett (Tiffnix)](https://tiffnix.com/contact).

A fast, minimal Paper plugin that adds **craftable invisible item frames** to your server; no configuration required, no commands or permissions, just drop it in and go.

See a [video demo](https://youtu.be/2kLfRHyH5cc), and the [gallery](https://modrinth.com/plugin/invisibleitemframes/gallery).

<table width="100%">
  <tr>
    <th style="text-align: center;">Item Frame Demo</th>
    <th style="text-align: center;">Crafting Recipe</th>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/0672ce90-f42c-4f5d-8b7d-fe04adf94f1d" alt="Item Frame Demo" width="100%">
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/04ab7b6a-1c56-4f89-805d-cde3a6230389" alt="Crafting Recipe" width="100%"><br>
      This crafting recipe is configurable!
    </td>
  </tr>
</table>

## Features

- Adds invisible item frames you can craft and place like normal
- Frames are visible when empty, and automatically turn invisible once an item is added
- Allows for container pass-through (you can still sneak to rotate items)
- Perfect for clean builds, displays, or shops without visual clutter

## Compatibility

This development branch targets **Paper 26.3** and **Java 25**. The plugin uses Paper's API and is not intended to run on older server versions unless separately tested.

## Why This Version?

This is an updated, stripped-down fork of the original [InvisibleItemFrames](https://www.spigotmc.org/resources/invisible-item-frames.85365/) plugin. Compared to the original:

- Lightweight performance; it's tiny, fast, and focused
- Works right out of the box; no additional configuration is needed
- Compatibility with newer Paper versions
- Bug fixes and maintenance updates

No command trees. No permission setup. Just craftable invisible item frames that feel like they could have shipped natively with vanilla.

## Installation

1. Download the plugin JAR from [Modrinth](https://modrinth.com/plugin/invisibleitemframes)
2. Drop the plugin JAR into your server's `plugins/` folder
3. Restart your server
4. Craft your item frames

## Configuration

The default configuration provides recipes for regular and glow invisible item frames. Item names, optional lore, enchantment glint, recipe shapes, ingredients, and output counts can be configured in `config.yml`. MiniMessage is supported for display text; legacy section-sign formatting remains supported for existing configurations.

## Migrating from v2 to v3

Server admins should update legacy chat formatting to Adventure's MiniMessage format: https://docs.advntr.dev/minimessage/format. Alternatively, delete the old config file so a fresh default config is generated on the next server startup.

## Development

Prerequisites:

- JDK 25
- Maven

Build and run tests with:

```bash
mvn clean verify
```

Test the resulting plugin JAR on a Paper 26.3 test server before deploying it to a production server. A successful compile alone does not replace in-game testing.

## Contributing

Code is licensed under MPLv2, as per our [upstream](https://github.com/tiffany352/InvisibleItemFrames/blob/main/LICENSE-MPL). See [LICENSE-MPL](./LICENSE-MPL) for details. Contributions are assumed to be under this license.

## bStats Metrics

<img src="https://bstats.org/signatures/bukkit/InvisibleItemFramesLite.svg" alt="bStats Metrics" width="100%">
