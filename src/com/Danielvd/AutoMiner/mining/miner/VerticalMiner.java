package com.Danielvd.AutoMiner.mining.miner;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.Danielvd.AutoMiner.AutoMiner;
import com.Danielvd.AutoMiner.mining.LootStorage;
import com.Danielvd.AutoMiner.mining.PlayerMiner;

import net.md_5.bungee.api.ChatColor;

public class VerticalMiner {
	
	Block mainBlock;
	Block additionalBlock1;
	Block additionalBlock2;
	
	BlockFace additionalBlockFace1;
	BlockFace additionalBlockFace2;
	
	int id;
	int timesRan = 0;
	
	boolean run = true;
	
	public VerticalMiner (Player player, Block targetBlock, int length) {
		//We don't need a blockface because it must always be UP
		
		@SuppressWarnings("unused")
		PlayerMiner playerMiner = new PlayerMiner();
		
		mainBlock = targetBlock;
		
		//Place chests
		new LootStorage(mainBlock, BlockFace.UP, player, true);
		
		player.sendMessage(ChatColor.BLUE + "Your vertical miner will start now and continue for " + length + " seconds!");
		
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(AutoMiner.class), new Runnable() {
            @Override
            public void run() {
            	timesRan++;
            	
            	if (timesRan == (length + 1)) {
        			Bukkit.getScheduler().cancelTask(id);
        			playerMiner.removePlayerFromMinersList(player);
        			player.sendMessage(ChatColor.BLUE + "Your miner has stopped after " + length + " seconds, you can now create a new one!");
        			return;
            	}
            	
            	Block block = null; //TODO Probably not necessary
            	
            	//Update the run bool every run
            	run = playerMiner.disableMiner(player);

            	for (int i = 1; i < 11; i++) {
            		
            		if (!run) {
            			Bukkit.getScheduler().cancelTask(id);
            			break;
            		}
            			//For each of the 9 blocks determine location and place down
	            	switch (i) {
	            		case 1:
	            			block = mainBlock;
	            			break;
	            		case 2:
	            			block = mainBlock.getRelative(BlockFace.SOUTH);
	            			additionalBlock1 = mainBlock.getRelative(BlockFace.SOUTH);
	            			break;
	            		case 3:
	            			block = mainBlock.getRelative(BlockFace.NORTH);
	            			additionalBlock2 = mainBlock.getRelative(BlockFace.NORTH);
	            			break;
	            		case 4:
	            			block = mainBlock.getRelative(BlockFace.EAST);
	            			break;
	            		case 5:
	            			block = mainBlock.getRelative(BlockFace.WEST);
	            			break;
	            		case 6:
	            			block = additionalBlock1.getRelative(BlockFace.EAST);
	            			break;
	            		case 7:
	            			block = additionalBlock1.getRelative(BlockFace.WEST);
	            			break;
	            		case 8:
	            			block = additionalBlock2.getRelative(BlockFace.EAST);
	            			break;
	            		case 9:
	            			block = additionalBlock2.getRelative(BlockFace.WEST);
	            			break;
	            		case 10:
	                    	mainBlock = mainBlock.getRelative(BlockFace.DOWN);
	            			break;
	            	}
	            	
	            	if (block.getType() == Material.BEDROCK) {
	            		player.sendMessage(ChatColor.RED + "Your miner hit a bedrock block and must now stop!");
	            		playerMiner.disableMinerList(player);
	            		break;
	            	}
	            	
	            	Collection<ItemStack> drops = block.getDrops();
	            		
	            	new LootStorage(drops, player);
	            		
	            	block.setType(Material.AIR);
            	}
            }
		}, 0L, 20L); //20 ticks = 1 second between every run		
	}
	
}
