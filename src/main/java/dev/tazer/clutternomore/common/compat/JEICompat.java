package dev.tazer.clutternomore.common.compat;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
//? if >1.20.1
import mezz.jei.api.registration.IIngredientAliasRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
//? if >1.20.1
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
//? if >1.20.1
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
//? if >1.20.1 {
import java.util.List;
import java.util.Map;
//?}

@JeiPlugin
public class JEICompat implements IModPlugin {

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        ArrayList<ItemStack> stacksToHide = new ArrayList<>();
        BuiltInRegistries.ITEM.entrySet().stream().forEach(item -> {
            if (ShapeMap.isShape(item.getValue())) stacksToHide.add(item.getValue().getDefaultInstance());
            else if (item.getKey().identifier().getNamespace().equals(ClutterNoMore.MODID)) stacksToHide.add(item.getValue().getDefaultInstance());
        });
        registry.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, stacksToHide);
    }

    //? if >1.20.1 {
    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        for (Map.Entry<Item, List<Item>> entry : ShapeMap.shapesView().entrySet()) {
            List<Item> shapes = entry.getValue();
            if (shapes.isEmpty()) continue;
            List<String> aliases = shapes.stream().map(s -> Component.translatable(s.getDescriptionId()).getString()).toList();
            registration.addAliases(VanillaTypes.ITEM_STACK, entry.getKey().getDefaultInstance(), aliases);
        }
    }
    //?}

    @Override
    public Identifier getPluginUid() {
        return ClutterNoMore.location("jei");
    }
}
