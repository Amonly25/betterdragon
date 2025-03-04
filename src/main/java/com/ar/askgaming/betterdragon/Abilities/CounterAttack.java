package com.ar.askgaming.betterdragon.Abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.joml.Random;

import com.ar.askgaming.betterdragon.BetterDragon;

import net.md_5.bungee.api.ChatColor;

public class CounterAttack {

    private List<String> abilities = new ArrayList<>();

    private BetterDragon plugin;
    public CounterAttack(BetterDragon main) {
        plugin = main;

        ConfigurationSection abilities = plugin.getConfig().getConfigurationSection("abilities");
        if (abilities == null) {
            plugin.getLogger().warning("No abilities found in config.yml");
            return;
        }
        for (String key : abilities.getKeys(false)) {
            this.abilities.add(key);
            //Bukkit.getLogger().info("Ability: " + key);
        }
    }

    public void createCounterAttack(EntityDamageByEntityEvent event, Player damager) {

        FileConfiguration config = plugin.getConfig();
        String ability = getRandom();

        String name = ability.toString().toLowerCase();

        double skillChance = config.getDouble("abilities." + name + ".chance",0.3);
        String skillMessage = config.getString("abilities." + name + ".msg_to_player","");

        double random = Math.random();

        EnderDragon d = (EnderDragon) event.getEntity();

        if (skillChance >= random) {

            if (!skillMessage.equals("")) {
                damager.sendMessage(ChatColor.translateAlternateColorCodes('&', skillMessage));
            }

            boolean superLightining = config.getBoolean("abilities." + name + ".super_lightning",false);
            if (superLightining) {
                superLightining(damager);
            }
            boolean blockDamage = config.getBoolean("abilities." + name + ".block_damage",false);
            if (blockDamage) {
                event.setCancelled(true);
            }

            int knockback = config.getInt("abilities." + name + ".knockback_power",0);
            if (knockback > 0) {
                damager.setVelocity(damager.getLocation().getDirection().multiply(-knockback));
            }

            double damage = config.getDouble("abilities." + name + ".damage_player",0);
            if (damage > 0) {
                damager.damage(damage);
            }

            boolean explosion = config.getBoolean("abilities." + name + ".explosion",false);
            if (explosion) {
                damager.getWorld().createExplosion(damager.getLocation(), 5, true);
            }
            
            boolean enderAttack = config.getBoolean("abilities." + name + ".ender_attack",false);
            if (enderAttack) {
                endermanAttack(damager, d);
            }

            playSoundIfExist(name, damager);  
            addPotionEffect(name, damager);
            sendCommandIfExist(name, damager);
        }
    }
    private void endermanAttack(Player damager, EnderDragon d) {
        for (Entity entity : damager.getNearbyEntities(20, 20, 20)) {
            if (entity instanceof Enderman) {
                ((Enderman) entity).setTarget(damager);
            }
        }
        // for future feature
        d.setPhase(Phase.LAND_ON_PORTAL);
    }
    private void superLightining(Player damager) {
        Location loc = damager.getLocation();
        damager.getWorld().strikeLightning(loc);

        Location[] surroundingLocations = new Location[] {
            loc.clone().add(3, 0, 0), // 2 bloques al este
            loc.clone().add(-3, 0, 0), // 2 bloques al oeste
            loc.clone().add(0, 0, 3), // 2 bloques al sur
            loc.clone().add(0, 0, -3) // 2 bloques al sur
        };

        for (Location l : surroundingLocations) {
            damager.getWorld().strikeLightning(l);
        }
    }
    private void sendCommandIfExist(String name, Player damager) {
        List<String> commands = plugin.getConfig().getStringList("abilities." + name + ".commands");
        if (commands != null) {
            for (String command : commands) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%player%", damager.getName()));
            }
        }
    }
    private void addPotionEffect(String name, Player damager) {
        List<String> effects = plugin.getConfig().getStringList("abilities." + name + ".effects");
        if (effects != null) {
            for (String effect : effects) {
                String[] split = effect.split(":");
                try {
                    int duration = Integer.parseInt(split[1]);
                    int amplifier = Integer.parseInt(split[2]);
                    PotionEffectType type = org.bukkit.Registry.EFFECT.match(split[0].toUpperCase()); // Check if the effect is in the

                    damager.addPotionEffect(new PotionEffect(type, duration, amplifier));
                } catch (Exception e) {
                    plugin.getLogger().warning("Wrong potion effect format: " + effect);
                    e.printStackTrace();
                }
            }
        }
    }
    private void playSoundIfExist(String name, Player damager) {
        List<String> sound = plugin.getConfig().getStringList("abilities." + name + ".sounds");
        if (sound != null) {
            for (String s : sound) {
                try {
                    damager.playSound(damager.getLocation(), Sound.valueOf(s.toUpperCase()), 3, 1);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid sound: " + s);
                    return;
                }
            }
        }
    }
    private String getRandom() {
        int length = abilities.size();
        int randomIndex = new Random().nextInt(length);
        return abilities.get(randomIndex);
    }
}
