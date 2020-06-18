package de.alexanderritter.varo.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UUIDs {
	
	public static JsonObject getJsonObject(String name) throws IOException {
		URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
		InputStream is = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		StringBuilder sb = new StringBuilder();
		String line = null;
		
		while((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String jsonString = sb.toString();
		JsonElement json = new JsonParser().parse(jsonString);
		reader.close();
		if(!json.isJsonObject()) return null;
		return json.getAsJsonObject();
	}
	
	public static UUID getUUID(String name) throws IOException {
		JsonObject json = getJsonObject(name);
		if(json == null) return null;
		String rawUUID = json.get("id").toString().replaceAll("\"", "").substring(0);
		rawUUID = rawUUID.substring(0, 8) + "-" + rawUUID.substring(8, 12) + "-" + rawUUID.substring(12, 16) + "-" + rawUUID.substring(16, 20) + "-" + rawUUID.substring(20, 32);
		UUID uuid = UUID.fromString(rawUUID);
		return uuid;
	}
	
	public static String getName(String name) throws IOException {
		JsonObject json = getJsonObject(name);
		String playername = json.get("name").toString().replace("\"", "");
		return playername;
	}
	
	public static String convertFromTrimmed(String trimmedUUID) {
		StringBuilder builder = new StringBuilder(trimmedUUID);
		builder.insert(20, "-");
		builder.insert(16, "-");
		builder.insert(12, "-");
		builder.insert(8, "-");
		return builder.toString();
		
	}

}
