package com.ar.askgaming.betterdragon.Abilities;

import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.s;

import com.ar.askgaming.betterdragon.BetterDragon;

public class Timed extends BukkitRunnable{

    private BetterDragon plugin;

    public Timed(BetterDragon main) {
        this.plugin = main;

        runTaskTimer(main, 20*5, 20*5);
    }

    @Override
    public void run() {
        
        List<EnderDragon> dragons = plugin.getDragonManager().getDragonsAlive();

        for (EnderDragon dragon : dragons) {
            Player player = getNearbyPlayer(dragon, 96);

            if (player != null) {
                attackPlayer(dragon, player);
            }
        }

    }

    private Player getNearbyPlayer(EnderDragon dragon, int radius) {

        for (Entity entity : dragon.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) {
                return (Player) entity;
            }
        }
        return null;
    }
    private void attackPlayer(EnderDragon dragon, Player player) {

        new BukkitRunnable() {
            int times = 0;
            @Override
            public void run() {
                times++;
                if (times >= 20) {
                    shootFireballBurst(dragon, player);
                    cancel();
                    return;
                }
                spawnParticleSphere(getDragonHeadLocation(dragon), Particle.FLAME, Color.RED, 1);
            }
        }.runTaskTimer(plugin, 0, 1);
    }
    public void shootFireball(EnderDragon dragon, Player target) {


        Location dragonLoc = dragon.getLocation();
        Location playerLoc = target.getLocation();
        World world = dragon.getWorld();

        // Vector de dirección desde el dragón hacia el jugador
        Vector direction = playerLoc.toVector().subtract(dragonLoc.toVector()).normalize();

        // Crear y lanzar la bola de fuego
        Fireball fireball = world.spawn(dragonLoc.add(direction.multiply(2)), Fireball.class);
        fireball.setDirection(direction);
        fireball.setYield(3F); // Tamaño de la explosión
        fireball.setIsIncendiary(false); // Evita incendios

        dragon.getWorld().playSound(dragonLoc, "entity.ender_dragon.growl", 2.0F, 1.0F);
    }
    public void spawnParticleSphere(Location center, Particle particle, Color color, double radius) {
        if (center == null || particle == null) return;
    
        World world = center.getWorld();
        if (world == null) return;
    
        Random random = new Random();
    
        // Calculamos la cantidad de partículas según el tamaño de la esfera
        int points = (int) (radius * radius * 50); // Cuanto más grande, más partículas
    
        for (int i = 0; i < points; i++) {
            double theta = random.nextDouble() * 2 * Math.PI; // Ángulo en el plano XY
            double phi = random.nextDouble() * Math.PI; // Ángulo vertical
    
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.cos(phi);
            double z = radius * Math.sin(phi) * Math.sin(theta);
    
            Location particleLoc = center.clone().add(x, y, z);
    
            if (particle == Particle.DUST) {
                Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1.0F);
                world.spawnParticle(Particle.DUST, particleLoc, 1, dustOptions);
            } else {
                world.spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
            }
        }
    }
    public Location getDragonHeadLocation(EnderDragon dragon) {
        if (dragon == null) return null;
    
        Location baseLocation = dragon.getLocation();
        
        // Ajustamos la altura de la cabeza (≈ 5 bloques más alto)
        double headHeight = 2.0; 
        
        // Obtener dirección de movimiento (vector de velocidad)
        Vector direction = dragon.getVelocity().normalize();
        
        // Si no tiene movimiento, usamos su dirección de rotación
        if (direction.lengthSquared() < 0.01) {
            direction = baseLocation.getDirection().normalize();
        }
        
        // Mover la posición de la cabeza 2 bloques hacia adelante
        Location headLocation = baseLocation.add(direction.multiply(7)).add(0, headHeight, 0);
        
        return headLocation;
    }
    
    public void shootFireballBurst(EnderDragon dragon, Player target) {
        new BukkitRunnable() {
            int shots = 0;
    
            @Override
            public void run() {
                if (shots >= 3) {
                    cancel();
                    return;
                }
    
                shootFireball(dragon, target);
                shots++;
            }
        }.runTaskTimer(plugin, 0L, 10L); // Dispara cada 10 ticks (0.5 segundos)
    }
 

}
