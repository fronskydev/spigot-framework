package org.example.spigotframework;

import org.bukkit.plugin.java.JavaPlugin;
import org.example.spigotframework.logic.logging.Logger;
import org.example.spigotframework.logic.module.ModuleManager;

public class Main extends JavaPlugin {
    private final ModuleManager moduleManager = new ModuleManager();

    @Override
    public void onLoad() {
//        moduleManager.prepare(new *******Module());
        try {
            moduleManager.load();
        } catch (Exception e) {
            Logger.severe("Failed to load modules!");
        }
    }

    @Override
    public void onEnable() {
        try {
            moduleManager.enable();
        } catch (Exception e) {
            Logger.severe("Failed to enable modules!");
        }
    }

    @Override
    public void onDisable() {
        try {
            moduleManager.disable();
        } catch (Exception e) {
            Logger.severe("Failed to disable modules!");
        }
    }

    public static Main getInstance() {
        return getPlugin(Main.class);
    }
}
