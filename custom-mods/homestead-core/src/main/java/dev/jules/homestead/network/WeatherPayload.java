package dev.jules.homestead.network;

import dev.jules.homestead.HomesteadCore;
import dev.jules.homestead.weather.Front;
import dev.jules.homestead.weather.WeatherManager;
import dev.jules.homestead.weather.WeatherSavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Server → client weather snapshot for one player, sent every 10 ticks while a front is active (and once when it ends).
 * kind: 0 none, 1 storm, 2 blizzard. precipitation: 0 none, 1 rain, 2 snow (at the player's position).
 */
public record WeatherPayload(byte kind, byte severity, float intensity, float windDirection, float windSpeed,
							 byte precipitation, boolean exposed) implements CustomPacketPayload {
	public static final Type<WeatherPayload> TYPE = new Type<>(HomesteadCore.id("weather"));
	public static final StreamCodec<FriendlyByteBuf, WeatherPayload> CODEC = StreamCodec.ofMember(WeatherPayload::write, WeatherPayload::read);

	public static WeatherPayload of(WeatherSavedData d, WeatherManager.PlayerWeather s, double windSpeed) {
		Front f = d.current();
		byte kind = f == null ? 0 : (byte) (f.kind() == Front.Kind.BLIZZARD ? 2 : 1);
		byte precip = switch (s.precipitation()) {
			case RAIN -> 1;
			case SNOW -> 2;
			default -> 0;
		};
		return new WeatherPayload(kind, (byte) s.severity(), s.intensity(), d.windDirection(), (float) windSpeed, precip, s.exposed());
	}

	private static WeatherPayload read(FriendlyByteBuf buf) {
		return new WeatherPayload(buf.readByte(), buf.readByte(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readByte(), buf.readBoolean());
	}

	private void write(FriendlyByteBuf buf) {
		buf.writeByte(kind);
		buf.writeByte(severity);
		buf.writeFloat(intensity);
		buf.writeFloat(windDirection);
		buf.writeFloat(windSpeed);
		buf.writeByte(precipitation);
		buf.writeBoolean(exposed);
	}

	public boolean blizzardExposure() {
		return exposed && precipitation == 2 && intensity > 0;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
