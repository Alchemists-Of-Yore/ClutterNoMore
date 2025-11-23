package dev.tazer.clutternomore.common.blocks;

import net.minecraft.world.level.block.WeatheringCopper;

public class WeatheringVerticalSlabBlock extends VerticalSlabBlock implements WeatheringCopper {
    private final WeatherState age;

    public WeatheringVerticalSlabBlock(Properties properties, WeatherState age) {
        super(properties);
        this.age = age;
    }

    @Override
    public WeatherState getAge() {
        return age;
    }
}
