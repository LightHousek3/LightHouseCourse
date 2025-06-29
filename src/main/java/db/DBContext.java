package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility class to manage database connections.
 * @author DangPH - CE180896
 */
public class DBContext {

    // Database connection parameters
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=LightHouseCourse;encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8";
    private static final String USER = "sa";
    private static final String PASSWORD = "1234";

    // Static block to load the driver
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading SQL Server JDBC driver: " + e.getMessage());
        }
    }

    /**
     * Get a database connection.
     *
     * @return A Connection object to the database
     * @throws SQLException If a database error occurs
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        return conn;
    }

    /**
     * Close a connection, ignoring any errors.
     *
     * @param rs The ResultSet to close
     * @param ps The PreparedStatement to close
     * @param conn The connection to close
     */
    public static void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing resource: " + e.getMessage());
        }
    }

}
