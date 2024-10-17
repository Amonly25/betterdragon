package com.ar.askgaming.betterdragon;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.joml.Random;

public class DragonAbilities {

    private enum Abilities {
        LIGHTNING,
        DODGE,
        IMPULSE,
        EXPLOSION,
        ENDER_ATTACK
    }

    private Main plugin;
    public DragonAbilities(Main main) {
        plugin = main;
    }

    public void createCounterAttack(EntityDamageByEntityEvent event, Player damager) {

        Abilities ability = getRandom();
        String name = ability.toString().toLowerCase();

        double skillChance = plugin.getConfig().getDouble("abilities.counter_attacks." + name + ".chance");
        String skillMessage = "abilities.counter_attacks." + name + ".msg_to_player";
        Integer skillRadius = plugin.getConfig().getInt(("abilities.counter_attacks." + name + ".radius"));

        double random = Math.random();

        if (skillChance >= random) {

            if (!plugin.getConfig().getString(skillMessage).equalsIgnoreCase("")) {
                damager.sendMessage(plugin.getLang(skillMessage));
            }

            EnderDragon d = (EnderDragon) event.getEntity();
            Location loc = damager.getLocation();

            switch (ability) {
                case LIGHTNING:

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

                    damager.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20, 0));
                break;
                case DODGE:
                    Vector v = loc.toVector().normalize();
    				        				        			
					damager.playSound(loc, Sound.ITEM_SHIELD_BLOCK, 3, 1);
					d.launchProjectile(Arrow.class, v.multiply(2));
					event.setCancelled(true);

                break;
                case IMPULSE:
                    event.setCancelled(true);
                    damager.playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 3, 1);
                    damager.playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 3, 1);
                    damager.setVelocity(loc.getDirection().multiply(-3));
                    damager.damage(10.0);
                break;
                case EXPLOSION:
                    damager.playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 3, 1);
                    d.setPhase(Phase.LEAVE_PORTAL);
                    loc.getWorld().createExplosion(loc, 5, true);
                break;
                case ENDER_ATTACK:

                    for (Entity entity : damager.getNearbyEntities(skillRadius, skillRadius, skillRadius)) {
                        if (entity instanceof Enderman) {
                            ((Enderman) entity).setTarget(damager);
                        }
                    }
                    d.setPhase(Phase.LAND_ON_PORTAL);
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                break;
            }
        }
    }

    private Abilities getRandom() {
        Abilities[] values = Abilities.values();
        int length = values.length;
        int randomIndex = new Random().nextInt(length);
        return values[randomIndex];
    }
}
