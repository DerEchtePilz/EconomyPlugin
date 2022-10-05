package me.derechtepilz.database;

import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.*;

public class Database {

    private final Plugin plugin;
    private final Connection connection;

    public Database(Plugin plugin) {
        this.plugin = plugin;
        this.connection = initializeDatabase();
    }

    private Connection initializeDatabase() {
        String url = "jdbc:sqlite:" + plugin.getServer().getWorldContainer().getAbsolutePath() + "/plugins/Economy/EconomyPlugin Database.db";
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

    public void updateBalance(Connection connection, UUID uuid, double balance) {
        String sql = "UPDATE bankAccounts SET balance = ? WHERE uuid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, balance);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getBalance(Connection connection, UUID uuid) {
        String sql = "SELECT balance FROM bankAccounts WHERE uuid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            double balance = 0;
            while (resultSet.next()) {
                balance = resultSet.getDouble("balance");
            }
            return balance;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<Double, UUID> getServerBalances(Connection connection) {
        String sql = "SELECT uuid, balance FROM bankAccounts";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            Map<Double, UUID> serverBalances = new HashMap<>();
            while (resultSet.next()) {
                serverBalances.put(resultSet.getDouble("balance"), UUID.fromString(resultSet.getString("uuid")));
            }
            return serverBalances;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public List<Double> getBalances(Connection connection) {
        String sql = "SELECT balance FROM bankAccounts";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            List<Double> balances = new ArrayList<>();
            while (resultSet.next()) {
                balances.add(resultSet.getDouble("balance"));
            }
            return balances;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void updateLastInterest(Connection connection, UUID uuid, long lastInterest) {
        String sql = "UPDATE bankAccounts SET lastInterest = ? WHERE uuid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, lastInterest);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getLastInterest(Connection connection, UUID uuid) {
        String sql = "SELECT lastInterest FROM bankAccounts WHERE uuid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            long lastInterest = 0;
            while (resultSet.next()) {
                lastInterest = resultSet.getLong("lastInterest");
            }
            return lastInterest;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateStartBalance(Connection connection, UUID uuid, double startBalance) {
        String sql = "UPDATE bankAccounts SET startBalance = ? WHERE uuid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, startBalance);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getStartBalance(Connection connection, UUID uuid) {
        String sql = "SELECT startBalance FROM bankAccounts WHERE uuid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            double startBalance = 0;
            while (resultSet.next()) {
                startBalance = resultSet.getDouble("startBalance");
            }
            return startBalance;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Connection getConnection() {
        return connection;
    }

}
