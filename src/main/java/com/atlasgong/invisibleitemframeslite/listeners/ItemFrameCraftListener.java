package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 * Prevents custom invisible frame items from being used as ingredients in their own shaped recipes.
 *
 * <p>This avoids recursive crafting in which an already-crafted custom frame could be fed back into
 * the recipe to produce additional custom frames. Ordinary vanilla frames and unrelated recipes are
 * not affected.</p>
 */
public class ItemFrameCraftListener implements Listener {

    /** Plugin-scoped key used to identify custom invisible frame items. */
    private final NamespacedKey isInvisibleKey;

    /** @param isInvisibleKey persistent-data key stored on custom frame ItemStacks */
    public ItemFrameCraftListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Clears the crafting result when a custom frame is used as an ingredient in a shaped recipe
     * whose output is itself a custom invisible frame.
     *
     * @param event preparation event for the crafting inventory
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        // Ignore recipes whose result is not one of our marked frame items.
        if (!Utils.isInvisibleItemFrame(event.getInventory().getResult(), isInvisibleKey)) {
            return;
        }

        // Restrict this safeguard to the plugin's shaped recipes; other recipe types retain vanilla behavior.
        if (event.getRecipe() instanceof ShapedRecipe) {
            for (ItemStack is : event.getInventory().getMatrix()) {
                if (Utils.isInvisibleItemFrame(is, isInvisibleKey)) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                }
            }
        }
    }
}
