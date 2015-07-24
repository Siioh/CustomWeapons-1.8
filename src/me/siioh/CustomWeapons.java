package me.siioh;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomWeapons extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("CustomWeapons by Siioh is now enabled!");
		getConfig().options().copyDefaults(true);
		saveConfig();
		new CustomWeaponsListener(this);
		this.getServer().getPluginManager()
				.registerEvents(new CustomWeaponsListener(this), this);
	}

	public class CustomWeaponsListener implements Listener {
		public CustomWeapons plugin;

		public CustomWeaponsListener(CustomWeapons plugin) {
			this.plugin = plugin;
		}

		@EventHandler
		public void onLogin(PlayerJoinEvent evt) {
			Player player = evt.getPlayer();
			player.sendMessage("Hi shrub");
		}
	}

	@Override
	public void onDisable() {
		getLogger().info("CustomWeapons has successfully been disabled.");

	}
}
