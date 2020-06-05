package de.alexanderritter.varo.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import de.alexanderritter.varo.main.Varo;

public class Worldborder {
	
	Varo plugin;
	int instant;
	public Worldborder(Varo plugin) {
		this.plugin = plugin;
	}
	
	public boolean isOutsideOfBorder(Player p, WorldBorder border) {
		
        Location loc = p.getLocation();
        double size = border.getSize()/2;
        Location center = border.getCenter();
        
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }
	
	public Location getCorrectedLocation(Location outside, WorldBorder border) {
		
		World world = outside.getWorld();
		double x = outside.getX();
		double y = outside.getY();
		double z = outside.getZ();
		
		double knockback = 5.0;
		
		double minX = border.getCenter().getX() - (border.getSize()/2);
		double maxX = border.getCenter().getX() + (border.getSize()/2);
		
		double minZ = border.getCenter().getZ() - (border.getSize()/2);
		double maxZ = border.getCenter().getZ() + (border.getSize()/2);
		
		if(x <= minX) {
			x = minX + knockback;
		} else if (x >= maxX)
			x = maxX - knockback;
		
		if(z <= minZ) {
			z = minZ + knockback;		
		} else if (z >= maxZ) {
			z = maxZ - knockback;
		}
		
		outside.setX(x);
		outside.setZ(z);
		
		while((y = getHighestBlock(world, x, z)) == 0.0) {
			if(x > world.getSpawnLocation().getX()) {
				x--;
				outside.setX(x);
			} else if(x < world.getSpawnLocation().getX()) {
				x++;
				outside.setX(x);
			}
			if(z > world.getSpawnLocation().getZ()) {
				z--;
				outside.setZ(z);
			} else if(z < world.getSpawnLocation().getZ()) {
				z++;
				outside.setZ(z);
			}
		}
		outside.setY(y);
		
		return outside.getBlock().getLocation().add(0.5, 0.0, 0.5);
	}
	
	private double getHighestBlock(World world, double x, double z) {
		int maxHeight = 255;
		if(world.getEnvironment().equals(Environment.NETHER)) maxHeight = 125;
		while(maxHeight > 0){
			Location possible = new Location(world, x, maxHeight, z);
			if(possible.getBlock().getType() != Material.AIR &&
					possible.getBlock().getType() != Material.LAVA &&
					possible.getBlock().getType() != Material.STATIONARY_LAVA &&
					possible.add(0.0, 1.0, 0.0).getBlock().getType() == Material.AIR &&
					possible.add(0.0, 2.0, 0.0).getBlock().getType() == Material.AIR) {
				return maxHeight + 1;
				}
			maxHeight--;
		}
		return 0.0;	   
	}
	
}