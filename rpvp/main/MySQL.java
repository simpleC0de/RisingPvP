package rpvp.main;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 15.05.2017.
 */
public class MySQL {

    private Connection conn;
    private String hostname;
    private String user;
    private String password;
    private String database;
    private int port;
    public MySQL() throws Exception
    {

        hostname = RisingPvP.getInstance().getConfig().getString("MySQL.Hostname");
        port = RisingPvP.getInstance().getConfig().getInt("MySQL.Port");
        database =  RisingPvP.getInstance().getConfig().getString("MySQL.Database");
        user = RisingPvP.getInstance().getConfig().getString("MySQL.Username");
        password = RisingPvP.getInstance().getConfig().getString("MySQL.Password");
        openConnection();

    }
    public Connection openConnection()
    {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://"+hostname + ":" + port + "/" + database + "?user=" + user + "&password=" + password + "&useUnicode=true&characterEncoding=UTF-8");

            queryUpdate("CREATE TABLE IF NOT EXISTS rpvp_points (UUID varchar(48), POINTS int, RESET varchar(32))");

            return conn;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Bukkit.shutdown();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Bukkit.shutdown();
        }
        return conn;
    }



    public Connection getConnection()
    {
        return this.conn;
    }

    public void closeRessources(ResultSet rs, PreparedStatement st)
    {
        if(rs != null)
        {
            try {
                rs.close();
            } catch (SQLException e) {

            }
        }
        if(st != null)
        {
            try {
                st.close();
            } catch (SQLException e) {

            }
        }
    }


    public void closeConnection()
    {
        try {
            this.conn.close();
        } catch (SQLException e) {

            e.printStackTrace();
        }finally
        {
            this.conn = null;
        }

    }
    public void queryUpdate(final String query)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(!getConnection().isValid(2000))
                        openConnection();


                    PreparedStatement st = conn.prepareStatement(query);

                    st.execute();

                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public boolean playerExists(String uuid){
        try{

            if(!getConnection().isValid(2000)){
                openConnection();
            }

            Connection conn = getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM rpvp_points WHERE UUID = '" + uuid + "';");

            ResultSet rs = st.executeQuery();
            return rs.next();
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public void addPlayer(String uuid){
        try{

            queryUpdate("INSERT INTO rpvp_points(`UUID`, `POINTS`, `RESET`) VALUES ('" + uuid + "', 100, '" + System.currentTimeMillis() + "');");

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void addPoints(String uuid, int points){

        try{

            if(!getConnection().isValid(2000)){
                openConnection();
            }
            queryUpdate("UPDATE rpvp_points SET POINTS = POINTS + " + points + " WHERE UUID = '" + uuid + "';");
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    public void removePoints(String uuid, int points){
        try{

            if(!getConnection().isValid(2000)){
                openConnection();
            }

            queryUpdate("UPDATE rpvp_points SET POINTS = POINTS - " + points + " WHERE UUID = '" + uuid + "';");


        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public Integer getPoints(String uuid){
        try{

            if(!getConnection().isValid(2000)){
                openConnection();
            }

            PreparedStatement st = conn.prepareStatement("SELECT POINTS FROM rpvp_points WHERE UUID = '" + uuid + "'");

            ResultSet rs = st.executeQuery();
            while(rs.next()){
                return rs.getInt(1);
            }

            return 0;


        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void reset(){
        try{

            if(!getConnection().isValid(2000)){
                openConnection();
            }

            HashMap<String, Integer> uuPoints = new HashMap<>();
            ArrayList<String> allPlayers = new ArrayList<>();

            PreparedStatement st = conn.prepareStatement("SELECT * FROM rpvp_points WHERE RESET != '" + System.currentTimeMillis() + "';");
            ResultSet rs = st.executeQuery();


            while(rs.next()){
               /* System.out.println(rs);
                System.out.println("2222: " + rs.getString(1)); // UUID
                System.out.println("3123: " + rs.getString(2)); // Points
                System.out.println("3123: " + rs.getString(3)); // Reset
                */
                uuPoints.put(rs.getString(1), rs.getInt(2));
                allPlayers.add(rs.getString(1));
            }




            double uTaler = 2, uMünzen = 0.5;

            double taler = 0, münzen = 0;

            for(int i = 0; i < allPlayers.size(); i++){

                String currentId = allPlayers.get(i);

                double pdataCoin = (double)uuPoints.get(currentId);

                double ergebnis = pdataCoin * uTaler;


                taler = ergebnis;

                ergebnis = pdataCoin * uMünzen;

                münzen = ergebnis;

                int roundedTaler = 0, roundedMünzen = 0;

                roundedTaler = (int)Math.round(taler);
                roundedMünzen = (int)Math.round(münzen);

                System.out.println("Taler: " + roundedTaler + " Münzen: " + roundedMünzen);

            }



            //Münzen und Taler dem Spieler anrechnen




            queryUpdate("UPDATE rpvp_points SET POINTS = 100 WHERE POINTS != 100");
            queryUpdate("UPDATE rpvp_points SET RESET = '" + System.currentTimeMillis() + "' WHERE RESET != '" + System.currentTimeMillis() + "';");


        }catch(Exception ex){

        }
    }







}
