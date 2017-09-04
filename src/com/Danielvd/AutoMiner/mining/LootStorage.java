package com.Danielvd.AutoMiner.mining;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.robotricker.transportpipes.api.PipeAPI;
import de.robotricker.transportpipes.api.PlayerDestroyPipeEvent;
import de.robotricker.transportpipes.pipes.PipeUtils;
import de.robotricker.transportpipes.pipeutils.ContainerBlockUtils;
import net.md_5.bungee.api.ChatColor;

public class LootStorage {
	
	private static HashMap<UUID, Chest> firstChest = new HashMap<UUID, Chest>();
	private static HashMap<UUID, Chest> secondChest = new HashMap<UUID, Chest>();

	//Add item to chests
	public LootStorage(Collection<ItemStack> drops, Player player) {
		
		Chest chest1 = (Chest) firstChest.get(player.getUniqueId());
		Chest chest2 = (Chest) secondChest.get(player.getUniqueId());
		
		for (ItemStack itemStack : drops) {
			if (chest1 != null && chest2 != null) {
				if (chest1.getLocation().getBlock().getType() == Material.CHEST && 
						chest2.getLocation().getBlock().getType() == Material.CHEST) {
					//chests are valid
					if (isChestNotFull(itemStack, chest1)) {
						chest1.getBlockInventory().addItem(itemStack);
						continue;
					} else {
						if (isChestNotFull(itemStack, chest2)) {
							chest2.getBlockInventory().addItem(itemStack);

						} else {
							player.sendMessage(ChatColor.RED + "Your miner's chest is full, your miner will now stop!");
							PlayerMiner playerMiner = new PlayerMiner();
							playerMiner.disableMinerList(player);
							
							firstChest.remove(player.getUniqueId());
							secondChest.remove(player.getUniqueId());
							return;
						}
					}
					return;
				} else {
					player.sendMessage(ChatColor.RED + "The miner chest was removed, your miner will now stop!");
					
					PlayerMiner playerMiner = new PlayerMiner();
					playerMiner.disableMinerList(player);
					break;
				}
			} else {
				player.sendMessage(ChatColor.RED + "The miner chest was removed, your miner will now stop!");
				return;
			}
		}
	}
	
	//Place chests
	public LootStorage(Block block, BlockFace blockFace, Player player, boolean vertical) {
		if (vertical) {
			Location loc1 = null;
			Location loc2 = null;
			
			Block chestBlock1 = block.getRelative(BlockFace.UP);
			
			loc1 = chestBlock1.getLocation().add(0, 2, 2);
			loc2 = loc1.getBlock().getRelative(BlockFace.EAST).getLocation();
			
			loc1.getBlock().setType(Material.CHEST);
			loc2.getBlock().setType(Material.CHEST);
			
			Chest chest1 = (Chest) loc1.getBlock().getState();
			Chest chest2 = (Chest) loc2.getBlock().getState();

			//Compatibility with TransportPipes: sync chest blocks with TransportPipes
			if(Bukkit.getServer().getPluginManager().getPlugin("TransportPipes") != null) {
				ContainerBlockUtils.updatePipeNeighborBlockSync(loc1.getBlock(), true);
				ContainerBlockUtils.updatePipeNeighborBlockSync(loc2.getBlock(), true);
			}
			
			firstChest.put(player.getUniqueId(), chest1);
			secondChest.put(player.getUniqueId(), chest2);
			
			return;
		}
		
		BlockFace face = blockFace;
		
		Location loc1 = null;
		Location loc2 = null;
		
		switch (face) {
		case NORTH:
			loc1 = block.getLocation().add(0, 0, 3);
			loc2 = loc1.getBlock().getRelative(BlockFace.EAST).getLocation();
			break;
		case EAST:
			loc1 = block.getLocation().add(-3, 0, 0);
			loc2 = loc1.getBlock().getRelative(BlockFace.SOUTH).getLocation();
			break;
		case SOUTH:
			loc1 = block.getLocation().add(0, 0, -3);
			loc2 = loc1.getBlock().getRelative(BlockFace.WEST).getLocation();
			break;
		case WEST:
			loc1 = block.getLocation().add(3, 0, 0);
			loc2 = loc1.getBlock().getRelative(BlockFace.NORTH).getLocation();
			break;
		default:
			break;
		}
		
		Block chestBlock1 = loc1.getBlock();
		
		Block chestBlock2 = loc2.getBlock();
		
		chestBlock1.setType(Material.CHEST);
		chestBlock2.setType(Material.CHEST);
		
		Chest chest1 = (Chest) chestBlock1.getState();
		Chest chest2 = (Chest) chestBlock2.getState();
		
		//Compatibility with TransportPipes: sync chest blocks with TransportPipes
		if(Bukkit.getServer().getPluginManager().getPlugin("TransportPipes") != null) {
			ContainerBlockUtils.updatePipeNeighborBlockSync(chestBlock1, true);
			ContainerBlockUtils.updatePipeNeighborBlockSync(chestBlock2, true);
		}
		
		firstChest.put(player.getUniqueId(), chest1);
		secondChest.put(player.getUniqueId(), chest2);
	}
	
	//Check whether chests are full
	private boolean isChestNotFull(ItemStack itemToAdd, Chest chest) { 
	    int foundcount = itemToAdd.getAmount();
	    for (ItemStack stack : chest.getBlockInventory().getContents()) {
	        if (stack == null) {
	        	foundcount -= itemToAdd.getMaxStackSize();
	        	return true;
	        }
	        	
	        if (stack.getType() == itemToAdd.getType()) {
	            if (stack.getDurability() == itemToAdd.getDurability()) {
	                foundcount -= itemToAdd.getMaxStackSize() - stack.getAmount();
	            }
	        }
	    }
	    boolean canContainitem = foundcount <= 0;
		return canContainitem;
	}
	
}
