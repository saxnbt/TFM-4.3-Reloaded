package me.StevenLawson.TotalFreedomMod.commands;

public enum AdminLevel {
    ALL("All Player Commands"), OP("OP Commands"), SUPER("SuperAdmin Commands"), SENIOR("Senior Admin Commands");
    //
    private final String friendlyName;

    AdminLevel(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }
}
