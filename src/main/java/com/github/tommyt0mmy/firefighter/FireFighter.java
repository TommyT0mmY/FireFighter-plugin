package com.github.tommyt0mmy.firefighter;

import com.github.tommyt0mmy.firefighter.commands.Fireset;
import com.github.tommyt0mmy.firefighter.commands.Firetool;
import com.github.tommyt0mmy.firefighter.commands.Help;
import com.github.tommyt0mmy.firefighter.events.FireExtinguisherActivation;
import com.github.tommyt0mmy.firefighter.events.FiresetWand;
import com.github.tommyt0mmy.firefighter.events.RewardsetGUI;
import com.github.tommyt0mmy.firefighter.events.onPlayerJoin;
import com.github.tommyt0mmy.firefighter.tabcompleters.FiresetTabCompleter;
import com.github.tommyt0mmy.firefighter.tabcompleters.HelpTabCompleter;
import com.github.tommyt0mmy.firefighter.utility.Configs;
import com.github.tommyt0mmy.firefighter.utility.Messages;
import com.github.tommyt0mmy.firefighter.utility.UpdateChecker;
import com.github.tommyt0mmy.firefighter.utility.XMaterial;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class FireFighter extends JavaPlugin
{

    private static FireFighter instance;

    //UPDATE CHECKER
    private final int spigotResourceId = 68772;
    private final String spigotResourceUrl = "https://www.spigotmc.org/resources/firefighter.68772/";

    public File datafolder = getDataFolder();
    public String prefix = "[" + this.getDescription().getPrefix() + "] ";
    public final String version = this.getDescription().getVersion();
    public boolean startedMission = false;
    public boolean missionsIntervalState = false;
    public boolean programmedStart = false;
    public long nextMissionStart;
    public HashMap<UUID, Integer> PlayerContribution = new HashMap<>();
    public String missionName = "";
    public HashMap<UUID, Location> fireset_first_position = new HashMap<>();
    public HashMap<UUID, Location> fireset_second_position = new HashMap<>();
    public Logger console = getLogger();
    public Messages messages = null;
    public Configs configs = null;

    public static FireFighter getInstance()
    {
        return instance;
    }

    private void setInstance(FireFighter instance)
    {
        FireFighter.instance = instance;
    }

    public void onEnable()
    {
        //priority 1
        setInstance(this);

        //priority 2
        configs = new Configs();
        messages = new Messages();
        nextMissionStart = System.currentTimeMillis() + ((configs.getConfig().getInt("missions_interval")) * 1000);

        //priority 3
        loadEvents();
        loadCommands();
        loadRecipes();

        //priority 4
        @SuppressWarnings("unused")
        BukkitTask task = new MissionsHandler().runTaskTimer(this, 0, 20);

        //checking for updates
        UpdateChecker updateChecker = new UpdateChecker();
        if (updateChecker.needsUpdate())
        {
            console.info("An update for FireFighter is available at:");
            console.info(spigotResourceUrl);
            console.info(String.format("Installed version: %s Lastest version: %s", updateChecker.getCurrent_version(), updateChecker.getLastest_version()));
        }

        console.info("FireFighter v" + version + " enabled succesfully");
    }

    public void onDisable()
    {
        console.info("FireFighter v" + version + " disabled succesfully");
    }

    private void loadEvents()
    {
        this.getServer().getPluginManager().registerEvents(new FireExtinguisherActivation(), this);
        this.getServer().getPluginManager().registerEvents(new FiresetWand(), this);
        this.getServer().getPluginManager().registerEvents(new RewardsetGUI(), this);
        this.getServer().getPluginManager().registerEvents(new onPlayerJoin(), this);
    }

    private void loadCommands()
    {
        getCommand("firefighter").setExecutor(new Help());
        getCommand("fireset").setExecutor(new Fireset());
        getCommand("firetool").setExecutor(new Firetool());
        getCommand("firefighter").setTabCompleter(new HelpTabCompleter());
        getCommand("fireset").setTabCompleter(new FiresetTabCompleter());
    }

    private void loadRecipes()
    {
        ItemStack fire_extinguisher = getFireExtinguisher();

        NamespacedKey key = new NamespacedKey(this, "fire_extinguisher");
        ShapedRecipe fire_extinguisher_recipe = new ShapedRecipe(key, fire_extinguisher);

        fire_extinguisher_recipe.shape("aih", "awa", "aia");
        fire_extinguisher_recipe.setIngredient('a', Material.AIR);
        fire_extinguisher_recipe.setIngredient('i', Material.IRON_INGOT);
        fire_extinguisher_recipe.setIngredient('h', Material.HOPPER);
        fire_extinguisher_recipe.setIngredient('w', Material.WATER_BUCKET);

        Bukkit.addRecipe(fire_extinguisher_recipe);
    }

    public ItemStack getFireExtinguisher()
    {
        ItemStack fire_extinguisher = XMaterial.IRON_HOE.parseItem();
        //getting meta
        ItemMeta meta = fire_extinguisher.getItemMeta();
        //modifying meta
        meta.setDisplayName("" + ChatColor.RED + "" + ChatColor.BOLD + messages.getMessage("fire_extinguisher"));
        List<String> lore = new ArrayList<String>();
        lore.add(messages.getMessage("fire_extinguisher"));
        lore.add(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + messages.getMessage("hold_right_click"));
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        //setting meta
        meta.setLore(lore);
        fire_extinguisher.setItemMeta(meta);
        return fire_extinguisher;
    }

    public int getSpigotResourceId()
    {
        return spigotResourceId;
    }

    public String getSpigotResourceUrl()
    {
        return spigotResourceUrl;
    }
}
