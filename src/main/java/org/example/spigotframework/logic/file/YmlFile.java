package org.example.spigotframework.logic.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.example.spigotframework.Main;
import org.example.spigotframework.logic.file.interfaces.IFile;
import org.example.spigotframework.logic.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class YmlFile implements IFile<FileConfiguration> {
    private final Plugin plugin;
    private final String fileName;
    private FileConfiguration configuration = null;
    private File file = null;

    public YmlFile(String fileName) {
        this.fileName = fileName + ".yml";
        this.plugin = Main.getInstance();
        saveDefaultConfig();
    }

    private void saveDefaultConfig() {
        if (this.file == null) {
            this.file = new File(plugin.getDataFolder(), fileName);
        }
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
    }

    @Override
    public boolean load() {
        if (this.file == null) {
            this.file = new File(plugin.getDataFolder(), fileName);
        }
        if (!file.exists()) {
            return false;
        }

        this.configuration = YamlConfiguration.loadConfiguration(file);
        return true;
    }

    @Override
    public void save() {
        if (this.configuration == null || this.file == null) {
            return;
        }
        try {
            configuration.save(file);
        } catch (IOException e) {
            Logger.severe(e.getMessage());
        }
    }

    @Override
    public void reload() {
        if (this.file == null) {
            this.file = new File(plugin.getDataFolder(), fileName);
        }

        this.configuration = YamlConfiguration.loadConfiguration(file);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        final InputStream stream = this.plugin.getResource(this.fileName);
        if (stream != null) {
            final InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            final YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(streamReader);
            this.configuration.setDefaults(yamlConfiguration);
        }
    }

    @Override
    public FileConfiguration get() {
        if (this.configuration == null) {
            reload();
        }
        return configuration;
    }
}
