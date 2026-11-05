package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.ItemFrameRegistry;
import com.atlasgong.invisibleitemframeslite.Utils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Recreates the expected drops when a plugin-created invisible item frame is removed.
 *
 * <p>The custom item marker exists on the placed entity, not on the ItemStack stored inside that entity.
 * Vanilla's normal frame drop therefore cannot recreate the plugin's custom item metadata. This listener
 * replaces that drop with a fresh item from {@link ItemFrameRegistry}.</p>
 */
public class ItemFrameBreakListener implements Listener {

    /** Plugin-scoped key used to identify custom invisible frame entities. */
    private final NamespacedKey isInvisibleKey;

    /** @param isInvisibleKey persistent-data key attached to custom frame entities */
    public ItemFrameBreakListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Handles all hanging-entity removal causes for marked frames.
     *
     * <p>The event is cancelled before the entity is removed to prevent Bukkit from also performing its
     * normal removal/drop behavior. The item displayed in the frame is dropped separately, followed by
     * the matching regular or glow custom frame item. The corresponding vanilla break sound is played
     * with the frame drop.</p>
     *
     * @param e hanging break event; unrelated and already-cancelled events are ignored
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent e) {
        final boolean isInvisFrame = Utils.isInvisibleItemFrame(e.getEntity(), isInvisibleKey);
        if (!isInvisFrame) return;

        final ItemFrame invisFrame = (ItemFrame) e.getEntity(); // safe to cast given check above
        final Location loc = invisFrame.getLocation();

        // Own the removal and drop sequence so the custom ItemStack metadata is preserved.
        e.setCancelled(true);
        invisFrame.remove();

        // Preserve the item currently displayed in the frame, if it is not empty.
        if (!invisFrame.getItem().getType().isAir()) {
            dropItem(loc, invisFrame.getItem());
        }

        // Drop the correct custom item variant and play its matching break sound.
        if (invisFrame instanceof GlowItemFrame) {
            ItemStack glowInvisItemFrame = ItemFrameRegistry.getInstance().getGlowInvisibleFrame();
            dropItem(loc, glowInvisItemFrame, Sound.ENTITY_GLOW_ITEM_FRAME_BREAK);
        } else {
            ItemStack regInvisItemFrame = ItemFrameRegistry.getInstance().getRegularInvisibleFrame();
            dropItem(loc, regInvisItemFrame, Sound.ENTITY_ITEM_FRAME_BREAK);
        }
    }

    /**
     * Spawns an item naturally at the supplied location and optionally plays a break sound.
     *
     * @param loc location where the item should appear
     * @param itemToDrop item stack to spawn
     * @param sound optional sound; null suppresses sound playback
     */
    private void dropItem(Location loc, ItemStack itemToDrop, Sound sound) {
        World w = loc.getWorld();
        w.dropItemNaturally(loc, itemToDrop);
        if (sound != null) w.playSound(loc, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        // No player statistics are adjusted: this is an entity drop, not a block break.
    }

    /**
     * Spawns an item without playing a sound. Used for the item that was displayed inside the frame.
     *
     * @param loc location where the item should appear
     * @param itemToDrop item stack to spawn
     */
    private void dropItem(Location loc, ItemStack itemToDrop) {
        dropItem(loc, itemToDrop, null);
    }
}
