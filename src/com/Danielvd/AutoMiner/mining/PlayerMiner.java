package com.Danielvd.AutoMiner.mining;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class PlayerMiner {

	private static List<UUID> playerMiners = new ArrayList<UUID>();
	private static List<UUID> disableMiner = new ArrayList<UUID>();
	
	public boolean playerHasMiner(Player player) {
		//Player has a mine if true
		if (playerMiners.contains(player.getUniqueId())) {	
			return true;
		}
		return false;
	}
	
	public void addPlayerToMinersList(Player player) {
		if (!playerMiners.contains(player.getUniqueId()))
			playerMiners.add(player.getUniqueId());
	}
	
	public void removePlayerFromMinersList(Player player) {
		if (playerMiners.contains(player.getUniqueId())) {
			playerMiners.remove(player.getUniqueId());
		} else {
			player.sendMessage(ChatColor.RED + "Something went wrong, please rejoin and/or restart the server!");
		}
	}
	
	public void disableMinerList(Player player) {
		if (playerMiners.contains(player.getUniqueId())) {
			if (!disableMiner.contains(player.getUniqueId())) {
				disableMiner.add(player.getUniqueId());
				playerMiners.remove(player.getUniqueId());
			}
		} else {
			player.sendMessage(ChatColor.RED + "You don't have a running miner!");
		}
	}
	
	public boolean disableMiner(Player player) {
		if (disableMiner.contains(player.getUniqueId())) {
			disableMiner.remove(player.getUniqueId());
			
			player.sendMessage(ChatColor.BLUE + "Your miner is turned off now!");
			return false;
		}
		return true;
	}
	
}
