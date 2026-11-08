package com.atlasgong.invisibleitemframeslite;

import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameBreakListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameCraftListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameInteractionListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFramePlaceListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin entry point.
 *
 * <p>Initializes the plugin-scoped persistent-data and recipe keys, loads configurable item metadata,
 * registers crafting recipes and event listeners, and starts bStats metrics.</p>
 *
 * <p>The invisible marker is stored on both the custom frame item and the placed entity. The item marker
 * identifies frames during placement/crafting; the entity marker allows later interaction and break
 * events to recognize the placed frame.</p>
 */
public class InvisibleItemFramesLite extends JavaPlugin {

    private static boolean firstLoad = true;

    /** @return the persistent-data key used to identify custom invisible frame items and entities */
    public NamespacedKey getInvisibleKey() {
        return IS_INVISIBLE_KEY;
    }

    /** @return the key for the configurable regular-frame crafting recipe */
    public NamespacedKey getRegularRecipeKey() {
        return REGULAR_RECIPE_KEY;
    }

    /** @return the key for the configurable glow-frame crafting recipe */
    public NamespacedKey getGlowRecipeKey() {
        return GLOW_RECIPE_KEY;
    }

    /** @return the key for converting a regular invisible frame into a glow frame */
    public NamespacedKey getShapelessGlowRecipeKey() {
        return SHAPELESS_GLOW_RECIPE_KEY;
    }

    private final NamespacedKey IS_INVISIBLE_KEY;
    private final NamespacedKey REGULAR_RECIPE_KEY;
    private final NamespacedKey GLOW_RECIPE_KEY;
    private final NamespacedKey SHAPELESS_GLOW_RECIPE_KEY;

    /** Creates the stable, plugin-namespaced identifiers used throughout this plugin. */
    public InvisibleItemFramesLite() {
        super();
        IS_INVISIBLE_KEY = new NamespacedKey(this, "invisible");
        REGULAR_RECIPE_KEY = new NamespacedKey(this, "invisible_item_frame");
        GLOW_RECIPE_KEY = new NamespacedKey(this, "invisible_glow_item_frame");
        SHAPELESS_GLOW_RECIPE_KEY = new NamespacedKey(this, "invisible_glow_item_frame_shapeless");
    }

    /**
     * Enables the plugin in dependency order: listeners, configuration, item templates, recipes, and metrics.
     * The registered item templates are shared by crafting and break-drop handling.
     */
    @Override
    public void onEnable() {
        firstLoad = false;

        // Register listeners before gameplay interactions can occur.
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ItemFramePlaceListener(IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameBreakListener(IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameInteractionListener(IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameCraftListener(IS_INVISIBLE_KEY), this);

        // Ensure a default config exists, then supply defaults for any missing item/recipe entries.
        saveDefaultConfig();
        ConfigurationManager cm = new ConfigurationManager(getLogger(), getConfig());
        cm.addConfigDefaults("invisible_item_frame", Material.ITEM_FRAME);
        cm.addConfigDefaults("invisible_glow_item_frame", Material.GLOW_ITEM_FRAME);

        // Build canonical item templates from config. The registry returns clones so callers cannot
        // accidentally mutate the stored templates when changing stack amounts or adding recipe data.
        ItemFrameRegistry registry = ItemFrameRegistry.getInstance();
        ItemFrameData regData = cm.loadItemData("invisible_item_frame");
        ItemFrameData glowData = cm.loadItemData("invisible_glow_item_frame");
        registry.registerRegularInvisibleItemFrame(getInvisibleKey(), regData.name(),
                regData.lore(), regData.glint());
        registry.registerGlowInvisibleItemFrame(getInvisibleKey(), glowData.name(),
                glowData.lore(), glowData.glint());

        // Register the two configurable shaped recipes.
        Recipe regRecipe = cm.loadRecipe(getRegularRecipeKey(), "invisible_item_frame",
                registry.getRegularInvisibleFrame());
        registerRecipe(regRecipe);

        Recipe glowRecipe = cm.loadRecipe(getGlowRecipeKey(), "invisible_glow_item_frame",
                registry.getGlowInvisibleFrame());
        registerRecipe(glowRecipe);

        // Also support upgrading an already-crafted regular invisible frame with glow ink.
        registerShapelessGlowRecipe(getShapelessGlowRecipeKey());

        // bStats plugin ID: 25837.
        int pluginId = 25837;
        new Metrics(this, pluginId);
    }

    /**
     * Adds a recipe to Bukkit's recipe registry.
     *
     * <p>Duplicate recipe keys can occur after an unsupported plugin reload. Log a useful diagnostic
     * rather than allowing that registration exception to prevent the rest of startup.</p>
     *
     * @param recipe recipe produced by {@link ConfigurationManager}
     */
    private void registerRecipe(Recipe recipe) {
        try {
            Bukkit.addRecipe(recipe);
        } catch (IllegalStateException ignored) {
            if (firstLoad) {
                getLogger().severe("Failed to add recipe. There is likely something wrong with the config. Try " +
                        "deleting the config and restarting the server to regenerate the default config.");
            } else {
                getLogger().warning("A plugin reload was detected. Plugin reloads are not supported. Crafting recipes" +
                        " failed to reload.");
            }
        }
    }

    /**
     * Registers the shapeless conversion recipe: one regular invisible frame plus one glow ink sac
     * produces one invisible glow item frame.
     *
     * <p>An exact item choice is used so ordinary item frames cannot be substituted for the custom
     * marked item. The registry supplies a clone to avoid mutating its canonical item template.</p>
     *
     * @param key plugin-scoped identifier for the recipe
     */
    private void registerShapelessGlowRecipe(NamespacedKey key) {
        ItemStack glowIs = ItemFrameRegistry.getInstance().getGlowInvisibleFrame();
        ItemStack regIs = ItemFrameRegistry.getInstance().getRegularInvisibleFrame();

        ShapelessRecipe recipe = new ShapelessRecipe(key, glowIs.clone());
        recipe.addIngredient(Material.GLOW_INK_SAC);
        recipe.addIngredient(RecipeChoice.exactChoice(regIs.clone()));

        Bukkit.addRecipe(recipe);
    }
}
