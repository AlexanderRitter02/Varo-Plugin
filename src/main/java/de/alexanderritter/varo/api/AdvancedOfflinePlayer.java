package de.alexanderritter.varo.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import de.alexanderritter.varo.main.Varo;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public class AdvancedOfflinePlayer {
	
	Varo plugin;
	File nbtdat;
	NBTTagCompound playernbt;
	
	public AdvancedOfflinePlayer(Varo plugin, UUID uuid) throws FileNotFoundException, IOException {
		nbtdat = new File(Bukkit.getWorldContainer()+ File.separator + plugin.getSettings().getVaroWorld().getName() + "/playerdata/" + uuid.toString() + ".dat");
		playernbt = NBTCompressedStreamTools.a(new FileInputStream(nbtdat));
	}
	
	public Location getLocationInt() {
		BigInteger addifneg = new BigInteger("18446744073709551616");
		long uuidmost = playernbt.getLong("WorldUUIDMost");
		long uuidleast = playernbt.getLong("WorldUUIDLeast");
		if(uuidmost < 0) uuidmost = addifneg.add(BigInteger.valueOf(uuidmost)).longValue();
		if(uuidleast < 0) uuidleast = addifneg.add(BigInteger.valueOf(uuidleast)).longValue();
		String worldUUID = UUIDs.convertFromTrimmed(Long.toHexString(uuidmost) + Long.toHexString(uuidleast));
		
		NBTTagList pos = (NBTTagList) playernbt.get("Pos");
		World world = Bukkit.getWorld(UUID.fromString(worldUUID));
		int x = Double.valueOf(pos.getString(0)).intValue();
		int y = Double.valueOf(pos.getString(1)).intValue();
		int z = Double.valueOf(pos.getString(2)).intValue();
		
		return new Location(world, x, y, z);
	}
	
	public Inventory getInventory() {
		System.out.println("1: " + playernbt.get("Inventory"));
		if(playernbt.get("Inventory") instanceof NBTTagList) {
			System.out.println("2: Is a NBTTagList");
			NBTTagList inventoryList = (NBTTagList) playernbt.get("Inventory");
			System.out.println("3: " + inventoryList);
			Inventory inventory = Bukkit.createInventory(null, 108);
			for(int i = 0; i < inventoryList.size(); i++) {
				if(inventoryList.get(i) instanceof NBTTagCompound) {
					ItemStack test = ItemStack.createStack(inventoryList.get(i));
					org.bukkit.inventory.ItemStack itemstack = CraftItemStack.asBukkitCopy(test);
					inventory.setItem(inventoryList.get(i).getByte("Slot"), itemstack);
				}
			}
			return inventory;
		}
		return null;
	}
	
}
