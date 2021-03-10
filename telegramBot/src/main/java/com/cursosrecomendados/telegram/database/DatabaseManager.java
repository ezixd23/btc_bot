package com.cursosrecomendados.telegram.database;


import org.telegram.telegrambots.meta.logging.BotLogger;

public class DatabaseManager {
	private static final String LOGTAG = "DATABASEMANAGER";

    private static volatile DatabaseManager instance;
    private static volatile ConnectionDB connetion;

    
    private DatabaseManager() {
        connetion = new ConnectionDB();
        final int currentVersion = connetion.checkVersion();
        BotLogger.info(LOGTAG, "Current db version: " + currentVersion);
    }

    
    public static DatabaseManager getInstance() {
        final DatabaseManager currentInstance;
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }
        return currentInstance;
    }

}
