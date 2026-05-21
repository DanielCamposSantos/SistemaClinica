package io.github.danielcampossantos.conn;

import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = "jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:6543/postgres";
    private static final String USERNAME = dotenv.get("USERNAME_DB");
    private static final String PASSWORD = dotenv.get("PASSWORD_DB");

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static JdbcRowSet getJdbcRowSet() throws SQLException {
        try (JdbcRowSet jdbcRowSet = RowSetProvider.newFactory().createJdbcRowSet()) {

            jdbcRowSet.setUrl(URL);
            jdbcRowSet.setUsername(USERNAME);
            jdbcRowSet.setPassword(PASSWORD);
            return jdbcRowSet;
        }
    }
}
