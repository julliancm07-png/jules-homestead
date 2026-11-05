package dev.jules.homestead.registry;

import dev.jules.homestead.HomesteadCore;
import dev.jules.homestead.block.entity.ElectricRangeBlockEntity;
import dev.jules.homestead.block.entity.PoweredBlockEntity;
import dev.jules.homestead.block.entity.RefrigeratorBlockEntity;
import dev.jules.homestead.block.entity.SpaceHeaterBlockEntity;
import dev.jules.homestead.block.entity.WaterWheelBlockEntity;
import dev.jules.homestead.block.entity.WindTurbineBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import team.reborn.energy.api.EnergyStorage;

public final class ModBlockEntities {
	public static final BlockEntityType<WindTurbineBlockEntity> WIND_TURBINE = register("wind_turbine", WindTurbineBlockEntity::new, ModBlocks.WIND_TURBINE);
	public static final BlockEntityType<WaterWheelBlockEntity> WATER_WHEEL = register("water_wheel", WaterWheelBlockEntity::new, ModBlocks.WATER_WHEEL);
	public static final BlockEntityType<RefrigeratorBlockEntity> REFRIGERATOR = register("refrigerator", RefrigeratorBlockEntity::new, ModBlocks.REFRIGERATOR);
	public static final BlockEntityType<ElectricRangeBlockEntity> ELECTRIC_RANGE = register("electric_range", ElectricRangeBlockEntity::new, ModBlocks.ELECTRIC_RANGE);
	public static final BlockEntityType<SpaceHeaterBlockEntity> SPACE_HEATER = register("space_heater", SpaceHeaterBlockEntity::new, ModBlocks.SPACE_HEATER);

	private ModBlockEntities() {
	}

	private static <T extends BlockEntity> BlockEntityType<T> register(String name, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, HomesteadCore.id(name), FabricBlockEntityTypeBuilder.create(factory, block).build());
	}

	public static void init() {
		for (BlockEntityType<? extends PoweredBlockEntity> t : java.util.List.of(WIND_TURBINE, WATER_WHEEL, REFRIGERATOR, ELECTRIC_RANGE, SPACE_HEATER)) {
			EnergyStorage.SIDED.registerForBlockEntity((be, dir) -> be.energyStorage(dir), t);
		}
		ItemStorage.SIDED.registerForBlockEntity((be, dir) -> ContainerStorage.of(be.items(), dir), REFRIGERATOR);
		ItemStorage.SIDED.registerForBlockEntity((be, dir) -> ContainerStorage.of(be.items(), dir), ELECTRIC_RANGE);
	}
}
