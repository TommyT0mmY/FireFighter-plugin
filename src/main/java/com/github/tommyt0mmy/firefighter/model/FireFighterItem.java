package com.github.tommyt0mmy.firefighter.model;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FireFighterItem {

    String name;
    String displayName;
    String particle;
    ItemStack item;
    int dur = 1;

    int r;
    int g;
    int b;


    public FireFighterItem(String name, ItemStack item, int dur) {
        this.name = name;
        this.item = item;
        this.dur = dur;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getDur() {
        return dur;
    }

    public void setDur(int dur) {
        this.dur = dur;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public String getParticle() {
        return particle;
    }

    public void setParticle(String particle) {
        this.particle = particle;
    }
}
