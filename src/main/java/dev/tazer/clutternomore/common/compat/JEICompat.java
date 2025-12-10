package dev.tazer.clutternomore.common.compat;
//? if <1.21.11 {
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

@JeiPlugin
public class JEICompat implements IModPlugin {

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        ArrayList<ItemStack> stacksToHide = new ArrayList<>();
        BuiltInRegistries.ITEM.entrySet().stream().forEach(item -> {
            if (ShapeMap.isShape(item.getValue())) stacksToHide.add(item.getValue().getDefaultInstance());
            else if (item.getKey().location().getNamespace().equals(ClutterNoMore.MODID)) stacksToHide.add(item.getValue().getDefaultInstance());
        });
        registry.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, stacksToHide);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ClutterNoMore.location("jei");
    }
}
//?}