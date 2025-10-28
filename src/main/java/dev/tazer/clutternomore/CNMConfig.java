package dev.tazer.clutternomore;

import com.mojang.serialization.Codec;
import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import net.minecraft.util.StringRepresentable;
//? if >1.20.1 {
import net.neoforged.neoforge.common.ModConfigSpec;
//?} else {
/*import net.minecraftforge.common.ForgeConfigSpec;
*///?}

public class CNMConfig {
    public static class StartupConfig extends ReflectiveConfig {
        @Comment("If vertical slabs should be added to all existing slabs")
        public final TrackedValue<Boolean> VERTICAL_SLABS = this.value(true);
        @Comment("If steps should be added to all existing stairs")
        public final TrackedValue<Boolean> STEPS = this.value(true);
    }

    public static class ClientConfig extends ReflectiveConfig {
        @Comment("If the shape switcher menu should be scrolling or static")
        public final TrackedValue<Boolean> SCROLLING = this.value(true);
        @Comment("If the change block shape key should be held or toggled to open the menu")
        public final TrackedValue<InputType> HOLD = this.value(InputType.HOLD);
        @Comment("Whether to generate models for vertical slabs and steps when the game is launched. Modpacks looking for a faster start can disable this option and ship the generated resource pack.")
        public final TrackedValue<Boolean> RUNTIME_ASSET_GENERATION = this.value(true);
    }

    public enum InputType implements StringRepresentable {
        HOLD,
        TOGGLE,
        PRESS;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        @Override
        public String getSerializedName() {
            return toString();
        }

        public static final Codec<InputType> CODEC = StringRepresentable.fromEnum(InputType::values);
    }
}