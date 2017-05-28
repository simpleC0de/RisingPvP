package rpvp.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import rpvp.commands.PointsCommand;
import rpvp.handler.AttackListener;
import rpvp.handler.JoinEvent;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by root on 15.05.2017.
 */
public class RisingPvP extends JavaPlugin{


    private static RisingPvP instance;
    private MySQL sql;

    public Scoreboard s;


    public void onEnable(){
        instance = this;
        loadPlugin();
        s = Bukkit.getScoreboardManager().getMainScoreboard();
        //registerHealth();


    }


    public void loadPlugin(){
        loadConfig();
        loadSQL();
        loadEvents();
        loadCommands();
        checkforReset();
        registerHealth();

    }

    protected void checkforReset(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        boolean monday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;

        if(monday && !getConfig().getBoolean("Reset.Done")){

            getSql().reset();

            getConfig().set("Reset.Done", true);

        }

        if(cal.get(Calendar.DAY_OF_WEEK)  == Calendar.TUESDAY){
            getConfig().set("Reset.Done", false);
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
    }

    public void registerHealth(){
        Objective o = s.registerNewObjective("health", "health");

        if(o != null){
            return;
        }

        Team t = s.registerNewTeam("healthTeam");
        t.setPrefix(ChatColor.BLUE + "");


        o.setDisplayName(ChatColor.RED + "❤");
        o.setDisplaySlot(DisplaySlot.BELOW_NAME);
        Objective _o = s.registerNewObjective("points" , "points");
        if(_o != null)
            return;



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
