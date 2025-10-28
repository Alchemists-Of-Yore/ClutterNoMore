package dev.tazer.clutternomore.common.registry;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
//? if forge {
/*import net.minecraftforge.registries.RegistryManager;
*///?}

import java.util.function.Supplier;

public class CommonRegistry {
    public static <R, T extends R> Supplier<T> register(String name, Supplier<T> supplier, Registry<R> reg) {
        T object = supplier.get();
        //? if neoforge || fabric {
        Registry.register(reg, ClutterNoMore.location(name), object);
        //?} else if forge {
        /*RegistryManager.ACTIVE.getRegistry(reg.key()).register(ClutterNoMore.location(name), object);
        *///?}
        return () -> object;
    }

    public static <B extends Item> Supplier<B> registerItem(String name, Supplier<B> supplier) {
        return register(name, supplier, BuiltInRegistries.ITEM);
    }

    public static <B extends Block> Supplier<B> registerBlock(String name, Supplier<B> supplier) {
        return register(name, supplier, BuiltInRegistries.BLOCK);
    }

    public static <T extends EntityType<?>> Supplier<T> registerEntity(String name, Supplier<T> supplier) {
        return register(name, supplier, BuiltInRegistries.ENTITY_TYPE);
    }

    public static Supplier<SoundEvent> registerSoundEvent(String name, Supplier<SoundEvent> supplier) {
        return register(name, supplier, BuiltInRegistries.SOUND_EVENT);
    }

    public static Supplier<SoundEvent> registerSoundEvent(String name) {
        return registerSoundEvent(name, ()->SoundEvent.createVariableRangeEvent(ClutterNoMore.location(name)));
    }

    public static Holder<MobEffect> registerMobEffect(String name, Supplier<MobEffect> supplier) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ClutterNoMore.location(name), supplier.get());
    }
}