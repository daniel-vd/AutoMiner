package com.Danielvd.AutoMiner.mining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.Danielvd.AutoMiner.mining.miner.HorizontalMiner;
import com.Danielvd.AutoMiner.mining.miner.VerticalMiner;

import net.md_5.bungee.api.ChatColor;

public class Mine {
	
	BlockFace targetBlockFace;
	BlockFace additionalBlockFace1;
	BlockFace additionalBlockFace2;
	
	Block block2, block3, block4, block5, block6;
	
	 //Blockfaces for yawToFace()
	private static final BlockFace[] blockFaces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};
	private static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
	
	public Mine(Block targetBlock, Player player, int length/*in seconds*/) {
		
		//Target block must be iron block to determine if it's a miner, and whether it's horizontal or vertical
		if (targetBlock.getType() != Material.IRON_BLOCK) {
			player.sendMessage(ChatColor.RED + "Please target an iron block that is part of a miner!");
			return;
		}
		
		for (BlockFace blockFace : blockFaces) {
			if (targetBlock.getRelative(blockFace).getType() == Material.WOOD) {
				targetBlockFace = blockFace;
				
				block2 = targetBlock.getRelative(blockFace);
				break;
			}
		}
		
		if (block2 == null) {
			player.sendMessage(ChatColor.RED + "Please target an iron block that is part of a miner!");
			return;
		}
		
		block5 = block2.getRelative(BlockFace.UP);
		block6 = block2.getRelative(BlockFace.DOWN);
		

		if (targetBlockFace != BlockFace.UP) {
			//Miner will be horizontal
			//Determine the side blocks
			if (targetBlockFace == BlockFace.NORTH || targetBlockFace == BlockFace.SOUTH) {
				additionalBlockFace1 = BlockFace.EAST;
				additionalBlockFace2 = BlockFace.WEST;
				
				block3 = block2.getRelative(additionalBlockFace1);
				block4 = block2.getRelative(additionalBlockFace2);
				
			} else {
				additionalBlockFace1 = BlockFace.NORTH;
				additionalBlockFace2 = BlockFace.SOUTH;
				block3 = block2.getRelative(additionalBlockFace1);
				block4 = block2.getRelative(additionalBlockFace2);
			}
			
		} else {
			block3 = block2.getRelative(BlockFace.EAST);
			block4 = block2.getRelative(BlockFace.WEST);
			block5 = block2.getRelative(BlockFace.SOUTH);
			block6 = block2.getRelative(BlockFace.NORTH);
		}
		
		if (block2.getType() != Material.WOOD || block3.getType() != Material.WOOD || block4.getType() != Material.WOOD ||
				block5.getType() != Material.WOOD || block6.getType() != Material.WOOD) {
			player.sendMessage(ChatColor.RED + "This is not a valid miner!");
			return;
		}
		
		List<Block> blocks = new ArrayList<Block>();
		
		//Better than a for loop..?
		blocks.add(targetBlock);
		blocks.add(block2);
		blocks.add(block3);
		blocks.add(block4);
		blocks.add(block5);
		blocks.add(block6);
		
		
		//Make sure player does not have a miner already
		PlayerMiner playerMiner = new PlayerMiner();
		
		if (playerMiner.playerHasMiner(player)) {
			player.sendMessage(ChatColor.RED + "You already have a mine running!"); //TODO configurable messages
			return;
		}
		
		//Player does't have a miner, so now add to miner List to avoid having multiple mines
		playerMiner.addPlayerToMinersList(player);
		
		BlockFace face = targetBlockFace;
		
		if (face == BlockFace.UP) {
			new VerticalMiner(player, blocks, length);
		} else if (face == BlockFace.DOWN) {
			player.sendMessage(ChatColor.RED + "Please target this block from above or from aside!");
			playerMiner.removePlayerFromMinersList(player);
		} else {
			//Start a horizontal miner
			new HorizontalMiner(player, blocks, 
					face.getOppositeFace()/*mine must start on other side of block*/, length);
		}
		
		//TODO Might be for later use//BlockFace face = yawToFace(player.getEyeLocation().getYaw(), false);
	}
	
	public Mine() {
		// TODO Auto-generated constructor stub
	}

}
