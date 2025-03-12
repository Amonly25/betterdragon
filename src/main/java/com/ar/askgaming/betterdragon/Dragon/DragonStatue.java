package com.ar.askgaming.betterdragon.Dragon;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.util.EulerAngle;

import com.ar.askgaming.betterdragon.BetterDragon;

import net.md_5.bungee.api.ChatColor;

public class DragonStatue {

	private ArmorStand statue;
	private Location location;

    private BetterDragon plugin;
    public DragonStatue(BetterDragon main) {

        plugin = main;
		load();
    }
	public void load(){
		location = plugin.getConfig().getLocation("statue.location");
		if (location == null){
			return;
		}
		if (statue != null){
			statue.remove();
		}
		
		spawn(location);
	}
	public void move(Location l){
		location = l;
		plugin.getConfig().set("statue.location", l);
		plugin.saveConfig();
		if (statue == null){
			spawn(l);
			return;
		}
		statue.teleport(l);
	}
	public void remove(){
		if (statue == null){
			return;
		}
		statue.remove();
	}

    public void spawn(Location l) {

        statue = (ArmorStand) l.getWorld().spawnEntity(l,EntityType.ARMOR_STAND);

        FileConfiguration conf = plugin.getConfig();

        statue.setArms(true);
		statue.setBasePlate(conf.getBoolean("statue.baseplate"));
		statue.setGravity(conf.getBoolean("statue.gravity"));
		String name = conf.getString("statue.name").replace("%player%", plugin.getDragon().getKillerName());
		statue.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
		statue.setCustomNameVisible(conf.getBoolean("statue.name_visible"));
		statue.setSmall(conf.getBoolean("statue.small"));

		statue.setRotation(location.getYaw(), location.getPitch());

        statue.setLeftArmPose(new EulerAngle(
				Math.toRadians(conf.getInt("statue.leftarm.x")), 
				Math.toRadians(conf.getInt("statue.leftarm.y")), 
				Math.toRadians(conf.getInt("statue.leftarm.z"))));
		
		statue.setRightArmPose(new EulerAngle(
				Math.toRadians(conf.getInt("statue.rightarm.x")), 
				Math.toRadians(conf.getInt("statue.rightarm.y")), 
				Math.toRadians(conf.getInt("statue.rightarm.z"))));
		
		statue.setLeftLegPose(new EulerAngle(
				Math.toRadians(conf.getInt("statue.leftleg.x")), 
				Math.toRadians(conf.getInt("statue.leftleg.y")), 
				Math.toRadians(conf.getInt("statue.leftleg.z"))));

		statue.setRightLegPose(new EulerAngle(
				Math.toRadians(conf.getInt("statue.rightleg.x")), 
				Math.toRadians(conf.getInt("statue.rightleg.y")), 
				Math.toRadians(conf.getInt("statue.rightleg.z"))));
		
		statue.setHeadPose(new EulerAngle(
				Math.toRadians(conf.getInt("statue.head.x")), 
				Math.toRadians(conf.getInt("statue.head.y")), 
				Math.toRadians(conf.getInt("statue.head.z"))));
		
		statue.getEquipment().setBoots(new ItemStack(Material.valueOf(conf.getString("statue.boots"))));
		statue.getEquipment().setLeggings(new ItemStack(Material.valueOf(conf.getString("statue.leggings"))));
		statue.getEquipment().setChestplate(new ItemStack(Material.valueOf(conf.getString("statue.chestplate"))));
		statue.getEquipment().setItemInMainHand(new ItemStack(Material.valueOf(conf.getString("statue.mainhand"))));
		statue.getEquipment().setItemInOffHand(new ItemStack(Material.valueOf( conf.getString("statue.offhand"))));
		
		ItemStack head = makeSkull();
					
		statue.getEquipment().setHelmet(head);

    }

    private ItemStack makeSkull() {

        // Crear el item de la cabeza de jugador
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

		String player = "Steve";
		String killer = plugin.getDragon().getKillerName();

		if (killer != null && !killer.isBlank()){
			player = killer;
		}

		PlayerProfile pf = plugin.getServer().createPlayerProfile(player);
        meta.setOwnerProfile(pf);

        skull.setItemMeta(meta);
        return skull;
    }  

	public void updateStatue(){

		if (statue == null){
			return;
		}

		String name = plugin.getDragon().getKillerName();
		statue.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
		ItemStack head = makeSkull();
		statue.getEquipment().setHelmet(head);
	}

}
