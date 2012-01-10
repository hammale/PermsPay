package me.hammale.PermsPay;

import java.util.logging.Logger;
import java.util.*;
import java.io.*;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PermsPay extends JavaPlugin {
	
		  public FileConfiguration config;
		
		  Random gen = new Random();
		 
		  private final PermsPlayerListener plistener = new PermsPlayerListener(this);
		  private final GroupListener glistener = new GroupListener(this);		  
		  
		  public static Economy econ = null;
		  
		  Logger log = Logger.getLogger("Minecraft");
		
		@Override
		public void onEnable() {
		      if (!setupEconomy() ) {
		            log.info(String.format("[PermsPay] Disabled due to Vault dependency not found!", getDescription().getName()));
		            getServer().getPluginManager().disablePlugin(this);
		            return;
		        }
			PluginDescriptionFile pdfFile = this.getDescription();
			
			log.info("[PermsPay] " + pdfFile.getVersion() + " Enabled!");
			
			PluginManager pm = getServer().getPluginManager();
			pm.registerEvent(Event.Type.PLAYER_JOIN, plistener, Priority.Normal, this);
			pm.registerEvent(Event.Type.CUSTOM_EVENT, glistener, Priority.Normal, this);			
			loadConfiguration();
		}
		
		@Override
		public void onDisable() {
			
			PluginDescriptionFile pdfFile = this.getDescription();
			
			log.info("[PermsPay] " + pdfFile.getVersion() + " Disabled!");
			
		}

		public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
			if(cmd.getName().equalsIgnoreCase("permspay")){
				reloadConfig();
				if(sender instanceof Player) {
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "PermsPay Reloaded!");
				}
				else{
					log.info("[PermsPay] Reloaded!");
				}
				return true;
			}
			return false; 
		}
		
		public void loadConfiguration(){
		    config = getConfig();
		    config.options().copyDefaults(true);   
		    String path1 = "GROUPNAME.Pays";	    
		    config.addDefault(path1, 100);
		    config.options().copyDefaults(true);
		    saveConfig();
		}
		
		public double getAmount(String s){
		    config = getConfig();
		    double amnt = config.getDouble(s + ".Pays"); 
		    return amnt;
		}		
		
	    private Boolean setupEconomy()
	    {
	        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) {
	            econ = economyProvider.getProvider();
	        }
	        return (econ != null);
	    }
	    
	    public void handlePayment(Player p, double i){
			 if(i > 0){		 
				 EconomyResponse r = econ.depositPlayer(p.getName(), i);
			        if(r.transactionSuccess()) {
						 p.sendMessage(ChatColor.GREEN + "You have been awarded $" + i);
			        } else {
						 p.sendMessage(ChatColor.RED + "PAYMENT FAILED!");
			        }
				 
			 }else if(i < 0){
				 i = Math.abs(i);
				 double bal = econ.getBalance(p.getName());
				 if(bal< i){
					 i = bal;
				 }
				 EconomyResponse r = econ.withdrawPlayer(p.getName(), i);

			        if(r.transactionSuccess()) {
						 p.sendMessage(ChatColor.RED + "You have been fined $" + i);
			        } else {
						 p.sendMessage(ChatColor.RED + "PAYMENT FAILED!");
			        }
			 }
	        
	    }
	    
		public void addPlayer(OfflinePlayer p, double amnt) {
			if(amnt != 0){
			File f = new File("plugins/PermsPay/players");
			  boolean exists = f.exists();
			  if (!exists) {
				  try{
					  if(f.mkdir()){
						  System.out.println("[PermsPay] Directory created!");
					  }else{
						  System.out.println("[PermsPay] ERROR! Directory not created!");
					  } 
				  }catch(Exception e){
					  e.printStackTrace();
				  } 
			  }
			
			try{
			File file = new File("plugins/PermsPay/players/" + p.getName() + ".dat");
			  
	        java.util.Scanner scan;  
	        String str = null;  
	  
	        if (file.exists()) {  
	  
	            scan = new java.util.Scanner(file);  
	            str = scan.nextLine();  
	            while (scan.hasNextLine()) {  
	                str = str.concat("\n" + scan.nextLine());  
	            }  
	        }  
			  
			  str = Double.toString(amnt);
	        
	  
	        PrintWriter out = new PrintWriter(new FileWriter(file, true));  
	  
	        out.println(str);  
	        out.close();
			}catch (Exception e){
			  System.err.println("Error: " + e.getMessage());
			}	
		  }
		}

		public void removeFile(final Player p){
			getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
			    public void run() {
					File f = new File("plugins/PermsPay/players/" + p.getName() + ".dat");
					boolean success = f.delete();
				    if (!success){
				      throw new IllegalArgumentException("[PermsPay] Deletion failed!");
				  	}
			    }
			}, 20L);
		}
		
		public boolean hasCash(Player p) {		
			try{
			File file = new File("plugins/PermsPay/players/" + p.getName() + ".dat"); 
	  
	        if (file.exists()) { 
	        	return true;
	        }else{
	        	return false;
	        }

			}catch (Exception e){
			  System.err.println("Error: " + e.getMessage());
			  return false;
			}
		}
		
		public Double readCash(Player p){		
			try{
				  FileInputStream fstream = new FileInputStream("plugins/PermsPay/players/" + p.getName() + ".dat");
				  DataInputStream in = new DataInputStream(fstream);
				  BufferedReader br = new BufferedReader(new InputStreamReader(in));
				  String strLine;
				  while ((strLine = br.readLine()) != null){
				  return Double.parseDouble(strLine);
				  }
				  in.close();
				  return null;
				    }catch (Exception e){
				  System.err.println("Error: " + e.getMessage());
				  }
			return null;		
		}
}