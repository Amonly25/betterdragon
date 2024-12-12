package com.ar.askgaming.betterdragon.Dragon;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.util.EulerAngle;

import com.ar.askgaming.betterdragon.BetterDragon;

import net.md_5.bungee.api.ChatColor;

public class DragonStatue {

	private String UUID;

    private BetterDragon plugin;
    public DragonStatue(BetterDragon main) {

        plugin = main;

		if (plugin.getDataHandler().getDragonDataConfig().get("statue") != null) {
			UUID = plugin.getDataHandler().getDragonDataConfig().getString("statue");
		}
    }

	private ArmorStand statue;

	public ArmorStand getArmorStand(){
        Bukkit.getWorlds().forEach(world ->{

			for (LivingEntity entity : world.getLivingEntities()) {
                if (entity instanceof ArmorStand) {

                   if (entity.getUniqueId().toString().replace("!!java.util.UUID ", "").equals(UUID)){
						statue = (ArmorStand) entity;
						break;
				   }
                }
            }
        });        
		return statue;
	}

    public void spawn(Location l, Player player) {

        ArmorStand statue = (ArmorStand) l.getWorld().spawnEntity(l,EntityType.ARMOR_STAND);

        FileConfiguration conf = plugin.getConfig();

        statue.setArms(true);
		statue.setBasePlate(conf.getBoolean("statue.baseplate"));
		statue.setGravity(conf.getBoolean("statue.gravity"));
		String name = conf.getString("statue.name").replace("%player%", plugin.getDragon().getKillerName());
		statue.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
		statue.setCustomNameVisible(conf.getBoolean("statue.name_visible"));
		statue.setSmall(conf.getBoolean("statue.small"));

		String id = statue.getUniqueId().toString().replace("!!java.util.UUID ", "");
		UUID = id;
		plugin.getDataHandler().getDragonDataConfig().set("statue", id);
		plugin.getDataHandler().saveDragonDataConfig();


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

		String player = "null";

		if (plugin.getDragon().getKillerName() != null){
			player = plugin.getDragon().getKillerName();
		}

		PlayerProfile pf = plugin.getServer().createPlayerProfile(player);
        meta.setOwnerProfile(pf);

        skull.setItemMeta(meta);
        return skull;
    }  

	public void updateStatue(){

		if (getArmorStand() == null) return;
		ArmorStand statue = getArmorStand();

		String name = plugin.getConfig().getString("statue.name").replace("%player%", plugin.getDragon().getKillerName());
		statue.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
		ItemStack head = makeSkull();
		statue.getEquipment().setHelmet(head);
	}

}
