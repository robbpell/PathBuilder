package com.robbpell;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
/**
 * Hello world!
 *
 */
public class Main extends JavaPlugin {
 
    @Override
    public void onEnable(){
        System.out.println("Enabling this plugin works.");
    }
 
    @Override
    public void onDisable(){
        System.out.println("Disabling this plugin works.");
    }
    
    /**
     *
     * @param sender
     * @param cmd
     * @param commandLabel
     * @param arg
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String arg[]) {
        final Player player = (Player) sender;
        
        if(commandLabel.equalsIgnoreCase("test")) {
            
            BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                PathBuilder.Find(player);
                }
            });
            
            
        }
        return false;
    }
}