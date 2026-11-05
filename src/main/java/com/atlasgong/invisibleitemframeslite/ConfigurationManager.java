package com.atlasgong.invisibleitemframeslite;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Reads item metadata and shaped-recipe definitions from the plugin configuration.
 *
 * <p>This class centralizes YAML access and converts configured values into Bukkit/Adventure objects.
 * It does not save the configuration; the plugin lifecycle is responsible for creating the default file.</p>
 */
public class ConfigurationManager {

    private final Logger logger;
    private final FileConfiguration config;

    /**
     * @param logger plugin logger used to report invalid recipe configuration
     * @param config loaded Bukkit configuration
     */
    public ConfigurationManager(Logger logger, FileConfiguration config) {
        this.logger = logger;
        this.config = config;
    }

    /**
     * Supplies fallback values for one frame type. Existing user values are not overwritten.
     *
     * <p>The center ingredient is deliberately a configuration value rather than a hard-coded
     * material. Both regular and glow frame recipes default to a glass pane, while server
     * administrators can replace that material independently in {@code config.yml}.</p>
     *
     * @param id configuration identifier used below both {@code items} and {@code recipes}
     * @param type Bukkit material represented by this frame type
     */
    public void addConfigDefaults(String id, Material type) {
        config.addDefault("items." + id + ".name", "Invisible " + Utils.toTitleCase(type.name().toLowerCase().replace("_", " ")));
        config.addDefault("items." + id + ".enchantment_glint", true);
        config.addDefault("recipes." + id + ".count", 8);
        config.addDefault("recipes." + id + ".shape", Arrays.asList("FFF", "FAF", "FFF"));
        config.addDefault("recipes." + id + ".ingredients.F", "minecraft:" + type.name().toLowerCase());
        // Keep the center ingredient configurable; glass pane is the default for new configurations.
        config.addDefault("recipes." + id + ".ingredients.A", "minecraft:glass_pane");
    }

    /**
     * Loads the display metadata for one configured frame item.
     *
     * <p>MiniMessage is the preferred format. If a configured line contains the legacy section-sign
     * character, the legacy serializer is used for that line so older configurations continue to work.</p>
     *
     * @param itemId identifier under the {@code items} configuration section
     * @return immutable item metadata ready for the item registry
     * @throws IllegalArgumentException if the requested configuration section is absent
     */
    public ItemFrameData loadItemData(String itemId) {
        ConfigurationSection section = config.getConfigurationSection("items." + itemId);
        if (section == null) {
            throw new IllegalArgumentException("Missing configuration section for 'items." + itemId + "'.");
        }

        String name = section.getString("name");
        assert name != null;
        List<String> lore = section.getStringList("lore");
        boolean glint = section.getBoolean("enchantment_glint");

        LegacyComponentSerializer lcs = LegacyComponentSerializer.legacy('§');

        // Parse the display name using the format indicated by its contents.
        Component nameComponent;
        if (name.contains("§")) {
            nameComponent = lcs.deserialize(name);
        } else {
            nameComponent = MiniMessage.miniMessage().deserialize(name);
        }

        // Apply the same compatibility rule to each lore line independently.
        List<Component> loreComponents = lore.stream()
                .map(line -> {
                    if (line.contains("§")) {
                        return lcs.deserialize(line);
                    }
                    return MiniMessage.miniMessage().deserialize(line);
                })
                .toList();

        return new ItemFrameData(nameComponent, loreComponents, glint);
    }

    /**
     * Builds a Bukkit shaped recipe from the configured shape, ingredient map, and output count.
     *
     * <p>Each ingredient-map key is the single character used in the shape. Values are parsed as Bukkit
     * material names or namespaced material keys. Unknown materials are logged and skipped; Bukkit may
     * then reject an incomplete recipe during registration.</p>
     *
     * @param recipeKey unique plugin-scoped recipe identifier
     * @param itemId identifier under the {@code recipes} configuration section
     * @param result registered custom item used as the recipe output
     * @return configured recipe, or null when the shape is empty
     * @throws IllegalArgumentException if the requested recipe section is absent
     */
    public Recipe loadRecipe(NamespacedKey recipeKey, String itemId, ItemStack result) {
        ConfigurationSection section = config.getConfigurationSection("recipes." + itemId);
        if (section == null) {
            throw new IllegalArgumentException("Missing configuration section for 'recipes." + itemId + "'.");
        }

        // Never mutate the registry's canonical ItemStack when applying the configured output amount.
        ItemStack item = result.clone();
        int count = section.getInt("count");
        if (count == 0) {
            logger.severe("Recipe count of " + itemId + " is 0. Falling back to 1.");
            count = 1;
        }
        item.setAmount(count);

        ShapedRecipe recipe = new ShapedRecipe(recipeKey, item);
        List<String> shape = section.getStringList("shape");
        if (shape.isEmpty()) {
            logger.severe("Recipe shape is empty. Cannot continue.");
            return null;
        }
        recipe.shape(shape.toArray(new String[0]));

        ConfigurationSection ingredients = section.getConfigurationSection("ingredients");
        // Defaults are supplied by addConfigDefaults(); a missing section indicates malformed configuration.
        assert ingredients != null;
        for (Map.Entry<String, Object> entry : ingredients.getValues(false).entrySet()) {
            Material material = Material.matchMaterial(entry.getValue().toString());
            if (material == null) {
                logger.severe("Failed to find material '" + entry.getValue().toString() + "'. Skipping it and " +
                        "continuing.");
                continue;
            }
            recipe.setIngredient(entry.getKey().charAt(0), material);
        }

        return recipe;
    }
}
