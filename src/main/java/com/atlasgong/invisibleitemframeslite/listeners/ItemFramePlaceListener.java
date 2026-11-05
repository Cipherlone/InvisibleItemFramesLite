package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Transfers the plugin marker from a custom frame item to the placed item-frame entity.
 *
 * <p>ItemStack metadata and entity persistent data are separate stores. Without this transfer, later
 * interaction and break listeners could not distinguish a plugin-created frame from a vanilla frame.</p>
 */
public class ItemFramePlaceListener implements Listener {

    /** Plugin-scoped key used to identify custom invisible frames. */
    private final NamespacedKey isInvisibleKey;

    /**
     * @param isInvisibleKey key used on both the inventory item and placed entity
     */
    public ItemFramePlaceListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Marks a newly placed hanging entity when the item used for placement is one of our custom frames.
     *
     * <p>Cancelled placement events are ignored. HIGHEST priority lets this listener observe the final
     * placement state after most other plugins have had an opportunity to modify the event.</p>
     *
     * @param e hanging-entity placement event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent e) {
        if (Utils.isInvisibleItemFrame(e.getItemStack(), isInvisibleKey)) {
            e.getEntity().getPersistentDataContainer().set(isInvisibleKey,
                    PersistentDataType.BYTE, (byte) 1);
        }
    }
}
