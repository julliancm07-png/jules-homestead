package dev.jules.homestead.registry;

import dev.jules.homestead.HomesteadCore;
import dev.jules.homestead.block.BarometerBlock;
import dev.jules.homestead.block.MachineBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ModBlocks {
	public static final List<Item> ITEMS = new ArrayList<>();

	public static final Block WIND_TURBINE = machine("wind_turbine", MachineBlock.Kind.WIND_TURBINE, metal().noOcclusion());
	public static final Block WATER_WHEEL = machine("water_wheel", MachineBlock.Kind.WATER_WHEEL, wood());
	public static final Block REFRIGERATOR = machine("refrigerator", MachineBlock.Kind.REFRIGERATOR, metal());
	public static final Block ELECTRIC_RANGE = machine("electric_range", MachineBlock.Kind.ELECTRIC_RANGE,
			metal().lightLevel(s -> s.getValue(BlockStateProperties.LIT) ? 7 : 0));
	public static final Block SPACE_HEATER = machine("space_heater", MachineBlock.Kind.SPACE_HEATER,
			metal().lightLevel(s -> s.getValue(BlockStateProperties.LIT) ? 15 : 0));
	public static final Block BAROMETER = register("barometer", BarometerBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0f).sound(SoundType.WOOD).noOcclusion());

	private ModBlocks() {
	}

	private static BlockBehaviour.Properties metal() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0f, 6.0f).sound(SoundType.METAL).requiresCorrectToolForDrops();
	}

	private static BlockBehaviour.Properties wood() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0f).sound(SoundType.WOOD);
	}

	private static Block machine(String name, MachineBlock.Kind kind, BlockBehaviour.Properties props) {
		return register(name, p -> new MachineBlock(kind, p), props);
	}

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties props) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, HomesteadCore.id(name));
		Block block = Blocks.register(key, factory, props);
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, HomesteadCore.id(name));
		Item item = Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
		ITEMS.add(item);
		return block;
	}

	public static void init() {
	}
}
