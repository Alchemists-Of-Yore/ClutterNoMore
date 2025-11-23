package dev.tazer.clutternomore.common.compat;

//? if <1.21.4 {


/*import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;

@EmiEntrypoint
public class EMICompat implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.removeEmiStacks(emiStack -> {
            return emiStack.getId().getNamespace().equals("clutternomore") || ShapeMap.isShape(emiStack.getItemStack().getItem());
        });
    }
}
*///?}