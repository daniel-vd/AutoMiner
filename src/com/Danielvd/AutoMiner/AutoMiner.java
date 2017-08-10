package com.Danielvd.AutoMiner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.Danielvd.AutoMiner.mining.Mine;
import com.Danielvd.AutoMiner.mining.PlayerMiner;
import com.Danielvd.AutoMiner.utils.Metrics;
import com.Danielvd.AutoMiner.utils.Updater;

import net.md_5.bungee.api.ChatColor;

public class AutoMiner extends JavaPlugin implements CommandExecutor {
	
	public Server server = Bukkit.getServer();
	
    public ConsoleCommandSender console = server.getConsoleSender();
    
    //For the update checker
    PluginDescriptionFile pdf = this.getDescription();

	@Override
	public void onEnable() {
		//Register commands
		getCommand("am").setExecutor(this);
		
		new Metrics(this);
		
		//Show some text in console
        getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            public void run() {
        	    checkUpdate();
            }
        }, 30L); //Wait for everything to finish
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("am")) {
				if (args.length == 0) {
					if(sender.hasPermission("AutoMiner.main") || sender.isOp()){
						//Show some help
						player.sendMessage(ChatColor.BLUE + "AutoMiner commands:");
						player.sendMessage(ChatColor.BLUE + "/am on <length in seconds>: Start a miner");
						player.sendMessage(ChatColor.BLUE + "/am off: Disable your miner");
						return true;
					} else {
						player.sendMessage(ChatColor.RED + "You are not allowed to do that!");
					}
				} else if (args[0].equalsIgnoreCase("on")) {
					if (args.length > 1) {
						if (isInt(args[1])) {
							if(sender.hasPermission("AutoMiner.miner") || sender.isOp()){
								Block block = player.getTargetBlock(null, 5);
								
								if (block.getType() == Material.AIR) {
									player.sendMessage(ChatColor.RED + "Make sure to look at a valid block!");
									return true;
								}
								
								new Mine(block, player, Integer.parseInt(args[1]));
								return true;
							} else {
								player.sendMessage(ChatColor.RED + "You are not allowed to do that!");
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("off")) {
					if(sender.hasPermission("AutoMiner.miner") || sender.isOp()){
						PlayerMiner playerMiner = new PlayerMiner();
						
						playerMiner.disableMinerList(player);
						return true;
					} else {
						player.sendMessage(ChatColor.RED + "You are not allowed to do that!");
					}
				} else {
					player.sendMessage(ChatColor.RED + "That command does not exist!");
				}
			}
		}
		return true;
	}
	
	//Check if command argument is integer
	public static boolean isInt(String s) {
	    try {
	        Integer.parseInt(s);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	//Update checker
	public void checkUpdate() {
		console.sendMessage(ChatColor.DARK_AQUA + "Checking for AutoMiner updates...");
        final Updater updater = new Updater(this, 45442/*plugin id*/, false);
        final Updater.UpdateResult result = updater.getResult();
        switch (result) {
            case FAIL_SPIGOT: {
            	console.sendMessage(ChatColor.RED + "ERROR: The AutoMiner update checker could not contact Spigotmc.org");
                break;
            }
            case NO_UPDATE: {
            	console.sendMessage(ChatColor.DARK_AQUA + "The AutoMiner update checker works fine!");
            	console.sendMessage(ChatColor.GREEN + "You have the latest AutoMiner version!");
            	console.sendMessage(ChatColor.DARK_AQUA + "Current version: " + pdf.getVersion());
                break;
            }
            case UPDATE_AVAILABLE: {
                String version = updater.getVersion();
            	console.sendMessage(ChatColor.DARK_AQUA + "The AutoMiner updater works fine!");
                console.sendMessage(ChatColor.GREEN + "An AutoMiner update is found!");
                console.sendMessage(ChatColor.DARK_AQUA + "Your version: " + pdf.getVersion() + ". Newest Version: " + version);
                @SuppressWarnings("unused")
				Boolean updateAvailable = true;
                break;
            }
            default: {
                console.sendMessage(result.toString());
                break;
            }
        }
}
	
}
