package me.siioh;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

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

		@SuppressWarnings("deprecation")
		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent evt) {
			Player player = evt.getPlayer();
			Inventory player_inventory = player.getInventory(); // Gets
																// inventory of
																// player.
			ItemStack in_hand = player.getItemInHand(); // In hand item.
			int in_hand_id = in_hand.getTypeId();
			ItemMeta in_hand_meta = in_hand.getItemMeta(); // Meta data of in
															// hand item.
			Action action = evt.getAction(); // Action within the event.
			World world = player.getWorld(); // World player is in.
			Location player_loc = player.getLocation(); // Location of the
														// player.
			Vector velocity_toss = player_loc.getDirection().multiply(2); // Vector
																			// of
																			// projectile
																			// speeds.
			Location direction = player
					.getEyeLocation()
					.toVector()
					.add(player_loc.getDirection().multiply(2))
					.toLocation(world, player_loc.getYaw(),
							player_loc.getPitch());

			if (in_hand_id == Material.BLAZE_ROD.getId()) { // Fire Rod
				if (in_hand_meta.hasDisplayName()) {
					if (in_hand_meta.getDisplayName().equalsIgnoreCase(
							plugin.getConfig().getString("Fire Rod"))) {

						if (action.equals(Action.RIGHT_CLICK_AIR)
								|| action.equals(Action.RIGHT_CLICK_BLOCK)) {
							if (Cooldowns.tryCooldown(player, "Fire_Rod", 500)) {
								Fireball fireball = player.getWorld().spawn(
										direction, Fireball.class);
								fireball.setIsIncendiary(false);
								fireball.setYield(1.5F);
								fireball.setShooter((ProjectileSource) player);
								fireball.setVelocity(velocity_toss);
							}
						}
					}
				}
			}

			if (in_hand_id == Material.TNT.getId()) { // Throwing TNT
				if (in_hand_meta.hasDisplayName()) {
					if (in_hand_meta.getDisplayName().equalsIgnoreCase(
							plugin.getConfig().getString("Throwing TNT"))) {
						TNTPrimed tnt = (TNTPrimed) world.spawn(direction,
								TNTPrimed.class);
						tnt.setVelocity(velocity_toss);
						if (player.getGameMode() == GameMode.ADVENTURE
								|| player.getGameMode() == GameMode.SURVIVAL) {
							if (in_hand.getAmount() == 1) {
								player.setItemInHand(null);
							}
							if (in_hand.getAmount() > 1) {
								in_hand.setAmount(in_hand.getAmount() - 1);
							}
						}
					}
				}
			}

			if (evt.getMaterial() == Material.DIAMOND_BARDING) { // Sniper +
																	// Machine
																	// Bow
				if (in_hand_meta.hasDisplayName()) {
					if (in_hand_meta.getDisplayName().equalsIgnoreCase( // Check
																		// Sniper
																		// Name
							plugin.getConfig().getString("Sniper Bow"))) {

						if (evt.getAction() == Action.RIGHT_CLICK_BLOCK
								|| evt.getAction() == Action.RIGHT_CLICK_AIR) {
							if (player.hasPotionEffect(PotionEffectType.SLOW)) {
								player.removePotionEffect(PotionEffectType.SLOW);
							} else {
								player.addPotionEffect(new PotionEffect(
										PotionEffectType.SLOW,
										Integer.MAX_VALUE, 20));
							}
						}
						if (evt.getAction() == Action.LEFT_CLICK_BLOCK
								|| evt.getAction() == Action.LEFT_CLICK_AIR) {
							if (player_inventory.contains(Material.ARROW)) {
								if (Cooldowns.tryCooldown(player, "Sniper",
										2000)) {
									world.spawnArrow(direction, velocity_toss,
											15F, 0F).setShooter(player);
									if (!world.getName().equalsIgnoreCase(
											"Mobarena")) {
										int current = in_hand.getDurability();
										int newdur = current + 1;
										in_hand.setDurability((short) newdur);
										if (current < 50) {
											ArrayList<String> lore = new ArrayList<String>();
											lore.add("Uses: " + current + "/50");
											lore.add("WARNING: The durability bar isn't accurate.");
											lore.add("This item doesn't work with enchantments");
											in_hand_meta.setLore(lore);
											in_hand.setItemMeta(in_hand_meta);
										}
										if (current == 50) {
											player.setItemInHand(null);
											player.playSound(player_loc,
													Sound.ITEM_BREAK, 10, 1);
										}
									}
								} else {
									String armor_name = "[Sniper Bow]";
									String cooldown = " seconds left until you can shoot again.";
									player.sendMessage(ChatColor.GOLD
											+ armor_name
											+ ChatColor.GRAY
											+ " You have "
											+ (Cooldowns.getCooldown(player,
													"Sniper") / 1000.0)
											+ cooldown);
								}
							}
						}
					}
					if (in_hand_meta.getDisplayName().equalsIgnoreCase( // Check
																		// Machine
																		// Name
							plugin.getConfig().getString("Machine Bow"))) {
						if (player_inventory.contains(Material.ARROW)) {
							if (Cooldowns.tryCooldown(player, "Machine", 200)) { // Change
																					// only
																					// here
																					// to
																					// set
																					// Cooldown
																					// (In
																					// milliseconds)
								world.spawnArrow(direction, velocity_toss, 5F,
										4F).setShooter(player);
								player.playSound(player_loc, Sound.EXPLODE, 10,
										1);
								ItemStack item = player.getItemInHand();
								if (!world.getName().equalsIgnoreCase(
										"Mobarena")) {
									int current = item.getDurability();
									int newdur = current + 1;
									item.setDurability((short) newdur);
									if (current < 100) {
										ArrayList<String> lore = new ArrayList<String>();
										lore.add("Uses: " + current + "/100"); // Use
																				// a
																				// second
																				// command
																				// to
																				// set
																				// another
																				// line.
										lore.add("WARNING: The durability bar isn't accurate.");
										lore.add("This item doesn't work with enchantments.");
										in_hand_meta.setLore(lore);
										item.setItemMeta(in_hand_meta);
									}
									if (current == 100) {
										player.setItemInHand(null);
										player.playSound(player_loc,
												Sound.ITEM_BREAK, 10, 1);
									}
								}
							}
						}
					}
				}
			}
		}

		@SuppressWarnings("deprecation")
		@EventHandler
		public void EntityShootBow(EntityShootBowEvent evt) {
			Entity entity = evt.getEntity();
			if (entity instanceof Player) {
				LivingEntity entity_alive = evt.getEntity();
				Player player = (Player) entity_alive;
				Inventory player_inventory = player.getInventory();
				ItemStack in_hand = player.getItemInHand();
				ItemMeta in_hand_meta = in_hand.getItemMeta();
				Vector velocity_arrow = evt.getProjectile().getVelocity();
				Vector velocity_toss = player.getLocation().getDirection()
						.multiply(2);
				Location player_loc = player.getLocation();
				World world = player.getWorld();
				Float speed = evt.getForce() * 2;
				Location direction = player
						.getEyeLocation()
						.toVector()
						.add(player_loc.getDirection().multiply(2))
						.toLocation(world, player_loc.getYaw(),
								player_loc.getPitch());

				if (in_hand_meta.hasDisplayName()) {
					if (in_hand_meta.getDisplayName().equalsIgnoreCase("Wither Bow")) { // Wither
																				// Bow
						if (player_inventory.contains(Material.ARROW)) {
							evt.setCancelled(true);
							player.updateInventory();
							if (Cooldowns.tryCooldown(player, "Wither", 350)) {
								WitherSkull skull = player
										.launchProjectile(WitherSkull.class);
								skull.setIsIncendiary(true);
								skull.setShooter((LivingEntity) player);
								skull.setYield(5F);
								skull.setVelocity(velocity_arrow);
								player.playSound(player_loc, Sound.SHOOT_ARROW,
										10, 1);
								int current = in_hand.getDurability();
								int newdur = current + 1;
								in_hand.setDurability((short) newdur);
								if (newdur < 50) {
									ArrayList<String> lore = new ArrayList<String>();
									lore.add("Uses: " + newdur + "/50");
									lore.add("WARNING: The durability bar isn't accurate.");
									in_hand_meta.setLore(lore);
									in_hand.setItemMeta(in_hand_meta);
								}
								if (newdur == 50) {
									player.setItemInHand(null);
									player.playSound(player_loc,
											Sound.ITEM_BREAK, 10, 1);
								}
							}
						}
					}
					if (in_hand_meta.getDisplayName().equalsIgnoreCase("Ender Bow")) { // Ender
																				// Bow
						evt.setCancelled(true);
						player.updateInventory();
						if (player_inventory.contains(Material.ARROW)) {
							player.playSound(player_loc, Sound.SHOOT_ARROW, 10,
									1);
							player.launchProjectile(EnderPearl.class)
									.setVelocity(velocity_arrow);
							ItemStack item = player.getItemInHand();
							int current = item.getDurability();
							if (current < 100) {
								ArrayList<String> lore = new ArrayList<String>();
								lore.add("Uses: " + current + "/100");
								lore.add("WARNING: The durability bar is for reload time.");
								in_hand_meta.setLore(lore);
								item.setItemMeta(in_hand_meta);
							}
							if (current == 100) {
								player.setItemInHand(null);
								player.playSound(player_loc, Sound.ITEM_BREAK,
										10, 1);
							}
						}
					}
					if (in_hand_meta.getDisplayName().equalsIgnoreCase("TNT Bow")) { // TNT
																				// Bow
						evt.setCancelled(true);
						player.updateInventory();
						if (player_inventory.contains(Material.ARROW)
								&& player_inventory.contains(Material.TNT)) {
							player.playSound(player_loc, Sound.SHOOT_ARROW, 10,
									1);
							TNTPrimed tnt = (TNTPrimed) world.spawn(direction,
									TNTPrimed.class);
							tnt.setVelocity(velocity_toss);
							ItemStack item = player.getItemInHand();
							int current = item.getDurability();
							if (current < 100) {
								ArrayList<String> lore = new ArrayList<String>();
								lore.add("Uses: " + current + "/100");
								lore.add("WARNING: The durability bar is for reload time.");
								in_hand_meta.setLore(lore);
								item.setItemMeta(in_hand_meta);
							}
							if (current == 100) {
								player.setItemInHand(null);
								player.playSound(player_loc, Sound.ITEM_BREAK,
										10, 1);
							}
						}
					}

					if (in_hand_meta.getDisplayName().equalsIgnoreCase("Shotbow")) { // Shot
																				// Bow
						if (player_inventory.contains(Material.ARROW)) {
							evt.setCancelled(true);
							player.playSound(player_loc, Sound.SHOOT_ARROW, 10,
									1);
							world.spawnArrow(direction, velocity_arrow, speed,
									0F).setShooter(player);
							world.spawnArrow(direction, velocity_arrow, speed,
									3F).setShooter(player);
							world.spawnArrow(direction, velocity_arrow, speed,
									6F).setShooter(player);
							world.spawnArrow(direction, velocity_arrow, speed,
									9F).setShooter(player);
							world.spawnArrow(direction, velocity_arrow, speed,
									12F).setShooter(player);
							world.spawnArrow(direction, velocity_arrow, speed,
									15F).setShooter(player);
							world.spawnArrow(direction, velocity_arrow, speed,
									18F).setShooter(player);
							world.spawnArrow(direction, velocity_arrow, speed,
									21F).setShooter(player);
							world.spawnArrow(direction, velocity_arrow, speed,
									24F).setShooter(player);
							world.spawnArrow(direction, velocity_arrow, speed,
									27F).setShooter(player);
							ItemStack item = player.getItemInHand();
							int current = item.getDurability();
							int newdur = current + 1;
							item.setDurability((short) newdur);
							if (newdur < 100) {
								ArrayList<String> lore = new ArrayList<String>();
								lore.add("Uses: " + newdur + "/100");
								lore.add("WARNING: The durability bar isn't accurate.");
								in_hand_meta.setLore(lore);
								item.setItemMeta(in_hand_meta);
							}
							if (newdur == 100) {
								player.setItemInHand(null);
								player.playSound(player_loc, Sound.ITEM_BREAK,
										10, 1);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onDisable() {
		getLogger().info("CustomWeapons has successfully been disabled.");

	}
}
