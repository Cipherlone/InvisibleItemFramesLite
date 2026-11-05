package com.atlasgong.invisibleitemframeslite;

import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * Immutable configuration snapshot used to construct an invisible item-frame item.
 *
 * @param name display name component, parsed from MiniMessage or legacy formatting
 * @param lore ordered lore components; an empty list means no lore
 * @param glint whether the item's enchantment glint override is enabled
 */
public record ItemFrameData(Component name, List<Component> lore, boolean glint) {
}
