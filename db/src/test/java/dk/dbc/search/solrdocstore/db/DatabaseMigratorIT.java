package dk.dbc.search.solrdocstore.db;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Before;
import org.postgresql.ds.PGSimpleDataSource;

import static org.junit.Assert.*;

public class DatabaseMigratorIT {

    private PGSimpleDataSource datasource;

    @Before
    public void resetDatabase() throws SQLException {
        datasource = getDataSource();
    }

    @Test
    public void onStartup() throws Exception {

        int version = -1;

        try (Connection connection = datasource.getConnection() ;
             Statement stmt = connection.createStatement()) {
            for (String sql : Arrays.asList("DROP SCHEMA public CASCADE",
                                            "CREATE SCHEMA public",
                                            "DROP TABLE IF EXISTS queueruledefault",
                                            "CREATE TABLE queueruledefault (queue TEXT NOT NULL)",
                                            "INSERT INTO queueruledefault(queue) VALUES('my-secret-queue')")) {
                stmt.executeUpdate(sql);
            }
        }
        HashSet<String> migrated = DatabaseMigrator.migrate(datasource, true);
        System.out.println("migrated = " + migrated);

        try (Connection connection = datasource.getConnection() ;
             Statement stmt = connection.createStatement() ;
             ResultSet resultSet = stmt.executeQuery("SELECT version FROM schema_version ORDER BY installed_rank DESC LIMIT 1")) {
            if (resultSet.next()) {
                version = resultSet.getInt(1);
                System.out.println("version = " + version);
            }
        }
        assertEquals(18, version);

        String queueRule = null;
        try (Connection connection = datasource.getConnection() ;
             Statement stmt = connection.createStatement() ;
             ResultSet resultSet = stmt.executeQuery("SELECT queue FROM queuerule WHERE queue='my-secret-queue'")) {
            if (resultSet.next()) {
                queueRule = resultSet.getString(1);
                System.out.println("resultSet = " + resultSet);
            }
        }
        assertEquals("my-secret-queue", queueRule);

        try (Connection connection = datasource.getConnection() ;
             Statement stmt = connection.createStatement()) {
            for (String sql : Arrays.asList("DROP TABLE IF EXISTS queueruledefault")) {
                stmt.executeUpdate(sql);
            }
        }
    }

    private static PGSimpleDataSource getDataSource() {
        PGSimpleDataSource datasource = new PGSimpleDataSource();

        datasource.setServerNames(new String[] {"localhost"});
        String postgresqlPort = System.getProperty("postgresql.port");
        if (postgresqlPort != null && postgresqlPort.length() > 1) {
            datasource.setDatabaseName("docstore");
            datasource.setPortNumbers(new int[] {Integer.parseInt(System.getProperty("postgresql.port", "5432"))});
        } else {
            datasource.setDatabaseName(System.getProperty("user.name"));
            datasource.setPortNumbers(new int[] {5432});

        }
        datasource.setUser(System.getProperty("user.name"));
        datasource.setPassword(System.getProperty("user.name"));
        return datasource;
    }

}
