package org.example.spigotframework.logic.utils;

import org.bukkit.Bukkit;

public class MinecraftVersion {
    /**
     * Retrieves the current version of the Bukkit server.
     *
     * @return Returns a string representing the current version of the Bukkit server, stripped of any build or snapshot information.
     */
    public static String getCurrentVersion() {
        String bukkitVersion = Bukkit.getBukkitVersion();
        String[] split = bukkitVersion.split("-");
        return split[0];
    }
}
