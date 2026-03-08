package pl.listaserwery.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.listaserwery.plugin.command.OdbierzCommand;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

public final class ListaSerweryPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Inicjalizacja konfiguracji
        this.saveDefaultConfig();
        
        // Dynamiczna rejestracja komendy
        this.registerCommand();

        this.getLogger().info("ListaSerweryPlugin zostal pomyslnie uruchomiony!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("ListaSerweryPlugin zostal wylaczony.");
    }

    private void registerCommand() {
        FileConfiguration config = this.getConfig();
        String commandName = config.getString("command.name", "odbierz");
        List<String> aliases = config.getStringList("command.aliases");

        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            Command command = new OdbierzCommand(this, commandName, aliases);
            commandMap.register(commandName, command);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            this.getLogger().log(Level.SEVERE, "Nie udalo sie dynamicznie zarejestrowac komendy!", e);
        }
    }
}
