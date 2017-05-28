package rpvp.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import rpvp.main.RisingPvP;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by root on 15.05.2017.
 */
public class AttackListener implements Listener {

    private HashMap<Player, HashMap<Player, Long>> hits = new HashMap<>();
    private HashMap<Player, Player> hitted = new HashMap<>();


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){

        if(e.getDamager() instanceof Wolf && ((Wolf) e.getDamager()).getOwner() instanceof Player && ((Wolf) e.getDamager()).getOwner() != null || e.getDamager() instanceof Player){

            if(e.getEntity() instanceof Player){

                if(e.getDamager() instanceof Wolf){
                    String damagerUuid = ((Wolf) e.getDamager()).getOwner().getUniqueId().toString();

                    Player damager = Bukkit.getPlayer(UUID.fromString(damagerUuid));


                    HashMap<Player, Long> temp = new HashMap<>();
                    temp.put(damager, System.currentTimeMillis() + 10000);

                    hitted.put((Player)e.getEntity(), damager);
                    hits.put((Player)e.getEntity(), temp);
                    temp = null;

                }else{
                    HashMap<Player, Long> temp = new HashMap<>();
                    temp.put((Player)e.getDamager(), System.currentTimeMillis() + 10000);
                    hitted.put((Player)e.getEntity(), (Player)e.getDamager());
                    hits.put((Player)e.getEntity(), temp);
                    temp = null;
                }

            }

        }

    }



    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player dead = e.getEntity();

        if(hits.containsKey(dead)){

            if(hits.get(e.getEntity()).get(hitted.get(e.getEntity())) <= System.currentTimeMillis()){

                hitted.remove(hits.get(e.getEntity()).get(e.getEntity()));
                hits.remove(e.getEntity());
                return;
            }

            Player killer = hitted.get(e.getEntity());

            int deadpoints = RisingPvP.getInstance().getSql().getPoints(dead.getUniqueId().toString());

            double dp;
            dp = (double)deadpoints;

            double percentage = 1;

            boolean set = false;

            if(dp >= 500 && !set){
                percentage = 12.5;
                set = true;
            }

            if(dp >= 400 && !set){
                percentage = 12;
                set = true;
            }

            if(dp >= 300 && !set){
                percentage = 11.5;
                set = true;
            }

            if(dp >= 200 && !set){
                percentage = 9.5;
                set = true;
            }

            if (dp >= 100 && !set){
                percentage = 7;
                set = true;
            }

            if (dp < 100 && !set){
                percentage = 5;
            }




            double ergebnis = 0;

            ergebnis = (dp / 100) * percentage;

            int cErgebnis = (int)Math.round(ergebnis);

            RisingPvP.getInstance().getSql().addPoints(killer.getUniqueId().toString(), cErgebnis);
            RisingPvP.getInstance().getSql().removePoints(dead.getUniqueId().toString(), cErgebnis);



        }else{
            return;
        }
    }

}
