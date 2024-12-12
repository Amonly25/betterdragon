package com.ar.askgaming.betterdragon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;


public class Commands implements TabExecutor{

    private BetterDragon plugin;
    public Commands(BetterDragon main) {
        plugin = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        List<String> result = new ArrayList<String>();

        if (args.length == 1) {
            result = new ArrayList<>(Arrays.asList("kill", "respawn","set_respawn","set_statue","tp","set_time","top","set_name","set_health","reload_config","next"));
        }

        return result;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length == 0) {
            sender.sendMessage("Use /command <subcommand>");
            return true;
        }
        Player p = null;
        if (sender instanceof Player){
            p = (Player)sender;
        }

        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "kill":
                    if (!plugin.getDragon().getDragonsAlive().isEmpty()){
                        for (EnderDragon e : plugin.getDragon().getDragonsAlive()){
                            e.setHealth(0);
                        }
                        if (p != null){
                            plugin.getDragon().setKiller("Server");
                        }
                    } else sender.sendMessage("There is not an alive dragon.");

                break;

                case "tp":
                    if (!plugin.getDragon().getDragonsAlive().isEmpty()){
                        if (p != null){
                            p.teleport(plugin.getDragon().getDragonsAlive().get(0));
                        }
                    } else sender.sendMessage("There is not an alive dragon.");
                break;

                case "top":
                    if (plugin.getDataHandler().getKillsLeaderboard() != null) {
                        
                        String prefix = plugin.getLang("leaderboard.prefix");
                        String separator = plugin.getLang("leaderboard.separator");
                        String suffix = plugin.getLang("leaderboard.suffix");
                        
                        final int MAX_RESULTS = 10;
                        final AtomicInteger counter = new AtomicInteger(0);
                        
                        plugin.getDataHandler().getKillsLeaderboard().forEach((name, kills) -> {
                            if (counter.getAndIncrement() < MAX_RESULTS) {
                                sender.sendMessage(prefix + name + separator + kills + suffix);
                            } else {
                                return;
                            }
                        });;
                    }
                break;

                case "test":

                /* 
                EnderDragon d = plugin.getDragon().getDragonsAlive().get(0);
                DragonFireball fireball = d.launchProjectile(DragonFireball.class);
                Location playerLocation = p.getLocation();
                Location dragonLocation = playerLocation.clone().add(0, -3, 0);
                Vector direction = dragonLocation.toVector().subtract(d.getLocation().toVector()).normalize();
                fireball.setVelocity(direction.multiply(0.5));
                */

                break;

                case "set_respawn":
                    if (p != null){
                        plugin.getDragon().setRespawnLocacion(((Player) sender).getLocation());
                        sender.sendMessage("New respawn location set.");
                    } else sender.sendMessage("This command must be sended by a player.");
                break;

                case "respawn":
                    if (plugin.getDragon().getRespawnLocacion() != null){
                        Location l = plugin.getDragon().getRespawnLocacion();
                        Bukkit.getWorld("world_the_end").spawnEntity(l, EntityType.ENDER_DRAGON);
                    } else sender.sendMessage("Please, setup a new respawn location first.");	
                break;

                case "reload_config":
                    plugin.reloadConfig();
                    sender.sendMessage("This will reload the config.yml file only.");
                    sender.sendMessage("To change the dragon configuration, use the commands.");
                break;

                case "next":
                    sender.sendMessage(plugin.getDragon().getNext());
                break;

                case "set_statue":
                    if (p != null){
                        plugin.getStatue().spawn(p.getLocation(), p);
                        sender.sendMessage("Statue spawned.");
                    } else sender.sendMessage("This command must be sended by a player.");
                break;
            
                default:
                sender.sendMessage("Invalid Command type.");
                break;
            }
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "set_time":
                    try {
                        long l = Long.parseLong(args[1]);
                        plugin.getDragon().setRespawnTime(l);
                        sender.sendMessage("Respawn time set to " + l + " minutes");
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Invalid number. Please enter a valid number.");
                    }
                break;
                case "set_health":
                    try {
                        double d = Double.parseDouble(args[1]);
                        plugin.getDragon().setHealth(d);
                        sender.sendMessage("Health set to " + d);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Invalid number. Please enter a valid number.");
                    }
                 break;
                case "set_name":
                    plugin.getDragon().setName(args[1]);
                    sender.sendMessage("Name set to " + args[1]);
                break;
                default:
                    sender.sendMessage("Invalid Command type or and argument is missing.");
            break;
            }
        }
        return true;
    }
}
