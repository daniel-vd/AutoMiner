package com.Danielvd.AutoMiner.mining.miner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class HorizontalMiner {
	
	BlockFace additionalBlockFace1;
	BlockFace additionalBlockFace2;
	
	Block additionalBlock1;
	Block additionalBlock2;
	Block mainBlock;
	
	List<Block> blocks2 = new ArrayList<Block>();
	
	int id;
	int timesRan = 0;
	
	boolean run = true;
	
	Block targetBlock;
	Block block2, block3, block4, block5, block6;
	
	public HorizontalMiner (Player player, List<Block> blocks, BlockFace face, int length) {
		PlayerMiner playerMiner = new PlayerMiner();
		
		targetBlock = blocks.get(0);
		
		mainBlock = targetBlock.getRelative(face);
		
		//Determine the two side blocks
		if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
			additionalBlockFace1 = BlockFace.EAST;
			additionalBlockFace2 = BlockFace.WEST;
		} else {
			additionalBlockFace1 = BlockFace.NORTH;
			additionalBlockFace2 = BlockFace.SOUTH;
		}
		
		//Place chests
		new LootStorage(mainBlock, face, player, false);
		
		player.sendMessage(ChatColor.BLUE + "Your horizontal miner will start now and continue for " + length + " seconds!");
		
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
	            			block = mainBlock.getRelative(additionalBlockFace1);
	            			additionalBlock1 = mainBlock.getRelative(additionalBlockFace1);
	            			break;
	            		case 3:
	            			block = mainBlock.getRelative(additionalBlockFace2);
	            			additionalBlock2 = mainBlock.getRelative(additionalBlockFace2);
	            			break;
	            		case 4:
	            			block = mainBlock.getRelative(BlockFace.DOWN);
	            			break;
	            		case 5:
	            			block = mainBlock.getRelative(BlockFace.UP);
	            			break;
	            		case 6:
	            			block = additionalBlock1.getRelative(BlockFace.DOWN);
	            			break;
	            		case 7:
	            			block = additionalBlock1.getRelative(BlockFace.UP);
	            			break;
	            		case 8:
	            			block = additionalBlock2.getRelative(BlockFace.DOWN);
	            			break;
	            		case 9:
	            			block = additionalBlock2.getRelative(BlockFace.UP);
	            			break;
	            		case 10:
	                    	mainBlock = mainBlock.getRelative(face);
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
            	           	
            	Location loc = null;//TODO temporary shitty variable;
            	
            	if (blocks.isEmpty()) {
            		for (Block tempBlock : blocks2) {
            			blocks.add(tempBlock);
            		}
            	}
            	
            	blocks2.clear();
            	
            	//Move miner blocks
            	for (Block minerBlock : blocks) {
						if (face == BlockFace.EAST) {
							if (minerBlock.getType() == Material.IRON_BLOCK) {
								minerBlock.setType(Material.AIR);
								loc = minerBlock.getLocation().add(1, 0, 0);
								loc.getBlock().setType(Material.IRON_BLOCK);
							} else {
							//Block is wood
							minerBlock.setType(Material.AIR);
							loc = minerBlock.getLocation().add(1, 0, 0);
							loc.getBlock().setType(Material.WOOD);
							}
						} else if (face == BlockFace.NORTH) {
							if (minerBlock.getType() == Material.IRON_BLOCK) {
								minerBlock.setType(Material.AIR);
								loc = minerBlock.getLocation().add(0, 0, -1);
								loc.getBlock().setType(Material.IRON_BLOCK);
							} else {
							//Block is wood
							minerBlock.setType(Material.AIR);
							loc = minerBlock.getLocation().add(0, 0, -1);
							loc.getBlock().setType(Material.WOOD);
							}
						} else if (face == BlockFace.SOUTH) {
							if (minerBlock.getType() == Material.IRON_BLOCK) {
								minerBlock.setType(Material.AIR);
								loc = minerBlock.getLocation().add(0, 0, 1);
								loc.getBlock().setType(Material.IRON_BLOCK);
							} else {
							//Block is wood
							minerBlock.setType(Material.AIR);
							loc = minerBlock.getLocation().add(0, 0, 1);
							loc.getBlock().setType(Material.WOOD);
							}
						} else if (face == BlockFace.WEST) {
							if (minerBlock.getType() == Material.IRON_BLOCK) {
								minerBlock.setType(Material.AIR);
								loc = minerBlock.getLocation().add(-1, 0, 0);
								loc.getBlock().setType(Material.IRON_BLOCK);
							} else {
							//Block is wood
							minerBlock.setType(Material.AIR);
							loc = minerBlock.getLocation().add(-1, 0, 0);
							loc.getBlock().setType(Material.WOOD);
							}
						} else {
							player.sendMessage(ChatColor.RED + "Something went wrong, please rejoin or restart the server.");
							break;
						}
            		
            		blocks2.add(loc.getBlock());
            		
            	}
            	
            	blocks.clear();
            	
            }
		}, 0L, 20L); //20 ticks = 1 second between every run
	}

}
