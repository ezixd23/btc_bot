package com.cursosrecomendados.telegram.database;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.telegram.telegrambots.meta.logging.BotLogger;

public class DatabaseManager {
	private static final String LOGTAG = "DATABASEMANAGER";

    private static volatile DatabaseManager instance;
    private static volatile ConnectionDB connetion;

    
    private DatabaseManager() {
        connetion = new ConnectionDB();
        final int currentVersion = connetion.checkVersion();
        BotLogger.info(LOGTAG, "Current db version: " + currentVersion);
        if (currentVersion < CreationTables.version) {
            recreateTable(currentVersion);
        }
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
    
    private void recreateTable(int currentVersion) {
        try {
            connetion.initTransaction();
            if (currentVersion == 0) {
                currentVersion = createNewTables();
            }
            if (currentVersion == 1) {
                currentVersion = updateToVersion2();
            }
            if (currentVersion == 2) {
                currentVersion = updateToVersion3();
            }
            if (currentVersion == 3) {
                currentVersion = updateToVersion4();
            }
            if (currentVersion == 4) {
                currentVersion = updateToVersion5();
            }
            if (currentVersion == 5) {
                currentVersion = updateToVersion6();
            }
            if (currentVersion == 6) {
                currentVersion = updateToVersion7();
            }
            if (currentVersion == 7) {
                currentVersion = updateToVersion8();
            }
            connetion.commitTransaction();
        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
    
    private int updateToVersion2() throws SQLException {
        connetion.executeQuery(CreationTables.createRecentCoinTable);
        connetion.executeQuery(String.format(CreationTables.insertCurrentVersion, 2));
        return 2;
    }

    private int updateToVersion3() throws SQLException {
        connetion.executeQuery(CreationTables.createDirectionsDatabase);
        connetion.executeQuery(String.format(CreationTables.insertCurrentVersion, 3));
        return 3;
    }

    private int updateToVersion4() throws SQLException {
        connetion.executeQuery(String.format(CreationTables.insertCurrentVersion, 4));
        return 4;
    }

    private int updateToVersion5() throws SQLException {
        connetion.executeQuery(CreationTables.createUserLanguageDatabase);
        connetion.executeQuery(String.format(CreationTables.insertCurrentVersion, 5));
        return 5;
    }

    private int updateToVersion6() throws SQLException {
        connetion.executeQuery(CreationTables.createCoinStateTable);
        connetion.executeQuery(String.format(CreationTables.insertCurrentVersion, 6));
        return 6;
    }

    private int updateToVersion7() throws SQLException {
        connetion.executeQuery("ALTER TABLE WeatherState MODIFY chatId BIGINT NOT NULL");
        connetion.executeQuery(String.format(CreationTables.insertCurrentVersion, 7));
        return 7;
    }

    private int updateToVersion8() throws SQLException {
        connetion.executeQuery(CreationTables.CREATE_COMMANDS_TABLE);
        connetion.executeQuery(String.format(CreationTables.insertCurrentVersion, 8));
        return 8;
    }

    private int createNewTables() throws SQLException {
        connetion.executeQuery(CreationTables.createVersionTable);
        connetion.executeQuery(CreationTables.createFilesTable);
        connetion.executeQuery(String.format(CreationTables.insertCurrentVersion, CreationTables.version));
        connetion.executeQuery(CreationTables.createUsersForFilesTable);
        connetion.executeQuery(CreationTables.createRecentCoinTable);
        connetion.executeQuery(CreationTables.createDirectionsDatabase);
        connetion.executeQuery(CreationTables.createUserLanguageDatabase);
        connetion.executeQuery(CreationTables.createCoinStateTable);
        connetion.executeQuery(CreationTables.createUserCoinOptionDatabase);
        connetion.executeQuery(CreationTables.CREATE_COMMANDS_TABLE);
        return CreationTables.version;
    }
    
    public boolean setUserStateForCommandsBot(Integer userId, boolean active) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("INSERT INTO CommandUsers (userId, status) VALUES(?, ?) ON DUPLICATE KEY UPDATE status=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, active ? 1 : 0);
            preparedStatement.setInt(3, active ? 1 : 0);

            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }
    
    public boolean getUserStateForCommandsBot(Integer userId) {
        int status = -1;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("Select status FROM CommandUsers WHERE userId=?");
            preparedStatement.setInt(1, userId);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                status = result.getInt("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status == 1;
    }

    public boolean addFile(String fileId, Integer userId, String caption) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("INSERT INTO Files (fileId, userId, caption) VALUES(?, ?, ?)");
            preparedStatement.setString(1, fileId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(3, caption);

            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }

    public HashMap<String, String> getFilesByUser(Integer userId) {
        HashMap<String, String> files = new HashMap<>();
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT * FROM Files WHERE userId = ?");
            preparedStatement.setInt(1, userId);
            final ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                files.put(result.getString("fileId"), result.getString("caption"));
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return files;
    }

    public boolean addUserForFile(Integer userId, int status) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("INSERT INTO FilesUsers (userId, status) VALUES(?, ?)");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, status);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }
    
    public boolean deleteUserForFile(Integer userId) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("DELETE FROM FilesUsers WHERE userId=?;");
            preparedStatement.setInt(1, userId);

            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }

    public int getUserStatusForFile(Integer userId) {
        int status = -1;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("Select status FROM FilesUsers WHERE userId=?");
            preparedStatement.setInt(1, userId);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                status = result.getInt("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public boolean doesFileExists(String fileId) {
        boolean exists = false;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("Select fileID FROM Files WHERE fileId=?");
            preparedStatement.setString(1, fileId);
            final ResultSet result = preparedStatement.executeQuery();
            exists = result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    public boolean deleteFile(String fileId) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("DELETE FROM Files WHERE fileId=?;");
            preparedStatement.setString(1, fileId);

            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }
    
    public boolean addRecentCoin(Integer userId, Integer coinId, String coinName) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("REPLACE INTO RecentCoin (userId, cityId, cityName) VALUES(?, ?, ?)");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, coinId);
            preparedStatement.setString(3, coinName);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }

