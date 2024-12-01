package org.example.spigotframework.logic.module;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.example.spigotframework.Main;
import org.example.spigotframework.logic.commands.CommandHandler;
import org.example.spigotframework.logic.logging.Logger;
import org.example.spigotframework.logic.module.interfaces.IModule;
import org.example.spigotframework.logic.tasks.ITask;
import org.example.spigotframework.logic.utils.Result;
import org.example.spigotframework.logic.utils.Status;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class Module implements IModule {
    private final Main mainClass;
    private final String moduleName;
    private final List<Listener> events;
    private final List<CommandHandler> commands;
    private final List<ITask> tasks;
    private final CommandMap commandMap;
    private final String STATUS_NOT_ENABLING;
    private Status moduleStatus;

    protected Module() {
        mainClass = Main.getInstance();
        moduleName = this.getClass().getSimpleName();
        STATUS_NOT_ENABLING = "The " + moduleName + " status is not ENABLING.";
        CommandMap tempCommandMap = null;
        try {
            tempCommandMap = (CommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            Logger.severe(e.getMessage());
        }
        if (tempCommandMap == null) {
            moduleStatus = Status.DISABLED;
            Bukkit.shutdown();
        }

        commandMap = tempCommandMap;
        events = new ArrayList<>();
        commands = new ArrayList<>();
        tasks = new ArrayList<>();
        moduleStatus = Status.IDLE;
    }

    /**
     * Loads the module and returns the result.
     *
     * @return The result of the load operation.
     */
    public Result<String> load() {
        if (!moduleStatus.equals(Status.IDLE)) {
            return new Result<>(null, new Exception("An attempt was made to load the " + moduleName + " while it was not idle."));
        }

        moduleStatus = Status.LOADING;
        Logger.info("Loading " + moduleName + "...");
        onLoad();
        moduleStatus = Status.LOADED;
        return new Result<>("Module has been successfully loaded.", null);
    }

    /**
     * Enables the module and returns the result.
     *
     * @return The result of the enable operation.
     */
    public Result<String> enable() {
        if (!moduleStatus.equals(Status.LOADED)) {
            return new Result<>(null, new Exception("An attempt was made to enable the " + moduleName + " while it was not loaded."));
        }

        moduleStatus = Status.ENABLING;
        Logger.info("Enabling " + moduleName + "...");
        try {
            onEnable();
            moduleStatus = Status.ENABLED;
            return new Result<>("Module has been successfully enabled.", null);
        } catch (Exception e) {
            moduleStatus = Status.DISABLING;
            Logger.severe(e.getMessage());
            Bukkit.shutdown();
            moduleStatus = Status.DISABLED;
            return new Result<>(null, e);
        }
    }

    /**
     * Disables the module and returns the result.
     *
     * @return The result of the disable operation.
     */
    public Result<String> disable() {
        if (!moduleStatus.equals(Status.ENABLED)) {
            return new Result<>(null, new Exception("An attempt was made to disable the " + moduleName + " while it was not enabled."));
        }

        moduleStatus = Status.DISABLING;
        int amountOfComponents = events.size() + commands.size();
        Logger.info("Disabling " + moduleName + ", removing " + amountOfComponents + " components...");
        events.forEach(HandlerList::unregisterAll);
        for (CommandHandler commandHandler : commands) {
            PluginCommand pluginCommand = mainClass.getCommand(commandHandler.getName());
            if (pluginCommand != null) {
                pluginCommand.unregister(commandMap);
            }
        }
        for (ITask task : tasks) {
            task.disable();
        }

        events.clear();
        commands.clear();
        tasks.clear();
        onDisable();
        moduleStatus = Status.DISABLED;
        return new Result<>("Module has been successfully disabled.", null);
    }

    /**
     * Registers an event listener supplied by the given supplier.
     *
     * @param supplier the supplier of the event listener.
     * @throws RuntimeException if the module is not in the enabling state.
     */
    protected void event(@NonNull Supplier<? extends Listener> supplier) {
        if (!moduleStatus.equals(Status.ENABLING)) {
            throw new IllegalStateException(STATUS_NOT_ENABLING);
        }

        Listener listener = supplier.get();
        Bukkit.getServer().getPluginManager().registerEvents(listener, mainClass);
        events.add(listener);
    }

    /**
     * Registers a command handler supplied by the given supplier.
     *
     * @param supplier the supplier of the command handler.
     * @throws RuntimeException if the module is not in the enabling state or if the plugin command is null.
     */
    protected void command(@NonNull Supplier<? extends CommandHandler> supplier) {
        if (!moduleStatus.equals(Status.ENABLING)) {
            throw new IllegalStateException(STATUS_NOT_ENABLING);
        }

        CommandHandler commandHandler = supplier.get();
        PluginCommand pluginCommand = mainClass.getCommand(commandHandler.getName());
        if (pluginCommand == null) {
            throw new NullPointerException("The plugin command is null.");
        }

        pluginCommand.setExecutor(commandHandler);
        pluginCommand.setTabCompleter(commandHandler);
        commands.add(commandHandler);
    }

    /**
     * Schedules a task provided by the given supplier.
     *
     * @param supplier the supplier of the task.
     * @throws RuntimeException if the module is not in the enabling state.
     */
    protected void task(@NonNull Supplier<? extends ITask> supplier) {
        if (!moduleStatus.equals(Status.ENABLING)) {
            throw new IllegalStateException(STATUS_NOT_ENABLING);
        }

        ITask task = supplier.get();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        };
        long delay = task.getDelay();
        long period = task.getPeriod();
        boolean isAsync = task.isAsync();
        if (isAsync) {
            runnable.runTaskTimerAsynchronously(mainClass, delay, period);
        } else {
            runnable.runTaskTimer(mainClass, delay, period);
        }

        tasks.add(task);
    }
}
