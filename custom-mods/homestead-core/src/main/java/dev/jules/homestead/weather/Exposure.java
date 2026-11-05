package dev.jules.homestead.weather;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Shelter heuristic (documented in docs/weather-design.md): an entity is sheltered when there is a roof (any
 * motion-blocking block except leaves, glass included) above its head AND a solid block lies upwind within {@code windCheck} blocks at head height. One heightmap lookup plus one
 * short raycast per check.
 */
public final class Exposure {
	private Exposure() {
	}

	public static boolean isExposed(ServerLevel level, Entity entity, float windDirectionDeg, int windCheck) {
		BlockPos head = BlockPos.containing(entity.getEyePosition());
		if (openToSky(level, head)) {
			return true;
		}
		return !blockedUpwind(level, entity.getEyePosition(), windDirectionDeg, windCheck);
	}

	/**
	 * Roof test via the MOTION_BLOCKING_NO_LEAVES heightmap (updated synchronously with block changes). Unlike
	 * {@code canSeeSky} (sky light 15) this treats glass as a roof — greenhouses shelter — and tree canopies as open.
	 */
	public static boolean openToSky(ServerLevel level, BlockPos pos) {
		return pos.getY() >= level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
	}

	/**
	 * Wind direction is where the wind blows FROM, in degrees clockwise from north (meteorological convention).
	 * The upwind ray therefore points towards that compass bearing.
	 */
	public static boolean blockedUpwind(ServerLevel level, Vec3 from, float windDirectionDeg, int distance) {
		double rad = Math.toRadians(windDirectionDeg);
		// Minecraft: north = -Z, east = +X.
		Vec3 dir = new Vec3(Math.sin(rad), 0, -Math.cos(rad));
		Vec3 to = from.add(dir.scale(distance));
		BlockHitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, net.minecraft.world.phys.shapes.CollisionContext.empty()));
		return hit.getType() == HitResult.Type.BLOCK;
	}

	public static Biome.Precipitation precipitationAt(ServerLevel level, BlockPos pos) {
		return level.getBiome(pos).value().getPrecipitationAt(pos, level.getSeaLevel());
	}
}
