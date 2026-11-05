package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.Utils;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Applies the plugin's visibility rules and container interaction behavior to marked frames.
 *
 * <p>Only frames carrying the plugin's entity marker are modified. Vanilla item frames are left
 * untouched, even when they are placed next to containers.</p>
 */
public class ItemFrameInteractionListener implements Listener {

    /** Plugin-scoped key used to identify custom invisible frames. */
    private final NamespacedKey isInvisibleKey;

    /** @param isInvisibleKey persistent-data key attached to custom frame entities */
    public ItemFrameInteractionListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Makes a frame invisible when an item is inserted and visible again when its item is removed.
     *
     * <p>When the player attempts to rotate a marked frame, the action is allowed while sneaking.
     * Otherwise, if the block supporting the frame is a container, the rotation is cancelled and that
     * container's inventory is opened. If no container is behind the frame, normal rotation proceeds.</p>
     *
     * @param e Paper event describing a player's item-frame change action
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemFrameChange(PlayerItemFrameChangeEvent e) {
        ItemFrame frame = e.getItemFrame();
        if (!Utils.isInvisibleItemFrame(frame, isInvisibleKey)) {
            return;
        }

        switch (e.getAction()) {
            case PLACE -> frame.setVisible(false);
            case REMOVE -> frame.setVisible(true);
            case ROTATE -> {
                Player p = e.getPlayer();
                if (p.isSneaking()) return;

                // The attached face points away from the block supporting the frame.
                BlockFace attachedFace = e.getItemFrame().getAttachedFace();
                Block mount = e.getItemFrame().getLocation().getBlock().getRelative(attachedFace);
                if (mount.getState() instanceof Container container) {
                    e.setCancelled(true);
                    p.openInventory(container.getInventory());
                }
            }
        }
    }
}
