package com.Danielvd.AutoMiner.mining;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
	public LootStorage(Block block, BlockFace blockFace, Player player) {
		BlockFace face = blockFace.getOppositeFace();
		Block chestBlock1 = block.getRelative(face);
		chestBlock1 = chestBlock1.getRelative(face);
		chestBlock1 = chestBlock1.getRelative(BlockFace.DOWN);
		Block chestBlock2 = chestBlock1.getRelative(BlockFace.EAST);
		
		chestBlock1.setType(Material.CHEST);
		chestBlock2.setType(Material.CHEST);
		
		Chest chest1 = (Chest) chestBlock1.getState();
		Chest chest2 = (Chest) chestBlock2.getState();
		
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
