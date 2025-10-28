package dev.tazer.clutternomore.common.compat;

//? if >1.21.4 {
import de.crafty.eiv.common.api.recipe.ItemView;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class EIVCompat {

    public static void hide(Item item) {
        ItemView.excludeItem(item);
    }
}
//?}