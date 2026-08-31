package design.resist.cooldown;

import java.nio.file.Path;
import java.util.OptionalLong;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class CooldownPaperPlugin extends JavaPlugin implements Listener {
	private CooldownPaperConfig config;
	private CooldownPaperStore store;

	@Override
	public void onEnable() {
		Path dataPath = getDataFolder().toPath();
		config = CooldownPaperConfig.load(dataPath.resolve("cooldown.properties"), getLogger());
		store = new CooldownPaperStore(dataPath.resolve("state.properties"), getLogger());

		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("COOLDOWN enabled with a " + config.cooldownSeconds() + " second death cooldown");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		// COOLDOWN costs time, not gear or experience.
		event.setKeepInventory(true);
		event.getDrops().clear();
		event.setKeepLevel(true);
		event.setDroppedExp(0);

		long cooldownMillis = config.cooldownMillis();
		long until = System.currentTimeMillis() + cooldownMillis;
		store.lock(player.getUniqueId(), until);

		String duration = DurationFormatter.format(cooldownMillis);
		getLogger().info(player.getName() + " died and entered cooldown for " + duration);

		// Let the death event finish applying keep-inventory before disconnecting.
		getServer().getScheduler().runTask(this, () -> {
			if (player.isOnline()) {
				player.kick(Component.text("You died. Cool down for " + duration + ". See you then."));
			}
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		OptionalLong remaining = store.remainingMillis(event.getPlayer().getUniqueId(), System.currentTimeMillis());
		if (remaining.isEmpty()) {
			return;
		}

		String duration = DurationFormatter.format(remaining.getAsLong());
		event.disallow(
			PlayerLoginEvent.Result.KICK_OTHER,
			Component.text("Still cooling down. Come back in " + duration + ".")
		);
	}
}
