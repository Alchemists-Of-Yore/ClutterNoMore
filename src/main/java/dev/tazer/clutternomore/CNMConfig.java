package dev.tazer.clutternomore;

import com.mojang.serialization.Codec;
import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedName;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import net.minecraft.util.StringRepresentable;

public class CNMConfig {
    public static class StartupConfig extends ReflectiveConfig {
        @Comment("If vertical slabs should be added to all existing slabs")
        @SerializedName("vertical_slabs")
        public final TrackedValue<Boolean> VERTICAL_SLABS = this.value(true);
        @Comment("If steps should be added to all existing stairs")
        @SerializedName("steps")
        public final TrackedValue<Boolean> STEPS = this.value(true);
    }

    public static class ClientConfig extends ReflectiveConfig {
        @Comment("Whether to wrap around to the beginning after scrolling past the end of the list")
        @SerializedName("wrap_scrolling")
        public final TrackedValue<Boolean> WRAP_SCROLLING = this.value(true);
        @Comment("If the shape switcher menu should be scrolling or static")
        @SerializedName("scrolling")
        public final TrackedValue<Boolean> SCROLLING = this.value(true);
        @Comment("If the change block shape key should be held or toggled to open the menu")
        @SerializedName("hold")
        public final TrackedValue<InputType> HOLD = this.value(InputType.HOLD);
        @Comment("Whether to generate models for vertical slabs and steps when the game is launched. Modpacks looking for a faster start can disable this option and ship the generated resource pack.")
        @SerializedName("runtime_asset_generation")
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