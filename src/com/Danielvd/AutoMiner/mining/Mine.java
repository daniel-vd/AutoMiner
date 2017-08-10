package com.Danielvd.AutoMiner.mining;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.Danielvd.AutoMiner.AutoMiner;

import net.md_5.bungee.api.ChatColor;

public class Mine {
	
	Block mainBlock;
	Block additionalBlock1;
	Block additionalBlock2;
	
	int id;
	int timesRan = 0;
	
	boolean run = true;
	
	BlockFace additionalBlockFace1;
	BlockFace additionBlockFace2;
	
	 //Blockfaces for yawToFace()
	private static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	private static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
	
	public Mine(Block targetBlock, Player player, int length/*in seconds*/) {
		
		//Make sure player does not have a miner already
		PlayerMiner playerMiner = new PlayerMiner();
		
		boolean hasMiner = false;
		
		if (playerMiner.playerHasMiner(player)) {
			hasMiner = true;
		}
		
		if (hasMiner) {
			player.sendMessage(ChatColor.RED + "You already have a mine running!"); //TODO configurable messages
			return;
		}
		
		//Player does't have a miner, so now add to miner List to avoid having multiple mines
		playerMiner.addPlayerToMinersList(player);
		
		BlockFace face = yawToFace(player.getEyeLocation().getYaw(), false);
		
		//First scheduler run must use targetBlock as main block;
		mainBlock = targetBlock;
		
		if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
			additionalBlockFace1 = BlockFace.EAST;
			additionBlockFace2 = BlockFace.WEST;
		} else {
			additionalBlockFace1 = BlockFace.NORTH;
			additionBlockFace2 = BlockFace.SOUTH;
		}
		
		//Place chests
		new LootStorage(targetBlock, face, player);
		
		player.sendMessage(ChatColor.BLUE + "Your miner will start now and continue for " + length + " seconds!");
		
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
	            			block = mainBlock.getRelative(additionBlockFace2);
	            			additionalBlock2 = mainBlock.getRelative(additionBlockFace2);
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
	            		
	            	Collection<ItemStack> drops = block.getDrops();
	            		
	            	new LootStorage(drops, player);
	            		
	            	block.setType(Material.AIR);
            	}
            }
		}, 0L, 20L); //20 ticks = 1 second between every run
	}
	
	public Mine() {
		// TODO Auto-generated constructor stub
	}

	//Determine BlockFace from player yaw
	public BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections)
            return radial[Math.round(yaw / 45f) & 0x7].getOppositeFace();
     
        return axis[Math.round(yaw / 90f) & 0x3].getOppositeFace();
    }

}
