package rpvp.handler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;
import rpvp.main.RisingPvP;

/**
 * Created by root on 15.05.2017.
 */
public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        try{Team t = RisingPvP.getInstance().s.getTeam("blue");t.addPlayer(e.getPlayer());}catch(Exception ex){}



        if(RisingPvP.getInstance().getSql().playerExists(e.getPlayer().getUniqueId().toString())){
            return;
        }

        RisingPvP.getInstance().getSql().addPlayer(e.getPlayer().getUniqueId().toString());


    }

}
