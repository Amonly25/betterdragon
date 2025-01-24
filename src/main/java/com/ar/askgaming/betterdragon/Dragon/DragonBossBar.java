package com.ar.askgaming.betterdragon.Dragon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.ar.askgaming.betterdragon.BetterDragon;

public class DragonBossBar {

    private BetterDragon plugin;
    public DragonBossBar(BetterDragon plugin) {
        this.plugin = plugin;

        Bukkit.getOnlinePlayers().forEach(this::checkToBossBar);
    }

    private HashMap<EnderDragon, BossBar> bossBars = new HashMap<>();


    public void checkToBossBar(Player player) {

        String mode = plugin.getConfig().getString("respawn.mode", "default");
        if (mode.equalsIgnoreCase("default")){
            return;

        }
        if (plugin.getConfig().getBoolean("disable_bossbar", false)) {
            return;
        }

        World world = player.getWorld();

        List<EnderDragon> dragons = getDragonsAliveInWorld(world);

        bossBars.forEach((dragon, bossBar) -> {
            if (!dragons.contains(dragon)) {
                //Bukkit.broadcastMessage("Dragon not found, removing boss bar");
                bossBar.removePlayer(player);
            }
        });
        //Bukkit.broadcastMessage("size: " + dragons.size());
        if (dragons.size() > 0) {
            for (EnderDragon dragon : dragons) {
                //Bukkit.broadcastMessage("Dragon found, adding boss bar");
                BossBar bossBar = getOrCreateBossBar(dragon);
                bossBar.addPlayer(player);
            }
        }
    }

    public List<EnderDragon> getDragonsAliveInWorld(World world) {

        List<EnderDragon> list = new ArrayList<>();

        for (LivingEntity entity : world.getLivingEntities()) {

            if (entity instanceof EnderDragon) {
                list.add((EnderDragon) entity);
            }
        }
        return list;
    }

    private BossBar getOrCreateBossBar(EnderDragon dragon) {
        if (bossBars.containsKey(dragon)) {
            return bossBars.get(dragon);
        }

        BossBar bossBar = plugin.getServer().createBossBar("Dragon", BarColor.PURPLE, BarStyle.SOLID);
        plugin.getDragonManager().updateBossBar(bossBar);
        bossBars.put(dragon, bossBar);

        return bossBar;
    }
    public void updateBossBar(EnderDragon dragon) {
        BossBar bossBar = getOrCreateBossBar(dragon);
        if (bossBar != null) {
            double maxHealth = dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            double dragHealth = Math.max(0, dragon.getHealth()); // Asegurarse de que la salud no sea negativa
            double barHealthValue = dragHealth / maxHealth;
            barHealthValue = Math.max(0, Math.min(1, barHealthValue)); // Limitar el valor entre 0 y 1
            bossBar.setProgress(barHealthValue); // Actualiza la barra de progreso
        }
    }
    

    public void removeBossBar(EnderDragon dragon) {
        BossBar bossBar = bossBars.remove(dragon);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void removeAllBossBars() {
        bossBars.values().forEach(BossBar::removeAll);
        bossBars.clear();
    }
}
