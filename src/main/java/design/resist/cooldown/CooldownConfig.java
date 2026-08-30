package design.resist.cooldown;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;

final class CooldownConfig {
	private static final long DEFAULT_COOLDOWN_SECONDS = 5 * 60 * 60;
	private static final String COOLDOWN_SECONDS = "cooldown-seconds";

	private final long cooldownSeconds;

	private CooldownConfig(long cooldownSeconds) {
		this.cooldownSeconds = Math.max(0, cooldownSeconds);
	}

	static CooldownConfig load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("cooldown.properties");
		Properties properties = new Properties();

		if (Files.exists(path)) {
			try (InputStream input = Files.newInputStream(path)) {
				properties.load(input);
			} catch (IOException exception) {
				CooldownMod.LOGGER.error("Failed to read {}", path, exception);
			}
		}

		long seconds = parseLong(properties.getProperty(COOLDOWN_SECONDS), DEFAULT_COOLDOWN_SECONDS);
		properties.setProperty(COOLDOWN_SECONDS, Long.toString(seconds));

		try {
			Files.createDirectories(path.getParent());
			try (OutputStream output = Files.newOutputStream(path)) {
				properties.store(output, "COOLDOWN server configuration. Duration is real-world seconds.");
			}
		} catch (IOException exception) {
			CooldownMod.LOGGER.error("Failed to write {}", path, exception);
		}

		return new CooldownConfig(seconds);
	}

	long cooldownMillis() {
		return cooldownSeconds * 1000L;
	}

	long cooldownSeconds() {
		return cooldownSeconds;
	}

	private static long parseLong(String value, long fallback) {
		if (value == null) {
			return fallback;
		}

		try {
			return Math.max(0, Long.parseLong(value.trim()));
		} catch (NumberFormatException exception) {
			CooldownMod.LOGGER.warn("Invalid cooldown-seconds value '{}'; using {}", value, fallback);
			return fallback;
		}
	}
}
