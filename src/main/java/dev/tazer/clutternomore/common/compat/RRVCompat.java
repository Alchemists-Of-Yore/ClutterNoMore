package dev.tazer.clutternomore.common.compat;

//? if >1.21.9 {

import cc.cassian.rrv.api.recipe.ItemView;
import net.minecraft.world.item.Item;

public class RRVCompat {

    public static void hide(Item item) {
        ItemView.excludeItem(item);
    }
}
//?}