package dev.tazer.clutternomore.common.compat;

//? if >1.21.9 {

import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.world.item.Item;

public class RRVCompat {

    public static void hide(Item item) {
        ItemView.excludeItem(item);
    }

    public static boolean isHoveringIngredient() {
        for (AbstractRrvOverlay overlay : OverlayManager.INSTANCE.screenContextMap().keySet()) {
            for (ItemSlot slot : overlay.itemSlots()) {
                if (slot.isHovered()) return true;
            }
        }
        return false;
    }
}
//?}