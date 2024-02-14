
package com.github.tommyt0mmy.firefighter.utility;

public enum Permissions {
    HELP_MENU("help"),
    GET_EXTINGUISHER("firetool.get"),
    USE_EXTINGUISHER("firetool.use"),
    FREEZE_EXTINGUISHER("firetool.freeze-durability"),
    FIRESET("fireset"),
    START_MISSION("fireset.startmission"),
    SET_REWARDS("fireset.rewardset"),
    SET_WAND("fireset.setwand"),
    ON_DUTY("onduty");

    private String node;

    Permissions(String node) {
        this.node = node;
    }

    public String getNode() {
        return "firefighter." + node;
    }
}
