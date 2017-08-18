package com.Danielvd.AutoMiner.mining;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.Danielvd.AutoMiner.mining.miner.HorizontalMiner;
import com.Danielvd.AutoMiner.mining.miner.VerticalMiner;

import net.md_5.bungee.api.ChatColor;

public class Mine {
	
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
		
		//Get blockface to determine whether we should go vertical or horizontal
		List<Block> lastBlocks = player.getLastTwoTargetBlocks(null, 5);
		BlockFace face = lastBlocks.get(1).getFace(lastBlocks.get(0));
		
		if (face == BlockFace.UP) {
			new VerticalMiner(player, targetBlock, length);
		} else if (face == BlockFace.DOWN) {
			player.sendMessage(ChatColor.RED + "Please target this block from above or from aside!");
			playerMiner.removePlayerFromMinersList(player);
		} else {
			//Start a horizontal miner
			new HorizontalMiner(player, targetBlock, 
					face.getOppositeFace()/*mine must start on other side of block*/, length);
		}
		
		//TODO Might be for later use//BlockFace face = yawToFace(player.getEyeLocation().getYaw(), false);
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
