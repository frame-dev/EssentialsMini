package ch.framedev.essentialsmini.database;

import ch.framedev.essentialsmini.main.Main;
import org.apache.log4j.Level;

import java.sql.*;

public class SQL {

    public static void createTable(String tableName, String... columns) {
        String columnDefinition = String.join(",", columns);
        String sql;

        if (Main.getInstance().isMysql()) {
            sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnDefinition + ", Numbers INT AUTO_INCREMENT PRIMARY KEY, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
        } else {
            sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + columnDefinition + ", created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
        }

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to execute statement", e);
        }
    }

    public static void insertData(String table, String[] data, String... columns) {
        // Join the column names with commas
        String columnNames = String.join(",", columns);
        // Create a string of placeholders (e.g., "?, ?")
        String placeholders = String.join(",", new String[columns.length]).replace("\0", "?");
        // Construct the SQL insert statement
        String sql = "INSERT INTO " + table + " (" + columnNames + ") VALUES (" + placeholders + ")";

        // Try-with-resources ensures that resources are closed automatically
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set the values for each placeholder
            for (int i = 0; i < data.length; i++) {
                stmt.setString(i + 1, data[i]);
            }
            // Execute the statement
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Log any SQL exceptions at the ERROR level
            Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to execute statement", e);
        }
    }



    public static void updateData(String table, String selected, String data, String whereClause, String... whereParams) {
        if (selected == null || selected.trim().isEmpty() || data == null) {
            throw new IllegalArgumentException("Selected column and data must not be null or empty");
        }

        String sql = "UPDATE " + table + " SET " + selected + " = ? WHERE " + whereClause;

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set the data to update
            stmt.setString(1, data);

            // Set the parameters for the WHERE clause
            for (int i = 0; i < whereParams.length; i++) {
                stmt.setString(i + 2, whereParams[i]);
            }

            // Execute the update
            stmt.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to execute update on table " + table + " with condition: " + whereClause, e);
        }
    }


    public static void deleteDataInTable(String table, String where) {
        String sql = "DELETE FROM " + table + " WHERE " + where;

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to execute statement", e);
        }
    }

    public static boolean exists(String table, String column, String data) {
        String sql = "SELECT 1 FROM " + table + " WHERE " + column + " = ? LIMIT 1";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, data);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to execute statement", e);
            return false;
        }
    }

    public static Object get(String table, String selected, String column, String data) {
        String sql = "SELECT " + selected + " FROM " + table + " WHERE " + column + " = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, data);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject(selected);
                }
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to execute statement", e);
        }
        return null;
    }


    public static void deleteTable(String table) {
        String sql = "DROP TABLE IF EXISTS " + table;

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to execute statement", e);
        }
    }

    public static boolean isTableExists(String table) {
        String sql = Main.getInstance().isMysql() ?
                "SHOW TABLES LIKE ?" :
                "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, table);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to execute statement", e);
            return false;
        }
    }

    public static <T> T get(String table, String selected, String column, String data, Class<T> type) {
        String sql = "SELECT " + selected + " FROM " + table + " WHERE " + column + " = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, data);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return type.cast(rs.getObject(selected));
                }
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to execute statement", e);
        }
        return null;
    }


    public static Connection getConnection() throws SQLException {
        return Main.getInstance().isMysql() ? MySQL.getConnection() : SQLite.connect();
    }
}