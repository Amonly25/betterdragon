package com.ar.askgaming.betterdragon.Dragon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.ar.askgaming.betterdragon.BetterDragon;

public class DragonData {

    private Double health;
    private String name;
    private String killerName;
    private long respawnTime;
    private Location respawnLocacion;
    private long deathTime;

    private BetterDragon plugin;
    public DragonData(BetterDragon main) {
        plugin = main;

        FileConfiguration conf = plugin.getConfig();

        name = conf.getString("dragon.name", "Ender Dragon").replace("&", "§");
        health = conf.getDouble("dragon.health",400);
        respawnLocacion = conf.getLocation("data.respawn_location");
        killerName =  conf.getString("data.killer","Steave");
        deathTime = conf.getLong("data.deathtime",0);
        respawnTime = conf.getLong("respawn.cooldown",1400);
    }
    public void setName(String name) {
        plugin.getConfig().set("dragon.name", name);
        plugin.saveConfig();
        this.name = name;
    }
    public void setHealth(Double health) {
        plugin.getConfig().set("dragon.health", health);
        plugin.saveConfig();
        this.health = health;
    }
    public void setRespawnTime(long respawnTime) {
        plugin.getConfig().set("respawn.cooldown", respawnTime);
        plugin.saveConfig();
        this.respawnTime = respawnTime;
    }
    public String getKillerName() {
        return killerName;
    }
    public void setKiller(String killerName) {
        this.killerName = killerName;
        String text = plugin.getLang("messages.onkill").replace("%player%", killerName);		
        Bukkit.broadcastMessage(text);
        plugin.getStatue().updateStatue();
        plugin.getConfig().set("data.killer", killerName);
        plugin.saveConfig();

    }
    public void setRespawnLocacion(Location respawnLocacion) {
        this.respawnLocacion = respawnLocacion;
        plugin.getConfig().set("data.respawn_location", respawnLocacion);
        plugin.saveConfig();
    }
    public void setDeathTime(long deathTime) {
        this.deathTime = deathTime;
        plugin.getConfig().set("data.deathtime", deathTime);
        plugin.saveConfig();
    }
    public long getRespawnTime() {
        return respawnTime;
    }
    public Double getHealth() {
        return health;
    }
    public String getName() {
        return name;
    }
    public Location getRespawnLocacion() {
        return respawnLocacion;
    }
    public long getDeathTime() {
        return deathTime;
    }
}
