package com.github.tommyt0mmy.firefighter;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import com.github.tommyt0mmy.firefighter.utility.Configs;
import com.github.tommyt0mmy.firefighter.utility.Messages;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.tommyt0mmy.firefighter.commands.Fireset;
import com.github.tommyt0mmy.firefighter.commands.Firetool;
import com.github.tommyt0mmy.firefighter.commands.Help;
import com.github.tommyt0mmy.firefighter.events.FireExtinguisherActivation;
import com.github.tommyt0mmy.firefighter.events.FiresetWand;
import com.github.tommyt0mmy.firefighter.events.RewardsetGUI;
import com.github.tommyt0mmy.firefighter.tabcompleters.FiresetTabCompleter;
import com.github.tommyt0mmy.firefighter.tabcompleters.HelpTabCompleter;

public class FireFighter extends JavaPlugin {

    private static FireFighter instance;

    public File datafolder = getDataFolder();
    public String prefix = "[" + this.getDescription().getPrefix() + "] ";
    public final String version = this.getDescription().getVersion();
    public boolean startedMission = false;
    public HashMap< UUID, Integer > PlayerContribution = new HashMap<>();
    public String missionName = "";
    public HashMap< UUID, Location > fireset_first_position = new HashMap<>();
    public HashMap< UUID, Location > fireset_second_position = new HashMap<>();
    public Logger console = getLogger();
    public Messages messages = null;
    public Configs configs = null;

    public static FireFighter getInstance() {
        return instance;
    }

    private void setInstance(FireFighter instance) {
        FireFighter.instance = instance;
    }

    public void onEnable() {
        //priority 1
        setInstance(this);

        //priority 2
        configs = new Configs();
        messages = new Messages();

        //priority 3
        loadEvents();
        loadCommands();

        //priority 4
        @SuppressWarnings("unused")
        BukkitTask task = new MissionsHandler().runTaskTimer(this, 0, (int)(getConfig().get("missions_interval")) * 20);


        console.info("FireFighter v" + version + " enabled succesfully");
    }

    public void onDisable() {
        console.info("FireFighter v" + version + " disabled succesfully");
    }

    private void loadEvents() {
        this.getServer().getPluginManager().registerEvents(new FireExtinguisherActivation(), this);
        this.getServer().getPluginManager().registerEvents(new FiresetWand(), this);
        this.getServer().getPluginManager().registerEvents(new RewardsetGUI(), this);
    }

    private void loadCommands() {
        getCommand("firefighter").setExecutor(new Help());
        getCommand("fireset").setExecutor(new Fireset());
        getCommand("firetool").setExecutor(new Firetool());
        getCommand("firefighter").setTabCompleter(new HelpTabCompleter());
        getCommand("fireset").setTabCompleter(new FiresetTabCompleter());
    }

    @Override
    public FileConfiguration getConfig() throws NullPointerException {
        return null;
    }
}