package me.derechtepilz.database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.*;

public class Database {

    private final Plugin plugin;
    private final Connection connection;

    private final HashMap<UUID, Double> balance = new HashMap<>();
    private final HashMap<UUID, Long> lastInterest = new HashMap<>();
    private final HashMap<UUID, Double> startBalance = new HashMap<>();

    public Database(Plugin plugin) {
        this.plugin = plugin;
        this.connection = initializeDatabase();
        if (connection == null) {
            throw new IllegalStateException("Database connection is null!");
        }
        loadEconomyData(connection);
    }

    private Connection initializeDatabase() {
        String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/EconomyPlugin Database.db";
        try {
            Connection connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);
            String sql = "CREATE TABLE IF NOT EXISTS bankAccounts(uuid text NOT NULL UNIQUE, balance real NOT NULL, lastInterest integer NOT NULL, startBalance real NOT NULL);";
            Statement statement = connection.createStatement();
            statement.execute(sql);
            connection.commit();
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadEconomyData(Connection connection) {
        String sql = "SELECT uuid, balance, lastInterest, startBalance FROM bankAccounts";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                balance.put(uuid, resultSet.getDouble("balance"));
                lastInterest.put(uuid, resultSet.getLong("lastInterest"));
                startBalance.put(uuid, resultSet.getDouble("startBalance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveEconomyData() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String updateBalanceSQL = "UPDATE bankAccounts SET balance = ? WHERE uuid = ?";
                String updateStartBalanceSQL = "UPDATE bankAccounts SET startBalance = ? WHERE uuid = ?";
                String updateLastInterestSQL = "UPDATE bankAccounts SET lastInterest = ? WHERE uuid = ?";

                PreparedStatement updateBalance = connection.prepareStatement(updateBalanceSQL);
                PreparedStatement updateStartBalance = connection.prepareStatement(updateStartBalanceSQL);
                PreparedStatement updateLastInterest = connection.prepareStatement(updateLastInterestSQL);
                for (UUID uuid : balance.keySet()) {
                    if (isPlayerRegistered(connection, uuid)) {
                        updateBalance.setDouble(1, balance.get(uuid));
                        updateBalance.setString(2, uuid.toString());

                        updateStartBalance.setDouble(1, startBalance.get(uuid));
                        updateStartBalance.setString(2, uuid.toString());

                        updateLastInterest.setDouble(1, lastInterest.get(uuid));
                        updateLastInterest.setString(2, uuid.toString());

                        updateBalance.addBatch();
                        updateStartBalance.addBatch();
                        updateLastInterest.addBatch();
                    } else {
                        registerPlayer(connection, uuid, balance.get(uuid), lastInterest.get(uuid), startBalance.get(uuid));
                    }
                }

                updateBalance.executeBatch();
                updateStartBalance.executeBatch();
                updateLastInterest.executeBatch();

                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void registerPlayer(Connection connection, UUID uuid, double balance, long lastInterest, double startBalance) {
        String sql = "INSERT into bankAccounts(uuid, balance, lastInterest, startBalance) VALUES(?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setDouble(2, balance);
            preparedStatement.setLong(3, lastInterest);
            preparedStatement.setDouble(4, startBalance);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerRegistered(Connection connection, UUID uuid) {
        String sql = "SELECT uuid FROM bankAccounts WHERE uuid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isPlayerRegistered = false;
            while (resultSet.next()) {
                isPlayerRegistered = true;
            }
            return isPlayerRegistered;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void registerPlayer(UUID uuid, double balance, long lastInterest, double startBalance) {
        this.balance.put(uuid, balance);
        this.startBalance.put(uuid, startBalance);
        this.lastInterest.put(uuid, lastInterest);
    }

    public boolean isPlayerRegistered(UUID uuid) {
        return balance.containsKey(uuid);
    }

    public void deletePlayer(Connection connection, UUID uuid) {
        String sql = "DELETE FROM bankAccounts WHERE uuid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBalance(UUID uuid, double balance) {
        this.balance.put(uuid, balance);
    }

    public void updateLastInterest(UUID uuid, long lastInterest) {
        this.lastInterest.put(uuid, lastInterest);
    }

    public void updateStartBalance(UUID uuid, double startBalance) {
        this.startBalance.put(uuid, startBalance);
    }

    public double getBalance(UUID uuid) {
        return balance.get(uuid);
    }

    public long getLastInterest(UUID uuid) {
        return lastInterest.get(uuid);
    }

    public double getStartBalance(UUID uuid) {
        return startBalance.get(uuid);
    }

    public Map<Double, UUID> getServerBalances() {
        Map<Double, UUID> serverBalances = new HashMap<>();
        for (UUID uuid : balance.keySet()) {
            serverBalances.put(balance.get(uuid), uuid);
        }
        return serverBalances;
    }

    public List<Double> getBalances() {
        List<Double> balances = new ArrayList<>();
        for (UUID uuid : balance.keySet()) {
            balances.add(balance.get(uuid));
        }
        return balances;
    }

    public Connection getConnection() {
        return connection;
    }

}
