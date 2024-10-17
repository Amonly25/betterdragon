package com.ar.askgaming.betterdragon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Dragon {

    private Double health;
    public void setHealth(Double health) {
        plugin.getDataHandler().getDragonDataConfig().set("dragon.health", health);
        plugin.getDataHandler().saveDragonDataConfig();
        getDragonsAlive().forEach(dragon -> {
            dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(plugin.getDragon().getHealth());
            dragon.setHealth(plugin.getDragon().getHealth());
            updateBossBar(dragon, 0);
        });
        this.health = health;
    }
    private String name;
    public void setName(String name) {
        plugin.getDataHandler().getDragonDataConfig().set("dragon.name", name);
        plugin.getDataHandler().saveDragonDataConfig();
        getDragonsAlive().forEach(dragon -> {
            dragon.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
            getBossBar(dragon).setTitle(name);
        });
        this.name = name;
    }
    private String killerName;
    private long respawnTime;

    public long getRespawnTime() {
        return respawnTime;
    }
    public void setRespawnTime(long respawnTime) {
        plugin.getDataHandler().getDragonDataConfig().set("respawn_time", respawnTime);
        plugin.getDataHandler().saveDragonDataConfig();
        this.respawnTime = respawnTime;
    }
    public String getKillerName() {
        if (killerName == null) {
            return "";
        }   else return killerName;
    }
    public void setKiller(String killerName) {
        this.killerName = killerName;
        String text = plugin.getLang("messages.onkill").replace("%player%", killerName);		
        Bukkit.broadcastMessage(text);
        plugin.getStatue().updateStatue();
        plugin.getDataHandler().getDragonDataConfig().set("killer", killerName);
        plugin.getDataHandler().saveDragonDataConfig();
    }
    // private Boolean alive;
    private Location respawnLocacion;

    public void setRespawnLocacion(Location respawnLocacion) {
        this.respawnLocacion = respawnLocacion;
        plugin.getDataHandler().getDragonDataConfig().set("respawn_location", respawnLocacion);
        plugin.getDataHandler().saveDragonDataConfig();
    }
    private long deathTime;

    public void setDeathTime(long deathTime) {
        this.deathTime = deathTime;
        plugin.getDataHandler().getDragonDataConfig().set("deathtime", deathTime);
        plugin.getDataHandler().saveDragonDataConfig();
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
    private Main plugin;
    public Dragon(Main main) {
        plugin = main;

        // Initialize dragon properties here
        FileConfiguration conf = plugin.getDataHandler().getDragonDataConfig();

        name = ChatColor.translateAlternateColorCodes('&', conf.getString("dragon.name"));
        health = conf.getDouble("dragon.health");

        respawnLocacion = conf.getLocation("respawn_location");

        killerName =  conf.getString("killer");

        deathTime = conf.getLong("deathtime");
        respawnTime = conf.getLong("respawn_time");

        getDragonsAlive().forEach(eDragon -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (eDragon.getWorld().equals(player.getWorld())){
                    getBossBar(eDragon).addPlayer(player);
                }
            });
        });
    }

    public List<EnderDragon> getDragonsAlive() {

        List<EnderDragon> list = new ArrayList<>();

        Bukkit.getWorlds().forEach(world ->{

            for (LivingEntity entity : world.getLivingEntities()) {
            
                if (entity instanceof EnderDragon) {
                    list.add((EnderDragon) entity);
                }
            }
        });        
        return list;
    }

    private HashMap<UUID, BossBar> BossBars = new HashMap<>();

    public HashMap<UUID, BossBar> getBossBars() {
        return BossBars;
    }
    public BossBar getBossBar(EnderDragon dragon){

        BossBar bossBar;

        if (dragon.getBossBar() != null){
            //Check and set color
            return dragon.getBossBar();
        } else if (BossBars.containsKey(dragon.getUniqueId())) {
            return BossBars.get(dragon.getUniqueId());
        }
        BarFlag[] flags = new BarFlag[]{BarFlag.PLAY_BOSS_MUSIC,BarFlag.DARKEN_SKY};
        BarColor color = BarColor.valueOf(plugin.getConfig().getString("options.bossbar_color"));
        bossBar = Bukkit.getServer().createBossBar(dragon.getCustomName(), color, BarStyle.SOLID, flags);
        BossBars.put(dragon.getUniqueId(), bossBar);
        updateBossBar(dragon, 0);
        return bossBar;
    }

    public void updateBossBar(EnderDragon dragon, double value){
        double MaxHealth = ((Attributable) dragon).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double DragHealth = ((Damageable) dragon).getHealth();
        double BarHealthValue = (DragHealth + value) / MaxHealth;
        
        if (BarHealthValue < 0.0D) BarHealthValue = 0.0D; {
            
            if (BarHealthValue > 1.0D) BarHealthValue = 1.0D;  {
                getBossBar(dragon).setProgress(BarHealthValue);
            }
        }
    }

    public String getNext(){
        long actualTime = System.currentTimeMillis() / 60000;
        long deathTime = getDeathTime();
        long respawnTime = getRespawnTime();
        long time = respawnTime - (actualTime - deathTime);

        if (time < 0) {time = 0;}

        long hours = time / 60;
        long minutes = time % 60;
        String text = plugin.getLang("messages.next_dragon").replace("%hours%", String.valueOf(hours)).replace("%min%", String.valueOf(minutes));
    
        return text;

    }
}
