package dev.tazer.clutternomore.common.mixin.recipe;

import dev.tazer.clutternomore.common.recipe.RecipeRemover;
//? if <1.21 {
/*import net.minecraft.core.RegistryAccess;
*///?}
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @Shadow
    @Final
    private RecipeManager recipes;

    //? if >=26 {
    @Inject(method = "updateComponentsAndStaticRegistryTags", at = @At("RETURN"))
    private void cnm$removeShapeRecipes(CallbackInfo ci) {
        RecipeRemover.removeShapeRecipes(this.recipes);
    }
    //?} else if >1.21 {
    /*@Inject(method = "updateRegistryTags()V", at = @At("RETURN"))
    private void cnm$removeShapeRecipes(CallbackInfo ci) {
        RecipeRemover.removeShapeRecipes(this.recipes);
    }
    *///?} else {
    /*@Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V", at = @At("RETURN"))
    private void cnm$removeShapeRecipes(RegistryAccess registryAccess, CallbackInfo ci) {
        RecipeRemover.removeShapeRecipes(this.recipes);
    }
    *///?}
}
