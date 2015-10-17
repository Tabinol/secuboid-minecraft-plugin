/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2015 Tabinol
 Forked from Factoid (Copyright (C) 2014 Kaz00, Tabinol)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.secuboid.scoreboard;

import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import me.tabinol.secuboid.Secuboid;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;


/**
 * The Class ScoreBoard.
 */
public class ScoreBoard extends Thread{
    
    /** The manager. */
    private ScoreboardManager manager;
    
    /** The Scoreboard list. */
    private Map<Player,Scoreboard> ScoreboardList = new HashMap<Player,Scoreboard>();
    
    /**
     * Instantiates a new score board.
     */
    public ScoreBoard(){
        super();
        this.manager = Secuboid.getThisPlugin().getServer().getScoreboardManager();
    }
    
    /**
     * Send scoreboard.
     *
     * @param playerlist the playerlist
     * @param player the player
     * @param LandName the land name
     */
    @SuppressWarnings("deprecation")
	public void sendScoreboard(HashSet<Player> playerlist, Player player, String LandName){
        resetScoreboard(player);
        Scoreboard scoreboard = manager.getNewScoreboard();
        ScoreboardList.put(player,scoreboard);
        scoreboard.registerNewObjective("land", "dummy");
        scoreboard.getObjective("land").setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.getObjective("land").setDisplayName(Secuboid.getThisPlugin().iLanguage().getMessage("SCOREBOARD.LANDINFO"));
        for(Player p : playerlist){
            scoreboard.getObjective("land").getScore(p).setScore(0);
        }
        scoreboard.getObjective("land").getScore(player).setScore(0);// Note: A voir si preferable de se voir soi meme ou non dans le land.
        player.setScoreboard(scoreboard);
    }
    
    /**
     * Gets the scoreboard.
     *
     * @param player the player
     * @return the scoreboard
     */
    public Scoreboard getScoreboard(Player player){
            return ScoreboardList.get(player);
    }
    
    /**
     * Gets the scoreboard manager.
     *
     * @return the scoreboard manager
     */
    public ScoreboardManager getScoreboardManager(){
        return manager;
    }

    
    /**
     * Reset scoreboard.
     *
     * @param player the player
     */
    @SuppressWarnings("deprecation")
	public void resetScoreboard(Player player){
        if(ScoreboardList.containsKey(player)){
            ScoreboardList.get(player).getObjective("land").unregister();
            ScoreboardList.get(player).resetScores(player);
            ScoreboardList.remove(player);
        }
    }
}
