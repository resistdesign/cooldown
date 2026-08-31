package design.resist.cooldown;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

final class CooldownPaperConfig {
	private static final long DEFAULT_COOLDOWN_SECONDS = 20 * 60;
	private static final String COOLDOWN_SECONDS = "cooldown-seconds";

	private final long cooldownSeconds;

	private CooldownPaperConfig(long cooldownSeconds) {
		this.cooldownSeconds = Math.max(0, cooldownSeconds);
	}

	static CooldownPaperConfig load(Path path, Logger logger) {
		Properties properties = new Properties();

		if (Files.exists(path)) {
			try (InputStream input = Files.newInputStream(path)) {
				properties.load(input);
			} catch (IOException exception) {
				logger.log(java.util.logging.Level.SEVERE, "Failed to read " + path, exception);
			}
		}

		long seconds = parseLong(properties.getProperty(COOLDOWN_SECONDS), DEFAULT_COOLDOWN_SECONDS, logger);
		properties.setProperty(COOLDOWN_SECONDS, Long.toString(seconds));

		try {
			Files.createDirectories(path.getParent());
			try (OutputStream output = Files.newOutputStream(path)) {
				properties.store(output, "COOLDOWN server configuration. Duration is real-world seconds.");
			}
		} catch (IOException exception) {
			logger.log(java.util.logging.Level.SEVERE, "Failed to write " + path, exception);
		}

		return new CooldownPaperConfig(seconds);
	}

	long cooldownMillis() {
		return cooldownSeconds * 1000L;
	}

	long cooldownSeconds() {
		return cooldownSeconds;
	}

	private static long parseLong(String value, long fallback, Logger logger) {
		if (value == null) {
			return fallback;
		}

		try {
			return Math.max(0, Long.parseLong(value.trim()));
		} catch (NumberFormatException exception) {
			logger.warning("Invalid cooldown-seconds value '" + value + "'; using " + fallback);
			return fallback;
		}
	}
}
