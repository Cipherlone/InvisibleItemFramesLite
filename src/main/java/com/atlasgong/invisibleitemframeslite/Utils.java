package com.atlasgong.invisibleitemframeslite;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.stream.Collectors;

/** Stateless helper methods shared by the plugin's listeners and configuration code. */
public class Utils {

    /** Prevent utility-class instantiation. */
    private Utils() {
        throw new AssertionError("Utils should not be instantiated.");
    }

    /**
     * Tests whether an inventory item is one of this plugin's custom invisible-frame items.
     *
     * <p>The marker is a plugin-namespaced byte in the item's persistent data container. Checking the key
     * rather than the material or display name prevents ordinary frames and similarly named items from
     * being mistaken for plugin items.</p>
     *
     * @param item item to test; may be null
     * @param isInvisibleKey plugin-owned persistent-data key
     * @return true when the item carries the plugin marker
     */
    public static boolean isInvisibleItemFrame(ItemStack item, NamespacedKey isInvisibleKey) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(isInvisibleKey, PersistentDataType.BYTE);
    }

    /**
     * Tests whether a placed entity is an item frame carrying this plugin's marker.
     *
     * <p>Item metadata is not copied onto the placed entity automatically, so the placement listener
     * explicitly writes the marker to the entity's persistent data container.</p>
     *
     * @param entity entity to test; may be null or any entity type
     * @param isInvisibleKey plugin-owned persistent-data key
     * @return true only for a marked regular or glow item frame
     */
    public static boolean isInvisibleItemFrame(Entity entity, NamespacedKey isInvisibleKey) {
        return entity instanceof ItemFrame && entity.getPersistentDataContainer().has(isInvisibleKey,
                PersistentDataType.BYTE);
    }

    /**
     * Capitalizes the first character of each space-separated word.
     *
     * <p>This is intended for human-readable names derived from Bukkit material names, not locale-aware
     * title casing or arbitrary natural-language text.</p>
     *
     * @param input input string
     * @return words joined by single spaces with their first characters capitalized
     */
    public static String toTitleCase(String input) {
        return Arrays.stream(input.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
