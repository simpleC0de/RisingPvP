package rpvp.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import rpvp.commands.PointsCommand;
import rpvp.handler.AttackListener;
import rpvp.handler.JoinEvent;
import rpvp.handler.RespawnEvent;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by root on 15.05.2017.
 */
public class RisingPvP extends JavaPlugin{


    private static RisingPvP instance;
    private MySQL sql;

    public HashMap<Player, Silverfish> playerTags = new HashMap<>();

    public Scoreboard s;


    public void onEnable(){
        instance = this;
        loadPlugin();
        s = Bukkit.getScoreboardManager().getNewScoreboard();
        //registerHealth();


    }


    public void loadPlugin(){
        loadConfig();
        loadSQL();
        loadEvents();
        loadCommands();
        checkforReset();
       // registerHealth();

    }

    protected void checkforReset(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        boolean monday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;

        if(monday && !getConfig().getBoolean("Reset.Done")){

            getSql().reset();

            getConfig().set("Reset.Done", true);
            saveConfig();
        }

        if(cal.get(Calendar.DAY_OF_WEEK)  != Calendar.MONDAY){
            getConfig().set("Reset.Done", false);
            saveConfig();
        }

    }


    public void loadConfig(){
        if(getDataFolder().exists()){
            reloadConfig();
            return;
        }

        getConfig().set("MySQL.Hostname", "localhost");
        getConfig().set("MySQL.Port", 3306);
        getConfig().set("MySQL.Username", "root");
        getConfig().set("MySQL.Password", "password");
        getConfig().set("MySQL.Database", "database");

        getConfig().set("Reset.Done", false);

        saveConfig();

    }

    public void loadEvents(){
        Bukkit.getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new AttackListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new RespawnEvent(), this);
    }


    public void registerHealth(Player p){

        try{
            Scoreboard board = p.getScoreboard();



            Objective o = board.registerNewObjective("obj-"+p.getPlayerListName(), "health");

            Team t = s.registerNewTeam(p.getDisplayName());
            t.setPrefix(ChatColor.BLUE + "");


            o.setDisplayName(ChatColor.RED + "❤ | " + ChatColor.DARK_AQUA + "Punkte " + sql.getPoints(p.getUniqueId().toString()) );
            o.setDisplaySlot(DisplaySlot.BELOW_NAME);

            p.setScoreboard(board);
        }catch(IllegalArgumentException e){
            p.getScoreboard().getObjective("obj-"+p.getPlayerListName()).unregister();
            registerHealth(p);
            return;
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    protected void updatePlayer(Player updateAble){

        new BukkitRunnable(){

            @Override
            public void run(){
                try{

                    Scoreboard board = updateAble.getScoreboard();
                    Objective obj = board.getObjective("obj-" + updateAble.getPlayerListName());
                    obj.setDisplayName(ChatColor.RED + "❤ | " + ChatColor.DARK_AQUA + "Punkte " +  sql.getPoints(updateAble.getUniqueId().toString()));

                    updateAble.setScoreboard(board);

                }catch(Exception ex){
                    ex.printStackTrace();
                }

            }

        }.runTaskTimer(this, 0, 20*30);


    }




    public void loadCommands(){
        getCommand("punkte").setExecutor(new PointsCommand());
    }

    public void loadSQL(){
        try {
            sql = new MySQL();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static RisingPvP getInstance(){
        return instance;
    }

    public MySQL getSql(){
        return sql;
    }

    public void onDisable(){

    }


}
