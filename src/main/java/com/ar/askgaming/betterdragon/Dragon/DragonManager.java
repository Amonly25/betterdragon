package com.ar.askgaming.betterdragon.Dragon;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.betterdragon.BetterDragon;

import net.md_5.bungee.api.ChatColor;

public class DragonManager {

    private BetterDragon plugin;
    public DragonManager(BetterDragon plugin) {
        this.plugin = plugin;
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
    public String getNext(){
        long actualTime = System.currentTimeMillis() / 60000;
        long deathTime = plugin.getDragon().getDeathTime();
        long respawnTime = plugin.getDragon().getRespawnTime();
        long time = respawnTime - (actualTime - deathTime);

        if (time < 0) {time = 0;}

        long hours = time / 60;
        long minutes = time % 60;
        String text = plugin.getLang("messages.next_dragon").replace("%hours%", String.valueOf(hours)).replace("%min%", String.valueOf(minutes));
    
        return text;

    }
    public void newDragonBattle(){
        Location l = plugin.getDragon().getRespawnLocacion();
        if (l != null && l.getWorld().getEnvironment() == Environment.THE_END){
            l.getWorld().getEnderDragonBattle().initiateRespawn(null);
            return;
        }
    }
    public void updateBossBar(BossBar bossBar){
        BarColor color;
        try {
            color = BarColor.valueOf(plugin.getConfig().getString("dragon.bossbar_color"));
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid bossbar color, using default color");
            color = BarColor.PURPLE;
        }
        bossBar.setColor(color);

        String title = plugin.getDragon().getName();
        bossBar.setTitle(title);
    
    }
    public void proccesRewards(Player p, Location loc){

        FileConfiguration cfg = plugin.getConfig();

        //Handling drops
        double randDoubleEgg = Math.random(), randDoubleHead = Math.random(), chance = Math.random();
        
        if (cfg.getDouble("drops.egg") >= randDoubleEgg) {
        
            p.sendMessage(plugin.getLang("messages.dropegg"));
            loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.DRAGON_EGG));
        }
        
        if (cfg.getDouble("drops.head") >= randDoubleHead) {	

            p.sendMessage(plugin.getLang("messages.drophead"));
            loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.DRAGON_HEAD));
        }
        // Custom drops
        for (String key : cfg.getConfigurationSection("custom_drops").getKeys(false)){
            ItemStack item = plugin.getConfig().getItemStack("custom_drops." + key + ".item");
            String text = cfg.getString("custom_drops." + key + ".broadcast_text");
            if (!text.equals("")) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', text));
            }
            if (item != null && cfg.getDouble("custom_drops." + key + ".chance") >= chance) {
                loc.getWorld().dropItemNaturally(loc, item);
            }
        }

        //Handling rewards              

        for (String key : cfg.getConfigurationSection("rewards").getKeys(false)){
            
            String txt = cfg.getString("rewards." + key + ".broadcast_text");
            List<String> drop = cfg.getStringList("rewards." + key + ".drops");
            List<String> commands = cfg.getStringList("rewards." + key + ".commands");
                                
            if (cfg.getDouble("rewards." + key + ".chance") >= chance) {
                                    
                if (!txt.equals("")){ 
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', txt));
                }
                for (String item : drop) {
                    loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.valueOf(item)));
                }
                
                for (String s : commands) {						
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", p.getName()));
                }
            }
        }
    }

}
