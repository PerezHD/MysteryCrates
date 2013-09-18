package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public FileConfiguration customConfig = null;
	public File customConfigFile = null;
	public String prefix = "[MysteryCrates] ";
	public void onEnable(){
		getConfig();
		saveDefaultConfig();
		reloadCustomConfig();
	}
	
	public FileConfiguration getCustomConfig() {
		if (customConfig == null) {
			reloadCustomConfig();
		}
		return customConfig;
	}
	
	public void saveCustomConfig() {
		if (customConfig == null || customConfigFile == null) {
			return;
		}
		try {
			getCustomConfig().save(customConfigFile);
		} catch (IOException ex) {
			this.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
		}
	}
	
	
	public void reloadCustomConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(this.getDataFolder(), "cratesDatabase.yml");
		}
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	 
		InputStream defConfigStream = this.getResource("cratesDatabase.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			customConfig.setDefaults(defConfig);
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		boolean isValidCommand = false;
		if(cmd.getName().equalsIgnoreCase("myst")){
			if (args[0].equalsIgnoreCase("give")){
				isValidCommand = true;
				if(sender.hasPermission("myst.give")){
					if(args.length==3){
						OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
						int quantity = Integer.parseInt(args[2]);
						int crates = getCustomConfig().getInt("crates."+args[1]);
						if (!getCustomConfig().contains("crates."+args[1])){
							getCustomConfig().createSection("crates."+args[1]);
						}
						getCustomConfig().set("crates."+args[1], crates + quantity);
						
						this.saveCustomConfig();
						if(player.isOnline()){
							((Player) player).sendMessage(ChatColor.GREEN+prefix+"You were just given "+args[2]+" mystery crates! Open them with /myst open");
						}
					}else{
						sender.sendMessage(ChatColor.RED+prefix+"Poper usage: /myst give [player] [quantity]");
					}
				}else{
					sender.sendMessage(ChatColor.RED+prefix+"Insufficient permissions");
				}
				
				
			}
			
			
			
			
			if (args[0].equalsIgnoreCase("open")){
				isValidCommand = true;
				if(sender instanceof Player){
					int crates = getCustomConfig().getInt("crates."+sender.getName());
					if (crates>0){
						getCustomConfig().set("crates."+sender.getName(), crates - 1);
						saveCustomConfig();
						Random r = new Random();
						List<String> list = getConfig().getStringList("rewards.items");
						String item = list.get(r.nextInt(list.size()));
						getServer().dispatchCommand(getServer().getConsoleSender(), "give "+sender.getName()+" "+item);
						sender.sendMessage(ChatColor.GREEN+prefix+"Crate opened. You have "+(crates-1)+" crates left");
					}else{
						sender.sendMessage(ChatColor.RED+prefix+"You don't have any crates!");
					}


					
				}
			}
			return true;
		}
		return false; 
	}
	
	
	
	
	public int getCrates(String name){
		if(getCustomConfig().contains("crates."+name)){
			return getCustomConfig().getInt("crates."+name);
		}else{
			return 0;
		}
	}
	
	
	
}
