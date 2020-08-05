package com.github.tommyt0mmy.firefighter;

import com.github.tommyt0mmy.firefighter.commands.Fireset;
import com.github.tommyt0mmy.firefighter.commands.Firetool;
import com.github.tommyt0mmy.firefighter.commands.Help;
import com.github.tommyt0mmy.firefighter.events.FireExtinguisherActivation;
import com.github.tommyt0mmy.firefighter.events.FiresetWand;
import com.github.tommyt0mmy.firefighter.events.RewardsetGUI;
import com.github.tommyt0mmy.firefighter.tabcompleters.FiresetTabCompleter;
import com.github.tommyt0mmy.firefighter.tabcompleters.HelpTabCompleter;
import com.github.tommyt0mmy.firefighter.utility.Configs;
import com.github.tommyt0mmy.firefighter.utility.Messages;
import com.github.tommyt0mmy.firefighter.utility.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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


        ShapedRecipe fire_extinguisher_recipe = new ShapedRecipe(fire_extinguisher);
        fire_extinguisher_recipe.shape("aih", "awa", "aia");
        fire_extinguisher_recipe.setIngredient('a', XMaterial.AIR.parseMaterial());
        fire_extinguisher_recipe.setIngredient('i', XMaterial.IRON_INGOT.parseMaterial());
        fire_extinguisher_recipe.setIngredient('h', XMaterial.HOPPER.parseMaterial());
        fire_extinguisher_recipe.setIngredient('w', (XMaterial.WATER_BUCKET.parseMaterial()));

        getServer().addRecipe(fire_extinguisher_recipe);
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
}
