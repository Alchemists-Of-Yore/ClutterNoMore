package dev.tazer.clutternomore.common.compat;

//? if <1.21.4 {


/*import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;

@EmiEntrypoint
public class EMICompat implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.removeEmiStacks(emiStack -> {
            return emiStack.getId().getNamespace().equals("clutternomore") || ShapeMap.isShape(emiStack.getItemStack().getItem());
        });

        for (Map.Entry<Item, List<Item>> entry : ShapeMap.shapesView().entrySet()) {
            List<Item> shapes = entry.getValue();
            if (shapes.isEmpty()) continue;
            EmiStack parentStack = EmiStack.of(entry.getKey().getDefaultInstance());
            for (Item shape : shapes) registry.addAlias(parentStack, Component.translatable(shape.getDescriptionId()));
        }
    }

    public static boolean isHoveringIngredient() {
        EmiStackInteraction interaction = EmiApi.getHoveredStack(false);
        return interaction != null && !interaction.isEmpty();
    }
}
*///?}