    public List<String> getRecentCoin(Integer userId) {
        List<String> recentWeather = new ArrayList<>();
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("select * FROM RecentCoin WHERE userId=? order by date desc");
            preparedStatement.setInt(1, userId);
            final ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                recentWeather.add(result.getString("cityName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recentWeather;
    }
    
    public int getUserDestinationStatus(Integer userId) {
        int status = -1;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT status FROM Directions WHERE userId = ?");
            preparedStatement.setInt(1, userId);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                status = result.getInt("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }
    
    public int getUserDestinationMessageId(Integer userId) {
        int messageId = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT messageId FROM Directions WHERE userId = ?");
            preparedStatement.setInt(1, userId);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                messageId = result.getInt("messageId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageId;
    }

    public String getUserOrigin(Integer userId) {
        String origin = "";
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT origin FROM Directions WHERE userId = ?");
            preparedStatement.setInt(1, userId);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                origin = result.getString("origin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return origin;
    }

    public boolean deleteUserForDirections(Integer userId) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("DELETE FROM Directions WHERE userId=?;");
            preparedStatement.setInt(1, userId);

            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }
    
    public String getUserLanguage(Integer userId) {
        String languageCode = "en";
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT languageCode FROM UserLanguage WHERE userId = ?");
            preparedStatement.setInt(1, userId);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                languageCode = result.getString("languageCode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return languageCode;
    }

    public boolean putUserLanguage(Integer userId, String language) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("REPLACE INTO UserLanguage (userId, languageCode) VALUES(?, ?)");
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, language);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }
    
    public int getCoinState(Integer userId, Long chatId) {
        int state = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT state FROM CoinState WHERE userId = ? AND chatId = ?");
            preparedStatement.setInt(1, userId);
            preparedStatement.setLong(2, chatId);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                state = result.getInt("state");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return state;
    }

    public boolean insertCoinState(Integer userId, Long chatId, int state) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("REPLACE INTO CoinState (userId, chatId, state) VALUES (?, ?, ?)");
            preparedStatement.setInt(1, userId);
            preparedStatement.setLong(2, chatId);
            preparedStatement.setInt(3, state);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }

    public Integer getRecentCoinId(Integer userId, String coin) {
        Integer coinId = -1;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("select coinId FROM RecentCoin WHERE userId=? AND coinName=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, coin);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                coinId = result.getInt("cityId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coinId;
    }
    
    public String[] getUserCoinOptions(Integer userId) {
        String[] options = new String[] {"en", "USD"};
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT * FROM UserCoinOptions WHERE userId = ?");
            preparedStatement.setInt(1, userId);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                options[0] = result.getString("languageCode");
                options[1] = result.getString("units");
            } else {
                addNewUserWeatherOptions(userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return options;
    }

    private boolean addNewUserWeatherOptions(Integer userId) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("REPLACE INTO UserCoinOptions(userId) VALUES (?)");
            preparedStatement.setInt(1, userId);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }

    public boolean putUserWeatherLanguageOption(Integer userId, String language) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("UPDATE UserCoinOptions SET languageCode = ? WHERE userId = ?");
            preparedStatement.setString(1, language);
            preparedStatement.setInt(2, userId);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }

    public boolean putUserWeatherUnitsOption(Integer userId, String currency) {
        int updatedRows = 0;
        try {
            final PreparedStatement preparedStatement = connetion.getPreparedStatement("UPDATE UserCoinrOptions SET currency = ? WHERE userId = ?");
            preparedStatement.setString(1, currency);
            preparedStatement.setInt(2, userId);
            updatedRows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRows > 0;
    }
}
