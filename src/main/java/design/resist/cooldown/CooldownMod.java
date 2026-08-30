package design.resist.cooldown;

import java.nio.file.Path;
import java.util.OptionalLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class CooldownMod implements ModInitializer {
	public static final String MOD_ID = "cooldown";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private CooldownConfig config;
	private CooldownStore store;

	@Override
	public void onInitialize() {
		config = CooldownConfig.load();
		LOGGER.info("COOLDOWN initialized with a {} second death cooldown", config.cooldownSeconds());

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			Path statePath = server.getWorldPath(LevelResource.ROOT).resolve("cooldown").resolve("state.properties");
			store = new CooldownStore(statePath);
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> store = null);

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (!(entity instanceof ServerPlayer player) || store == null) {
				return;
			}

			long cooldownMillis = config.cooldownMillis();
			long until = System.currentTimeMillis() + cooldownMillis;
			store.lock(player.getUUID(), until);

			String duration = DurationFormatter.format(cooldownMillis);
			LOGGER.info("{} died and entered cooldown for {}", player.getGameProfile().name(), duration);
			player.connection.disconnect(Component.literal("You died. Cool down for " + duration + ". See you then."));
		});

		ServerPlayerEvents.JOIN.register(player -> {
			if (store == null) {
				return;
			}

			OptionalLong remaining = store.remainingMillis(player.getUUID(), System.currentTimeMillis());
			if (remaining.isEmpty()) {
				return;
			}

			String duration = DurationFormatter.format(remaining.getAsLong());
			player.connection.disconnect(Component.literal("Still cooling down. Come back in " + duration + "."));
		});
	}
}
