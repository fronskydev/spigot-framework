package org.example.spigotframework.logic.commands;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.example.spigotframework.logic.commands.annotations.CommandClass;
import org.example.spigotframework.logic.commands.annotations.SubCommandMethod;
import org.example.spigotframework.logic.commands.interfaces.ICommandExecutor;
import org.example.spigotframework.logic.logging.Logger;
import org.example.spigotframework.logic.utils.Language;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class CommandHandler implements TabCompleter, CommandExecutor, ICommandExecutor {
    @Getter
    private final String name;
    @Getter
    private final String permission;
    @Getter
    private final List<String> subcommands;
    private final boolean isValid;
    private Player player;

    protected CommandHandler() {
        subcommands = new ArrayList<>();
        isValid = getClass().isAnnotationPresent(CommandClass.class);
        if (!isValid) {
            name = "invalid";
            permission = "invalid";
            return;
        }

        CommandClass commandClass = getClass().getAnnotation(CommandClass.class);
        name = commandClass.name();
        permission = commandClass.permission();
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(SubCommandMethod.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 3) continue;
                if (!parameterTypes[0].equals(CommandSender.class)) continue;
                if (!parameterTypes[1].equals(String.class)) continue;
                if (!parameterTypes[2].equals(String[].class)) continue;

                subcommands.add(method.getName());
            }
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!isValid) return true;

        player = null;
        if (sender instanceof Player) player = (Player) sender;
        if (!subcommands.isEmpty()) {
            String subcommand = getSubcommand(args);
            if (!subcommand.isEmpty() && hasPermission(player, permission + "." + subcommand.toLowerCase())) {
                try {
                    Method method = this.getClass().getMethod(subcommand, CommandSender.class, String.class, String[].class);
                    method.invoke(this, sender, label, getSubcommandArgs(args));
                    return true;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
                    Logger.severe("An error occurred while invoking subcommand method.");
                }
            }
        }
        if (!hasPermission(player, permission)) return true;

        onCommand(sender, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String[] args) {
        if (!isValid) return new ArrayList<>();

        List<String> completions = new ArrayList<>();
        player = null;
        if (sender instanceof Player) player = (Player) sender;
        if (args.length == 1) {
            subcommands.stream()
                    .filter(subcommand -> subcommand.startsWith(args[0]) && (player != null && hasPermission(player, permission + "." + subcommand)))
                    .forEach(completions::add);
        }
        return completions;
    }

    /**
     * Checks if the given player has the specified permission.
     *
     * @param player The Player object representing the player to check the permission for. A null value bypasses the permission check.
     * @param permission The string representing the permission to check.
     * @return Returns true if the player has the specified permission, or if the player object is null. Returns false otherwise.
     *
     * Note: Logs a severe message if the permission string is empty, and returns false.
     */
    protected boolean hasPermission(Player player, String permission) {
        if (player == null) {
            return true;
        }
        if (permission.isEmpty()) {
            Logger.severe("Permissions haven't been set. Make sure to initialize them correctly.");
            return false;
        }
        if (! player.hasPermission(permission)) {
            player.sendMessage(Language.NO_PERMISSION.getMessageWithColor());
            return false;
        }
        return true;
    }

    private String getSubcommand(String[] args) {
        if (args.length == 0 || subcommands.isEmpty()) {
            return "";
        }
        for (String subcommand : subcommands) {
            if (subcommand.equalsIgnoreCase(args[0])) {
                return subcommand;
            }
        }
        return "";
    }

    private String[] getSubcommandArgs(String[] args) {
        if (args.length == 0 || subcommands.isEmpty()) {
            return new String[0];
        }

        String[] subcommandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subcommandArgs, 0, subcommandArgs.length);
        return subcommandArgs;
    }
}
