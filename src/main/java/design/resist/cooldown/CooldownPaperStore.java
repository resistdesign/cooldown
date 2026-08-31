package design.resist.cooldown;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

final class CooldownPaperStore {
	private final Path path;
	private final Logger logger;
	private final Map<UUID, Long> cooldownUntil = new HashMap<>();

	CooldownPaperStore(Path path, Logger logger) {
		this.path = path;
		this.logger = logger;
		load();
	}

	synchronized void lock(UUID playerId, long untilEpochMillis) {
		cooldownUntil.put(playerId, untilEpochMillis);
		save();
	}

	synchronized OptionalLong remainingMillis(UUID playerId, long nowEpochMillis) {
		Long until = cooldownUntil.get(playerId);
		if (until == null) {
			return OptionalLong.empty();
		}

		long remaining = until - nowEpochMillis;
		if (remaining <= 0) {
			cooldownUntil.remove(playerId);
			save();
			return OptionalLong.empty();
		}

		return OptionalLong.of(remaining);
	}

	private void load() {
		if (!Files.exists(path)) {
			return;
		}

		Properties properties = new Properties();
		try (InputStream input = Files.newInputStream(path)) {
			properties.load(input);
		} catch (IOException exception) {
			logger.log(Level.SEVERE, "Failed to load cooldown state from " + path, exception);
			return;
		}

		for (String key : properties.stringPropertyNames()) {
			try {
				UUID playerId = UUID.fromString(key);
				long until = Long.parseLong(properties.getProperty(key));
				cooldownUntil.put(playerId, until);
			} catch (IllegalArgumentException exception) {
				logger.warning("Ignoring invalid cooldown state entry '" + key + "'");
			}
		}
	}

	private void save() {
		Properties properties = new Properties();
		cooldownUntil.forEach((playerId, until) -> properties.setProperty(playerId.toString(), Long.toString(until)));

		try {
			Files.createDirectories(path.getParent());
			Path temp = path.resolveSibling(path.getFileName() + ".tmp");
			try (OutputStream output = Files.newOutputStream(temp)) {
				properties.store(output, "COOLDOWN player lockout state");
			}

			try {
				Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (AtomicMoveNotSupportedException exception) {
				Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException exception) {
			logger.log(Level.SEVERE, "Failed to persist cooldown state to " + path, exception);
		}
	}
}
