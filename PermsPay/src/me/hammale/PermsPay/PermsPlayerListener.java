package me.hammale.PermsPay;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class PermsPlayerListener extends PlayerListener {

	public final PermsPay plugin;
	
    public PermsPlayerListener(PermsPay plugin)
    {
      this.plugin = plugin;
    }
	
    public void onPlayerJoin(PlayerJoinEvent e){
    	Player p = e.getPlayer();
    	if(plugin.hasCash(p)){
    		if(plugin.readCash(p) != null){
	    		Double amnt = plugin.readCash(p);
	    		plugin.handlePayment(p, amnt);
	    		plugin.removeFile(p);
    		}
    	}
    }
	
}
