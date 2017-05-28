package rpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpvp.main.RisingPvP;

/**
 * Created by root on 28.05.2017.
 */
public class PointsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

        if(cs instanceof Player){
            Player p = (Player)cs;

            int points = RisingPvP.getInstance().getSql().getPoints(p.getUniqueId().toString());

            p.sendMessage("§5-------------§2Punkte§5-------------");
            p.sendMessage("§8Du hast: §2" + points + "§8 Punkte");
            p.sendMessage("§5-------------§2Punkte§5-------------");

        }

        return true;
    }
}
