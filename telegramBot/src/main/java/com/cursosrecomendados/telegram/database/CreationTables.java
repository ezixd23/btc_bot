package com.cursosrecomendados.telegram.database;

public class CreationTables {
	public static final int version = 8;
    public static final String createVersionTable = "CREATE TABLE IF NOT EXISTS Versions(ID INTEGER AUTO_INCREMENT, PRIMARY KEY(ID), Version INTEGER);";
    public static final String insertCurrentVersion = "INSERT INTO Versions (Version) VALUES(%d);";
    public static final String createFilesTable = "CREATE TABLE IF NOT EXISTS Files (fileId VARCHAR(100), PRIMARY KEY(fileId), userId INTEGER NOT NULL, caption TEXT NOT NULL)";
    public static final String createUsersForFilesTable = "CREATE TABLE IF NOT EXISTS FilesUsers (userId INTEGER, PRIMARY KEY(userId), status INTEGER NOT NULL DEFAULT 0)";
    public static final String createRecentCoinTable = "CREATE TABLE IF NOT EXISTS RecentCoin (ID INTEGER AUTO_INCREMENT, PRIMARY KEY(ID), userId INTEGER NOT NULL, " +
            "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, coinId INTEGER NOT NULL, coinName VARCHAR(60) NOT NULL," +
            "CONSTRAINT unique_cistyuser UNIQUE (userId,coinId))";
    public static final String createDirectionsDatabase = "CREATE TABLE IF NOT EXISTS Directions (userId INTEGER, PRIMARY KEY(userId), status INTEGER NOT NULL, " +
            "messageId INTEGER NOT NULL DEFAULT 0, origin VARCHAR(100));";
    public static final String createUserLanguageDatabase = "CREATE TABLE IF NOT EXISTS UserLanguage (userId INTEGER, PRIMARY KEY(userId), languageCode VARCHAR(10) NOT NULL)";
    public static final String createUserCoinOptionDatabase = "CREATE TABLE IF NOT EXISTS UserCoinOptions (userId INTEGER, PRIMARY KEY(userId), languageCode VARCHAR(10) NOT NULL DEFAULT 'en', " +
            "currency VARCHAR(10) NOT NULL DEFAULT 'USD')";
    public static final String createCoinStateTable = "CREATE TABLE IF NOT EXISTS CoinState (userId INTEGER NOT NULL, chatId BIGINT NOT NULL, state INTEGER NOT NULL DEFAULT 0, " +
            "languageCode VARCHAR(10) NOT NULL DEFAULT 'en', " +
            "CONSTRAINT `watherPrimaryKey` PRIMARY KEY(userId,chatId));";

    public static final String CREATE_COMMANDS_TABLE = "CREATE TABLE IF NOT EXISTS CommandUsers (userId INTEGER, PRIMARY KEY(userId), status INTEGER NOT NULL);";
}
