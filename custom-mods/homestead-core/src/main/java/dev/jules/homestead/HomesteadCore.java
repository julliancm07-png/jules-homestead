package dev.jules.homestead;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HomesteadCore implements ModInitializer {
	public static final String MOD_ID = "homesteadcore";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOG.info("Homestead Core initialising");
	}
}
