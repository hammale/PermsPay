package me.hammale.PermsPay;
 
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import ru.tehkode.permissions.events.PermissionEntityEvent;
import ru.tehkode.permissions.events.PermissionEntityEvent.Action;
 

public class GroupListener extends CustomEventListener implements Listener {

	public final PermsPay plugin;
	
    public GroupListener(PermsPay plugin)
    {
      this.plugin = plugin;
    }
	
	public void onPermissionEntityEvent(PermissionEntityEvent event) {
	}

	@Override
	public void onCustomEvent(Event event) {
		if(event instanceof PermissionEntityEvent){
			onPermissionEntityEvent((PermissionEntityEvent) event);
			if(((PermissionEntityEvent) event).getAction() == Action.RANK_CHANGED){
				Player p = plugin.getServer().getPlayer(((PermissionEntityEvent) event).getEntity().getName());				
				if(p == null){
					OfflinePlayer op = plugin.getServer().getOfflinePlayer(((PermissionEntityEvent) event).getEntity().getName());
					ArrayList<String> group = getGroupOffline(op);
					 if(group.size() == 1){
						 for(String s:group){
							 	 double amount = plugin.getAmount(s);
								 plugin.addPlayer(op, amount);
						 }
					 }else{
						 System.out.println("[PermsPay] Multiple groups are not yet supported!");
					 }
				}else{
				ArrayList<String> group = getGroup(p);
				 if(group.size() == 1){
					 for(String s:group){
						 double amount = plugin.getAmount(s);
							 payPlayer(amount, p);
					 }
				 }else{
					 System.out.println("[PermsPay] Multiple groups are not yet supported!");
				 }
			   } 
			}
		}
	}
	
	 private ArrayList<String> getGroupOffline(OfflinePlayer p) {
		 String player = p.getName();			 
		  if(plugin.getServer().getPluginManager().isPluginEnabled("PermissionsEx")){
		    	PermissionManager pex = PermissionsEx.getPermissionManager();
		  
		  ArrayList<String> daGroups = new ArrayList<String>();
		 
	        PermissionGroup[] groups;
	        groups = pex.getUser(player).getGroups();
	            for (PermissionGroup group : groups) {
	                daGroups.add(group.getName());
	            }
	            return daGroups;
	    }
		  return null;
	 }

	private void payPlayer(double amnt, Player p) {
		 plugin.handlePayment(p, amnt);
	}

	public ArrayList<String> getGroup(Player player) {
		  if(plugin.getServer().getPluginManager().isPluginEnabled("PermissionsEx")){
		    	PermissionManager pex = PermissionsEx.getPermissionManager();
		  
		  ArrayList<String> daGroups = new ArrayList<String>();
		 
	        PermissionGroup[] groups;
	        groups = pex.getUser(player).getGroups(player.getWorld().getName());
	            for (PermissionGroup group : groups) {
	                daGroups.add(group.getName());
	            }
	            return daGroups;
	    }
		  return null;
	 }	
}