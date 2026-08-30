package design.resist.cooldown;

import java.time.Duration;

final class DurationFormatter {
	private DurationFormatter() {
	}

	static String format(long millis) {
		Duration duration = Duration.ofMillis(Math.max(0, millis));
		long hours = duration.toHours();
		long minutes = duration.minusHours(hours).toMinutes();
		long seconds = duration.minusHours(hours).minusMinutes(minutes).toSeconds();

		if (hours > 0) {
			return String.format("%dh %02dm %02ds", hours, minutes, seconds);
		}
		if (minutes > 0) {
			return String.format("%dm %02ds", minutes, seconds);
		}
		return seconds + "s";
	}
}
