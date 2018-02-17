package com.robbpell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author 314mp
 */

public class PathBuilder {
    public static PathBuilder    plugin;
    
    
public static Block Find(Player player){
    System.out.println("start");
    Location pLoc = player.getLocation();
    Block closest = closestBlock(pLoc,new HashSet<Material>(Arrays.asList(Material.NETHER_BRICK)),500);
    if(closest == null)
    {
        System.out.println("none found");
        return null;
    }
    System.out.println("block found");
    closest = getLedge(closest);
    
    boolean XDecrease = pLoc.getX() > closest.getX();

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
    
    for (int x = -(radius); x <= radius; x ++)
        {
            for (int y = -(radius); y <= radius; y ++)
            {
                for (int z = -(radius); z <= radius; z ++)
                {
                    Block b = world.getBlockAt((int)pX+x, (int)pY+y, (int)pZ+z);
                    if(b.getType() == Material.NETHER_BRICK)
                    {
                        Location pos = b.getLocation();
                        System.out.println("non-top found");
                            while(world.getBlockAt(pos).getType() == Material.NETHER_BRICK){
                                pos.setY(pos.getY()+1);
                            }
                            pos.setY(pos.getY()-1);
                        System.out.println("top found");
                        return world.getBlockAt(pos);
                    
                    }
                }
            }
        }
                    
                    
    return null;
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
    int posOffset = 0;
    for (int i = 1; i <= 5; i++) {
        if(!isType(loc,Material.NETHER_BRICK,i))
            posOffset = i--;
        else if (i == 5)
            posOffset = 5;
    }
    int negOffset = 0;
    for (int i = 1; i <= 5; i++) {
        if(!isType(loc,Material.NETHER_BRICK,i*-1))
            negOffset = i++;
        else if (i == 5)
            negOffset = -5;
    }
    
    if((negOffset *-1) + posOffset < 5)
        return null;
    else if (negOffset > -2){
        loc.setX(loc.getX() + (2 + negOffset));
    }else if (posOffset < 2){
        loc.setX(loc.getX() - (2 - posOffset));
    }
    
    return world.getBlockAt(loc);
}
 
public static boolean isType(Location loc,Material type, int xOffset){
    loc.setX(loc.getX()+ xOffset);
    return loc.getWorld().getBlockAt(loc).getType() == type;
}

