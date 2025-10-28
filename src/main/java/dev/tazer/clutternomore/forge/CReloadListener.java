package dev.tazer.clutternomore.forge;

//? if forge {

/*import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeManager;

public record CReloadListener(ReloadableServerResources resources, RegistryAccess registries) implements ResourceManagerReloadListener{

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        RecipeManager recipeManager = resources.getRecipeManager();
        ClutterNoMore.load(registries, recipeManager);
    }
}
*///?}