package dev.tazer.clutternomore.common.inventory;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ShapeTooltip(List<ItemStack> stacks, int selectedIndex) implements TooltipComponent {
}