   /** 
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String arg[]) {
        Player player = (Player) sender;
        
        if(commandLabel.equalsIgnoreCase("test")) {
            Location loc1 =  player.getPlayer().getLocation();
            Location loc2 =  player.getPlayer().getLocation();
            ArrayList<Location> path;
            World world = loc1.getWorld();
 //           netherpath pathType = new netherpath();
            
            loc1.setX(21);
            loc1.setZ(-31);
            loc1.setY(63);
            loc2.setX(294);
            loc2.setZ(173);
            loc2.setY(73);
            
            String dir = whatdirection(loc1,loc2);
            path = directpath(dir,loc1,loc2,world);

          
            int layer = 0, z = 0;
            
        for (int i = 0; i <= path.size()-1; i++){
            z=0;getLogger().info("1_" + z + "i=" +i );
            
            switch (layer) {
                case 0:
                        while (z < 5){
                            if (z == 0 || z == 4){
                               path.get(i).setX(path.get(i).getX()+z);
                               Block blockToChange = world.getBlockAt(path.get(i));
                               blockToChange.setTypeId(112);
                               z++;getLogger().info("2_" + z);
                               } else
                            {z++;}
                        } 
                        layer++;getLogger().info("layer_" + layer);
                           i--;
                           break;
                            
                case 1: path.get(i).setY((path.get(i).getY())-1);
                    while (z < 4){
                        
                        path.get(i).setX((path.get(i).getX())-1);
                        Block blockToChange = world.getBlockAt(path.get(i));
                        blockToChange.setTypeId(112);
                        z++;getLogger().info("3" + path.get(i));
                    } 
                    layer=0;
                    break;
                    default: break;
                }
            }
            
            //pathType.build(path, world);

//            for (int i = 0; i <= path.size()-1; i++)
//            { 
//                Location tempLoc = path.get(i);
//                getLogger().info("Z=" + tempLoc.getZ() + "X=" + tempLoc.getX());
//                Block blockToChange = world.getBlockAt(tempLoc);
//    		blockToChange.setTypeId(112);    // set the block to Type 1
//                path.set(i, null);
//                
//            }
    	} return false;
    }
        
    public String whatdirection(Location start, Location end){
    double x1 = start.getX();
    double x2 = end.getX();
    double z1 =start.getZ();
    double z2 = end.getZ();
        //need to test Direct coor. ie just north also add up/down;
    String dir;
    if (z1 > z2)
        {dir = "N";}
    else if (z1 < z2)
        {dir = "S";}
    else
        {dir = "";}
    
    if (x1 > x2)
        {dir = dir+"W";}
    else if (x1 < x2)
        {dir = dir+"E";}
    
    return dir;
    }
    

    
    public ArrayList<Location> directpath(String dir, Location start, Location end, World world){
        ArrayList<Location>path = new ArrayList<Location>();
        Location tempLoc = start.clone();
        world = start.getWorld();
        
        getLogger().info("1");
        path.add(tempLoc);
        int i;
        
        if ("SE".equals(dir)){
            for (i=1; end.getY() != tempLoc.getY(); i++)
            {
                tempLoc = new Location(world, path.get(i-1).getX(), 62, 1);
                tempLoc = path.get(i-1).clone();
                tempLoc.setY(start.getY()+i);
                tempLoc.setZ(start.getZ()+i);
                path.add(tempLoc);
            } 
            start = path.get(i-1).clone(); 
 //           getLogger().info("i=" + i + "_" + path.get(i-1));
            //y is recycling.
            for ( i=1  ; end.getX() != tempLoc.getX() && end.getZ() != tempLoc.getZ() ;i++)
                { //create diagonal untill X or Z match.
                    tempLoc = new Location(world, 1, path.get(i-1).getY(), 1);
                    tempLoc.setY(start.getY());
                tempLoc.setX(start.getX()+i);
                tempLoc.setZ(start.getZ()+i);
                path.add(tempLoc);
//
                                }
            start = tempLoc.clone(); 
            while (tempLoc.getX() != end.getX() || tempLoc.getZ() != end.getZ())
              {
                    tempLoc = new Location(world, 1, 62, 1);
                    tempLoc = start.clone();
                    if (tempLoc.getX() == end.getX())
                        { //create straight line by z++
                            tempLoc.setX(path.get(path.size() -1).getX());
                            tempLoc.setZ(path.get(path.size() -1).getZ()+1);
                            path.add(tempLoc);
                            i++;
                    } else if (tempLoc.getZ() == end.getZ())
                        {//create straight line by x++
                            tempLoc.setZ(path.get(path.size() -1).getZ());
                            tempLoc.setX(path.get(path.size() -1).getX()+1);
                            path.add(tempLoc);
                            i++; 
                        }
              }
        }
        //getLogger().info("here3");
    

        return path;
    }
    
    
    
//    //    public ArrayList<Location> directpath(String dir, Location start, Location end, World world){
//        ArrayList<Location>path = new ArrayList<Location>();
//        Location tempLoc = start.clone();
//        world = start.getWorld();
//        
//        getLogger().info("1");
//        path.add(tempLoc);
//        int i;
//        
//        if ("SE".equals(dir)){
//            for ( i=1  ; end.getX() != tempLoc.getX() && end.getZ() != tempLoc.getZ() ;i++)
//                { //create diagonal untill X or Z match.
//                    tempLoc = new Location(world, 1, 62, 1);
//                tempLoc.setX(start.getX()+i);
//                tempLoc.setZ(start.getZ()+i);
//                path.add(tempLoc);
//
//                                }
//            getLogger().info("overthere");
//            while (path.get(i-1).getX() != end.getX() || path.get(i-1).getZ() != end.getZ())
//            {tempLoc = new Location(world, 1, 62, 1);
//            if (path.get(i-1).getX() == end.getX())
//                {
//                    tempLoc.setX(path.get(i-1).getX());
//                    tempLoc.setZ(path.get(i-1).getZ()+1);
//                    path.add(tempLoc);
//                    i++;
//            } else if (path.get(i-1).getZ() == end.getZ())
//                {
//                    tempLoc.setZ(path.get(i-1).getZ());
//                    tempLoc.setX(path.get(i-1).getX()+1);
//                    path.add(tempLoc);
//                    i++;
//                }getLogger().info("here1" + path.get(i-1)+"_"+i);
//            }getLogger().info("here2");
//        }getLogger().info("here3");
//    
//
//        return path;
//    } */       
}

