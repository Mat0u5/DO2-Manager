package net.mat0u5.do2manager.utils;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.mat0u5.do2manager.command.Command;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.events.Events;

import javax.xml.crypto.Data;

public class ModRegistries {
    public static void registerModStuff() {
        try {
            registerCommands();
            registerEvents();
            if (DatabaseManager.isFirstStartup()) DatabaseManager.initialize();
            TextUtils.setEmotes();
        }catch (Exception e) {}
    }
    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(Command::register);
    }
    private static void registerEvents() {
        Events.register();
    }
}
