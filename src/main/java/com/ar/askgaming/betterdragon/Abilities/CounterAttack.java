package com.ar.askgaming.betterdragon.Abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.joml.Random;

import com.ar.askgaming.betterdragon.BetterDragon;

import net.md_5.bungee.api.ChatColor;

public class CounterAttack {

    private List<String> abilities = new ArrayList<>();

    private BetterDragon plugin;
    public CounterAttack(BetterDragon main) {
        plugin = main;

        load();
    }
    public void load(){
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

    public void createCounterAttack(EnderDragon dragon, Player damager) {

        if (damager.getGameMode().equals(GameMode.CREATIVE)) {
            return;

        }

        FileConfiguration config = plugin.getConfig();
        String ability = getRandom();

        String name = ability.toString().toLowerCase();

        double skillChance = config.getDouble("abilities." + name + ".chance",0.3);
        String skillMessage = config.getString("abilities." + name + ".msg_to_player","");

        double random = Math.random();

        if (skillChance >= random) {

            if (!skillMessage.equals("")) {
                damager.sendMessage(ChatColor.translateAlternateColorCodes('&', skillMessage));
            }

            boolean superLightining = config.getBoolean("abilities." + name + ".super_lightning",false);
            if (superLightining) {
                superLightining(damager);
            }

            int knockback = config.getInt("abilities." + name + ".knockback_power",0);
            if (knockback > 0) {
                Vector velocity = damager.getLocation().getDirection().multiply(-knockback); // Retroceso horizontal
                velocity.setY(knockback); // Aumentar la componente Y para empuje vertical
                damager.setVelocity(velocity);
                damager.getWorld().spawnEntity(damager.getLocation(), EntityType.WIND_CHARGE);
            }

            double damage = config.getDouble("abilities." + name + ".damage_player",0);
            if (damage > 0) {
                damager.damage(damage);
            }

            boolean explosion = config.getBoolean("abilities." + name + ".explosion",false);
            if (explosion) {
                damager.getWorld().createExplosion(damager.getLocation(), 2, true);
            }
            
            boolean enderAttack = config.getBoolean("abilities." + name + ".ender_attack",false);
            if (enderAttack) {
                endermanAttack(damager, dragon);
            }

            boolean fireBall = config.getBoolean("abilities." + name + ".fireball",false);
            if (fireBall) {
                shootBall(dragon, damager, Fireball.class);
            }

            boolean dragonBall = config.getBoolean("abilities." + name + ".dragon_fireball",false);
            if (dragonBall) {
                shootBall(dragon, damager, DragonFireball.class);
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

    public void shootBall(EnderDragon dragon, Player target, Class<? extends Fireball> fireballClass) {

        Location dragonLoc = dragon.getLocation();
        Location playerLoc = target.getLocation();
        World world = dragon.getWorld();

        // Vector de dirección desde el dragón hacia el jugador
        Vector direction = playerLoc.toVector().subtract(dragonLoc.toVector()).normalize();

        // Crear y lanzar la bola de fuego
        Fireball fireball = world.spawn(dragonLoc.add(direction.multiply(2)), fireballClass);
        fireball.setDirection(direction);
        fireball.setYield(3F); // Tamaño de la explosión
        fireball.setIsIncendiary(true); // Evita incendios

    }
}
