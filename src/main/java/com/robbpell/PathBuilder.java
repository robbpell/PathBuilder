package com.robbpell;

import static com.robbpell.Direction.EAST;
import static com.robbpell.Direction.NORTH;
import static com.robbpell.Direction.SOUTH;
import static com.robbpell.Direction.WEST;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.Colorable;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author 314mp
 */

public class PathBuilder {
    public static PathBuilder    plugin;
    public static Player myPlayer;
    
public static Block Find(Player player){
    System.out.println("start");
    myPlayer = player;
    Location currentLocation = player.getLocation();
    
    //Get end/goal block
    Block closest = closestBlock(currentLocation,new HashSet<Material>(Arrays.asList(Material.NETHER_BRICK)),500);
    if(closest == null)
    {
        System.out.println("none found");
        return null;
    }
    System.out.println("block found " + closest.getX() + "," + closest.getZ());
    closest = getLedge(closest);
    System.out.println("Ledge found");

    double xDif = Math.abs(currentLocation.getX() - closest.getLocation().getX());
    double zDif = Math.abs(currentLocation.getZ() - closest.getLocation().getZ());
    Direction dir;
    if(xDif > zDif){
        if(currentLocation.getX() > closest.getX()){
            dir = Direction.WEST;
        }else{
            dir = Direction.EAST;
        }
    } else{
        if(currentLocation.getZ() > closest.getZ()){
            dir = Direction.NORTH;
        }else{
            dir = Direction.SOUTH;
        }
    }
    
    //build path
    System.out.println("Building Steps");
    currentLocation = buildSteps(currentLocation,closest.getLocation(),dir);
    System.out.println("Steps Built");
    System.out.println(currentLocation.getX() + "," + currentLocation.getY() + "," + currentLocation.getZ());
    Block b = setDiagonalPath(currentLocation,closest.getLocation());
    System.out.println("Diagonal Built");
    
    setStraight(b, closest);
        
    Location loc = closest.getLocation();
    System.out.println(loc.getX() + "," + loc.getY() + "," + loc.getZ());
    return closest;
}
    /**
 * Finds block by type in radius. select top most if stacked.
 * @param origin The location around which to search.
 *               This location will NOT be included in the search, but all other locations in the column will.
 * @param types  A Set (preferably a HashSet) that contains the type IDs of blocks to search for
 * @parm radius radius to search
 * @return The closest block, or null if one was not found in the column.
 *         In the case of a tie, the higher block wins.
 */
public static Block closestBlock(Location origin, Set<Material> types, int radius)
{
    int pX = origin.getBlockX();
    int pY = origin.getBlockY();
    int pZ = origin.getBlockZ();
    World world = origin.getWorld();
    
    Block closest = null;
    int currentSize = 1;
    int count = 0;
    while(currentSize <= radius){

        for (int x = (pX - currentSize); x <= (pX + currentSize); x ++)
            {
                  if(Math.abs(x) == pX + currentSize)
                    {
                        for (int z = (pZ - currentSize); z <= (pZ + currentSize); z ++)
                        {
                            if(Math.abs(x) == (pX + currentSize) 
                                    || Math.abs(z) == (pZ + currentSize))
                            {
                                count++;

                                Block b = world.getBlockAt(x, 64, z);
                                if(b.getType() == Material.NETHER_BRICK)
                                {           
                                    Location pos = b.getLocation();
                                    while(world.getBlockAt(pos).getType() == Material.NETHER_BRICK){
                                            pos.setY(pos.getY()+1);
                                        }
                                        pos.setY(pos.getY()-1);
                                        return world.getBlockAt(pos);
                                }
                            }
                        }
                    }
            }
           currentSize++;
    }           
                    
    return closest;
}

public static double getDistance(Block start, Block end){
    return Math.hypot(start.getX()-end.getX(), start.getZ()-end.getZ());
}


/*
 * Checks for positoin with 2 blocks on each side. 
 * @parm block starting block.
 * @return The center ledge of the block (top most with 2 on each side)
 */
public static Block getLedge(Block block){
    World world = block.getWorld();
    Location loc = block.getLocation();
    
    if(isType(loc,Material.NETHER_BRICK,-2) &&
       isType(loc,Material.NETHER_BRICK,-1) &&
       isType(loc,Material.NETHER_BRICK,1) &&
       isType(loc,Material.NETHER_BRICK,2)){
        return block;
    }
    
    System.out.println("finding offset for " + loc.getBlockX() + "," + loc.getBlockY() + "," +loc.getBlockZ());
    int posOffset = 0;
    for (int i = 1; i <= 5; i++) {
        if(!isType(loc,Material.NETHER_BRICK,i))
        {
            posOffset = i--;
            break;
        }
        else if (i == 5)
            posOffset = 5;
    }
    int negOffset = 0;
    for (int i = 1; i <= 5; i++) {
        if(!isType(loc,Material.NETHER_BRICK,(-i)))
        {
            negOffset = (i*-1)+ 1;
            break;
        }
        else if (i == 5)
            negOffset = -5;
    }
    
    System.out.println("Ajusting offset" + negOffset + " " + posOffset);
    if((-negOffset + posOffset) < 5)
        return null;
    else if (negOffset > -2){
        loc.setX(loc.getX() + (5 + negOffset));
    }else if (posOffset < 2){
        loc.setX(loc.getX() - (5 - posOffset));
    }else
        System.err.println("no ledge found using current block");
    
    return world.getBlockAt(loc);
}
 
/*
 * check if block is type
 * @parm loc location to check
 * @parm type check if is this type
 * @parm xOffset int to add to the x value of the location before checking
 */
public static boolean isType(Location location,Material type, int xOffset){
    Location loc = location.clone();
    loc.setX(loc.getX()+ xOffset);
    return loc.getWorld().getBlockAt(loc).getType() == type;
}

public static Block setBlock(Location loc, Material type){
    return setBlock(loc,type,false);
}

public static Block setBlock(Location loc, Material type, boolean setAir){
    Block block = loc.getBlock();
    if(block.getType() == Material.BEDROCK || block.getType() == Material.NETHER_BRICK)
        return null;
    block.setType(type);
    if(setAir)
        setAir(block.getLocation());
    return block;
    //#TODO add side blocks for path
}

/*
 * buildSteps from startloc to endloc then moves one block horizontally
 */
public static Location buildSteps(Location startLoc,Location endLoc, Direction direction){
    boolean YDecrease = startLoc.getY() > endLoc.getY();
    int offsetZ = 0,offsetX = 0, sideStepX = 0, sideStepZ = 0;
    BlockFace face = BlockFace.NORTH;
    
    switch (direction) {
            case NORTH:
            {
                offsetZ = -1;
                offsetX = 0;
                sideStepX = -1;
                if(YDecrease)
                    face = BlockFace.SOUTH;
                else
                    face = BlockFace.NORTH;
            }
            break;
             case SOUTH:
             {
                offsetZ = 1;
                offsetX = 0;
                sideStepX = 1;
                if(YDecrease)
                    face = BlockFace.SOUTH;
                else
                    face = BlockFace.NORTH;
             }
            break;
            case EAST:
            {
                offsetZ = 0;
                offsetX = 1;
                sideStepZ = -1;
                if(YDecrease)
                    face = BlockFace.WEST;
                else
                    face = BlockFace.EAST;
            }
            break;
            case WEST:
            {
                offsetZ = 0;
                offsetX = -1;
                sideStepZ = 1;
                if(YDecrease)
                    face = BlockFace.EAST;
                else
                    face = BlockFace.WEST;
            }
            break;
    }
    Location currentLoc = startLoc;
    int offsetY = 1;
    if(YDecrease) offsetY = -1;
    else currentLoc.setY(currentLoc.getY()-1);
    
    
    
    while((YDecrease && currentLoc.getBlockY()> endLoc.getY() )
            || (!YDecrease && currentLoc.getBlockY()< endLoc.getY())){

        currentLoc.setY(currentLoc.getY()+offsetY);
        currentLoc.setZ(currentLoc.getZ()+offsetZ);
        currentLoc.setX(currentLoc.getX()+offsetX);
        System.out.println("Adding Step" + currentLoc.getX() +" "+ currentLoc.getY()+" "+ currentLoc.getZ());
        Block block = setBlock(currentLoc,Material.NETHER_BRICK_STAIRS,true);
        if(block == null)break;
        
        BlockState state = currentLoc.getBlock().getState();
        Stairs stairs = (Stairs) state.getData();
        stairs.setFacingDirection(face);
       
        state.setData(stairs);
        state.update(false, false);
        
        Location tempLoc = currentLoc.clone();
        tempLoc.setX(tempLoc.getBlockX()+sideStepX);
        tempLoc.setZ(tempLoc.getBlockZ()+sideStepZ);
        block = setBlock(tempLoc,Material.NETHER_BRICK_STAIRS,true);
        if(block == null)break;
        
        state = tempLoc.getBlock().getState();
        stairs = (Stairs) state.getData();
        stairs.setFacingDirection(face);
       
        state.setData(stairs);
        state.update(false, false);
    }
        currentLoc.setZ(currentLoc.getZ()+offsetZ);
        currentLoc.setX(currentLoc.getX()+offsetX);
    return currentLoc;
}

/*
 * Set two air blocks above path, unless passing through portal.
 */
public static void setAir(Location loc){
    Location location = loc.clone();
    location.setY(location.getY()+1);
    if(location.getBlock().getType()!= Material.OBSIDIAN
            && location.getBlock().getType()!= Material.PORTAL){
        setBlock(location,Material.AIR);
    }
    location.setY(location.getY()+1);
    if(location.getBlock().getType()!= Material.OBSIDIAN
            && location.getBlock().getType()!= Material.PORTAL){
        setBlock(location,Material.AIR);
    }
        location.setY(location.getY()+1);
    if(location.getBlock().getType()!= Material.OBSIDIAN
            && location.getBlock().getType()!= Material.PORTAL){
        setBlock(location,Material.AIR);
    }
}


public static Block setDiagonalPath(Location startLoc, Location endLoc){
    double x = endLoc.getBlockX();
    double z = endLoc.getBlockZ();
    int offsetX = 0, offsetZ = 0;
    if(startLoc.getX()> x) offsetX = -1;
    else offsetX = 1;
    if(startLoc.getZ()> z) offsetZ = -1;
    else offsetZ = 1;
    

    while(startLoc.getBlockX() != x 
            && startLoc.getBlockZ() != z){
        System.out.println(startLoc.getX() +" "+ startLoc.getY()+" "+ startLoc.getZ());
        startLoc.setZ(startLoc.getZ()+offsetZ);
        Block b = setBlock(startLoc,Material.STAINED_GLASS,true);
        b.setData((byte)15);
        Location tempLoc = startLoc.clone();
        tempLoc.setZ(tempLoc.getZ()+offsetZ);
        b = setBlock(tempLoc,Material.STAINED_GLASS,true);
        b.setData((byte)15);
        tempLoc.setZ(tempLoc.getZ()+offsetZ);
        tempLoc.setY(tempLoc.getY()+1);
        b = setBlock(tempLoc,Material.STEP,true);
        b.setData((byte)6);
        
        tempLoc = startLoc.clone();
        tempLoc.setZ(tempLoc.getZ()-offsetZ);
        b = setBlock(tempLoc,Material.STAINED_GLASS,true);
        b.setData((byte)15);
        tempLoc.setZ(tempLoc.getZ()-offsetZ);
        b = setBlock(tempLoc,Material.STAINED_GLASS,true);
        b.setData((byte)15);
        tempLoc.setZ(tempLoc.getZ()-offsetZ);
        tempLoc.setY(tempLoc.getY()+1);
        b = setBlock(tempLoc,Material.STEP,true);
        b.setData((byte)6);
        
        startLoc.setX(startLoc.getX()+offsetX);
        setAir(startLoc);       

    }
    return startLoc.getBlock();
}

public static void setStraight(Block block, Block endBlock){
    Location location = block.getLocation();
    Location endLocation = endBlock.getLocation();
    int endX = endLocation.getBlockX();
    int endZ = endLocation.getBlockZ();
    int xOffset = 0, zOffset = 0;
    if(location.getBlockX() > endX) xOffset = -1;
    else if(location.getBlockX() < endX) xOffset = 1;
    else if(location.getBlockZ() < endZ) zOffset = 1;
    else if(location.getBlockZ() < endZ) zOffset = 1;
    
    System.out.println(endX + "," + endZ+ "," +xOffset+ "," +zOffset+ "," + location.getBlockX() + "," + location.getBlockZ());
    
    while(location.getBlockX() != endX 
            || location.getBlockZ() != endZ){
        Block b = setBlock(location,Material.STAINED_GLASS,true);
        if(b != null) b.setData((byte)15);
        
        location.setZ(location.getZ()+zOffset);
        location.setX(location.getX()+xOffset);
        System.out.println(location.getBlockX() + "," + location.getBlockZ());
    }
}
}