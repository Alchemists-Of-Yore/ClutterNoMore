package dev.tazer.clutternomore.client;

import com.google.common.collect.ImmutableList;
import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;

//? if >1.20.1 {
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
//?} else {
/*import net.minecraft.client.gui.screens.OptionsSubScreen;
*///?}
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ShapeSwitcherOptionsScreen extends OptionsSubScreen {
    public static final Component TITLE = Component.translatable("options.clutternomore.shape_switcher.title");
    public static final OptionInstance.Enum<CNMConfig.InputType> INPUT_TYPE_VALUES = new OptionInstance.Enum<>(
            ImmutableList.of(
                    CNMConfig.InputType.HOLD,
                    CNMConfig.InputType.TOGGLE,
                    CNMConfig.InputType.PRESS
            ),
            CNMConfig.InputType.CODEC
    );
    //? if <1.21 {
    /*private OptionsList list;
    *///?}

    public ShapeSwitcherOptionsScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, TITLE);
    }

    public ShapeSwitcherOptionsScreen(Screen lastScreen) {
        super(lastScreen, Minecraft.getInstance().options, TITLE);
    }

    //? if >1.20.1 {
    @Override
    protected void addOptions() {
        OptionInstance<?> moving = new OptionInstance<>(
                "key.clutternomore.menu_type",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.menu_scrolling") : Component.translatable("key.clutternomore.menu_static"),
                OptionInstance.BOOLEAN_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.SCROLLING.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.SCROLLING::setValue);

        OptionInstance<?> toggleButton = new OptionInstance<>(
                "key.clutternomore.open_menu",
                OptionInstance.noTooltip(),
                (component, value) -> Component.translatable("key.clutternomore.menu_" + value),
                INPUT_TYPE_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.HOLD.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.HOLD::setValue);

        OptionInstance<?> overscroll = new OptionInstance<>(
                "key.clutternomore.wrap_scrolling",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.enabled") : Component.translatable("key.clutternomore.disabled"),
                OptionInstance.BOOLEAN_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.WRAP_SCROLLING.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.WRAP_SCROLLING::setValue);

        OptionInstance<?> lookToSwitch = new OptionInstance<>(
                "key.clutternomore.look_to_switch",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.enabled") : Component.translatable("key.clutternomore.disabled"),
                OptionInstance.BOOLEAN_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.LOOK_TO_SWITCH.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.LOOK_TO_SWITCH::setValue);

        OptionInstance<?> shapeIndicator = new OptionInstance<>(
                "key.clutternomore.shape_indicator",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.enabled") : Component.translatable("key.clutternomore.disabled"),
                OptionInstance.BOOLEAN_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.SHAPE_INDICATOR.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.SHAPE_INDICATOR::setValue);

        OptionInstance<?> detailedTooltips = new OptionInstance<>(
                "key.clutternomore.detailed_tooltips",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.enabled") : Component.translatable("key.clutternomore.disabled"),
                OptionInstance.BOOLEAN_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.DETAILED_TOOLTIPS.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.DETAILED_TOOLTIPS::setValue);


        if (list != null) list.addSmall(moving, toggleButton, overscroll, lookToSwitch, shapeIndicator, detailedTooltips);
    }//?} else {
    /*@Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);

        OptionInstance<?> moving = new OptionInstance<>(
                "key.clutternomore.menu_type",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.menu_scrolling") : Component.translatable("key.clutternomore.menu_static"),
                OptionInstance.BOOLEAN_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.SCROLLING.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.SCROLLING::setValue);

        OptionInstance<?> toggleButton = new OptionInstance<>(
                "key.clutternomore.open_menu",
                OptionInstance.noTooltip(),
                (component, value) -> Component.translatable("key.clutternomore.menu_" + value),
                INPUT_TYPE_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.HOLD.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.HOLD::setValue);

        OptionInstance<?> overscroll = new OptionInstance<>(
                "key.clutternomore.wrap_scrolling",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.enabled") : Component.translatable("key.clutternomore.disabled"),
                OptionInstance.BOOLEAN_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.WRAP_SCROLLING.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.WRAP_SCROLLING::setValue);

        OptionInstance<?> lookToSwitch = new OptionInstance<>(
                "key.clutternomore.look_to_switch",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.enabled") : Component.translatable("key.clutternomore.disabled"),
                OptionInstance.BOOLEAN_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.LOOK_TO_SWITCH.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.LOOK_TO_SWITCH::setValue);

        OptionInstance<?> shapeIndicator = new OptionInstance<>(
                "key.clutternomore.shape_indicator",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.enabled") : Component.translatable("key.clutternomore.disabled"),
                OptionInstance.BOOLEAN_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.SHAPE_INDICATOR.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.SHAPE_INDICATOR::setValue);

        OptionInstance<?> detailedTooltips = new OptionInstance<>(
                "key.clutternomore.detailed_tooltips",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.enabled") : Component.translatable("key.clutternomore.disabled"),
                OptionInstance.BOOLEAN_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.DETAILED_TOOLTIPS.value(),
                ClutterNoMoreClient.CLIENT_CONFIG.DETAILED_TOOLTIPS::setValue);

        this.list.addSmall(new OptionInstance[]{moving, toggleButton, overscroll, lookToSwitch, shapeIndicator, detailedTooltips});
        this.addRenderableWidget(this.list);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.minecraft.options.save();
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }
    *///?}
}
