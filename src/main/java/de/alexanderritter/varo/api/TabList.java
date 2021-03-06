package de.alexanderritter.varo.api;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

public class TabList {
	
	PacketPlayOutPlayerListHeaderFooter packet;
	
	public TabList(String header, String footer) {
		IChatBaseComponent headerMessage = new ChatMessage(header);
		IChatBaseComponent footerMessage = new ChatMessage(footer);
		packet = new PacketPlayOutPlayerListHeaderFooter(headerMessage);
		Field field;
		try {
			field = (Field) packet.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(packet, footerMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void send(Player p) {
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	
}
