// My default main class
package us.com.stellarsquad.stellarcraft;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import us.com.stellarsquad.stellarcraft.addons.*;
import us.com.stellarsquad.stellarcraft.api.PlaceholderAPI;
import us.com.stellarsquad.stellarcraft.cache.PlayerCache;
import us.com.stellarsquad.stellarcraft.commands.AdvancedCommands;
import us.com.stellarsquad.stellarcraft.commands.DefaultCommands;
import us.com.stellarsquad.stellarcraft.commands.PersonalCommands;
import us.com.stellarsquad.stellarcraft.events.player.ChatListener;
import us.com.stellarsquad.stellarcraft.events.player.PlayerListener;
import us.com.stellarsquad.stellarcraft.management.ConfigManager;
import us.com.stellarsquad.stellarcraft.objects.TitleObjects;
import us.com.stellarsquad.stellarcraft.plugins.*;

public final class Loader extends JavaPlugin {

    private PersonalCommands personalCommands;
    private DefaultCommands defautCommands;
    private AdvancedCommands advancedCommands;

    private boolean placeholderAPIEnabled;
    private boolean multiverseEnabled;

    private updateConfig updateFiles;

    private static Loader core;
  
    public static ConfigManager settings;
  
    private PlaceholderAPI phapi;
    private SQLProvider sqlp = new SQLProvider(this);

    private void registerCommands() {

        personalCommands = new PersonalCommands(this);
        defautCommands = new DefaultCommands(this);
        advancedCommands = new AdvancedCommands(this);

    }

    Listener[] listeners = {
            new PlayerListener(this), new ChatListener(this), new TitleObjects(), new Prevention(this), 
            new StellarAPI(this), new StrengthFix(this), new Backpacks(this), new Chat(this), , new StellarBoard(this)
    };

    @Override
    public void onEnable() {

        loggerInfo(StellarSource.c("&7&m----------------------------------"));
        loggerInfo(StellarSource.c("&dStellarCraft - Essentials"));
        getServer().getConsoleSender().sendMessage(StellarSource.c("&eVersion: &a") + getDescription().getVersion());
        loggerInfo(StellarSource.c("&eDesc: &a") + getDescription().getDescription());
        loggerInfo(StellarSource.c("&eAutor: &aTyranzx"));

        if (getVersion() <= 9) {
            loggerInfo("&eStellarCore es para la versión &61.20.1");
            loggerInfo("&c- StellarAPI 1.20.1 desactivado");
            loggerInfo("&7(Si no se instala en un servidor v1.20.1, varias opciones o funcionalidades serán desactivadas. Esto porque el core se pensó para 1.20.1 pero utilizable para 1.8.8).");
        }
      
        multiverseEnabled = getServer().getPluginManager().isPluginEnabled("Multiverse-Core");
        if (multiverseEnabled) {
            multiverseCore = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderAPIEnabled = true;
            new PlaceholderAPI(this).register();
        } else {
            placeholderAPIEnabled = false;
            loggerInfo(StellarSource.c("&ePlacerHolderAPI no está activado!"));
        }

        settings = ConfigManager.getInstance();

        core = this;
        settings.setup(this);

        this.registerSQLManager();
        this.registerCommands();
        this.registerAddons();
        this.registerListeners();

        loggerInfo(StellarSource.c("&7&m----------------------------------"));
    }

    public static Loader getInstance(){
        return core;
    }

     private void registerSQLManager() {
         sqlp.mysqlSetup();
     } 

    private void registerAddons(){
        updateFiles = new updateConfig(this);
        updateFiles.task();
    }
    public void onDisable(){
        Backpacks.saveEntryMap();
        PlayerCache pc = new PlayerCache();
        pc.deletePlayerCache(this);
    }
}
