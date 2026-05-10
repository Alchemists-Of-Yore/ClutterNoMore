package dev.tazer.clutternomore.common.mixin.dev;

import dev.tazer.clutternomore.common.mixin.annotation.IfDevEnvironment;
//? fabric {
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
//?}

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@IfDevEnvironment
//? if >26 {
@Mixin(net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder.class)
//?} else {
/*@Mixin(net.minecraft.data.recipes.ShapedRecipeBuilder.class)
*///?}
public class ShapedRecipeBuilderMixin {
    //? fabric && >26 {
    @Shadow
    @Final
    private Map<String, Criterion<?>> criteria;

    @Inject(method = "build", at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z"), cancellable = true)
    private void weDontNeedRecipeAdvancementsInThisHouse(RecipeOutput output, ResourceKey<Recipe<?>> id, RecipeCategory category, CallbackInfoReturnable<AdvancementHolder> cir) {
        if (criteria.isEmpty() && output.getRecipeIdentifier(id.identifier()).getNamespace().equals("minecraft")) cir.setReturnValue(null);
    }
    //?}
    //? if fabric && <26 {
    /*@Shadow
    @Final
    private Map<String, Criterion<?>> criteria;
    @Shadow
    @Final
    private List<String> rows;

    @Shadow
    @Final
    private Map<Character, Ingredient> key;

    @Inject(method = "ensureValid", at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z"), cancellable = true)
    private void weDontNeedRecipeAdvancementsInThisHouse(Identifier location, CallbackInfoReturnable<ShapedRecipePattern> cir) {
        if (criteria.isEmpty()) cir.setReturnValue(ShapedRecipePattern.of(this.key, this.rows));
    }
    *///?}
}
