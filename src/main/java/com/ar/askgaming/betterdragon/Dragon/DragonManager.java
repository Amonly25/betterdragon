package com.ar.askgaming.betterdragon.Dragon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
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
    public boolean isDragonAlive(World world){
        for (LivingEntity entity : world.getLivingEntities()) {
            if (entity instanceof EnderDragon) {
                return true;
            }
        }
        return false;
    }

    HashMap<EnderDragon, HashMap<Player, Double>> dragonDamagers = new HashMap<>();

    public HashMap<EnderDragon, HashMap<Player, Double>> getDragonDamagers() {
        return dragonDamagers;
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
    public void checkDamagersAndReward(EnderDragon dragon){
        HashMap<Player, Double> damagers = dragonDamagers.get(dragon);

        if (damagers != null && !damagers.isEmpty()) {
            // Crear una lista de las entradas del mapa
            List<Map.Entry<Player, Double>> damagerList = new ArrayList<>(damagers.entrySet());

            // Ordenar la lista por los valores (daño) en orden descendente
            damagerList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        
            // Obtener el jugador con más daño (top 1)
            Map.Entry<Player, Double> topDamager = damagerList.remove(0); // Remover y obtener el primer elemento
            Player topPlayer = topDamager.getKey();
            //double topDamage = topDamager.getValue();
            proccesRewards(topPlayer, topPlayer.getLocation(), "top_damager");

            // Crear una lista con el resto de los jugadores (sin el top 1)
            List<Map.Entry<Player, Double>> remainingDamagers = new ArrayList<>(damagerList);

            // Mostrar o usar los resultados
           // Bukkit.broadcastMessage("Top damager: " + topPlayer.getName() + " with " + topDamage + " damage.");
           // Bukkit.broadcastMessage("Other players and their damage:");
            for (Map.Entry<Player, Double> entry : remainingDamagers) {
                //Bukkit.broadcastMessage(entry.getKey().getName() + " - " + entry.getValue() + " damage.");
                proccesRewards(entry.getKey(), entry.getKey().getLocation(), "all_damagers");
            }
        }
    }


    public void proccesCustomDrops(Player p, Location loc){
        FileConfiguration cfg = plugin.getConfig();
        // Custom drops
        for (String key : cfg.getConfigurationSection("custom_drops").getKeys(false)){
            double chance = Math.random();
            ItemStack item = plugin.getConfig().getItemStack("custom_drops." + key + ".item");

            if (item != null && cfg.getDouble("custom_drops." + key + ".chance") >= chance) {
                String text = cfg.getString("custom_drops." + key + ".broadcast_text");
                if (!text.equals("")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', text));
                }
                loc.getWorld().dropItemNaturally(loc, item);
            }
        }
    }
    public void proccesRewards(Player p, Location loc, String path){

        FileConfiguration cfg = plugin.getConfig();
        
        //Handling rewards              

        for (String key : cfg.getConfigurationSection("rewards."+path).getKeys(false)){
            double chance = Math.random();
            String txt = cfg.getString("rewards."+path+ "." + key + ".broadcast_text");
            List<String> drop = cfg.getStringList("rewards."+path+ "." + key + ".drops");
            List<String> commands = cfg.getStringList("rewards."+path+ "." + key + ".commands");
                                
            if (cfg.getDouble("rewards." +path+ "." + key + ".chance") >= chance) {
                                    
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
    public List<Location> getCrystalLocations() {
        Object obj = plugin.getConfig().get("crystals");
        
        if (obj instanceof List<?>) {
            List<?> genericList = (List<?>) obj;
            List<Location> locationList = new ArrayList<>();
            
            for (Object item : genericList) {
                if (item instanceof Location) {
                    locationList.add((Location) item); // Casting seguro
                } else {
                    plugin.getLogger().warning("Invalid entry in 'crystals' configuration: " + item);
                }
            }
            return locationList;
        }
        
        return new ArrayList<>(); // Si no es una lista, devuelve una lista vacía
    }
    
    public void addCrystal(Location location) {
        List<Location> list = getCrystalLocations();
        list.add(location);
        plugin.getConfig().set("crystals", list);
        plugin.saveConfig();
    }
    public void respawnCrystals() {
        List<Location> locs = plugin.getDragonManager().getCrystalLocations();
        if (!locs.isEmpty()){
            plugin.getDragonManager().getCrystalLocations().forEach(loc -> loc.getWorld().spawnEntity(loc, EntityType.END_CRYSTAL));
            return;
        }
    }
}
