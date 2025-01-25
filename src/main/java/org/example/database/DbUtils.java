package org.example.database;

import org.apache.logging.log4j.Logger;
import org.example.LoggerUtil;

import java.sql.*;

public class DbUtils {

    private DbUtils(){
        throw new IllegalStateException("Utility class");
    }

    private static final Logger logger = LoggerUtil.getLogger(DbUtils.class);

    public static void start(DataSource dataSource){
        pgcrypto(dataSource);
        if(!checkTables(dataSource)){
            init(dataSource);
        }
    }
    private static void pgcrypto(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE EXTENSION IF NOT EXISTS pgcrypto;");
            logger.info("pgcrypto successfully installed in the database");
        } catch (SQLException e) {
            logger.error("Failed to download pgcrypto to the database{}", e.getMessage(), e);
        }
    }

    private static boolean checkTables(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            logger.info("Check tables in database");

            ResultSet directors = statement.executeQuery("SELECT EXISTS (" +
                    "   SELECT 1 " +
                    "   FROM pg_catalog.pg_tables" +
                    "   WHERE schemaname = 'public'" +
                    "       AND tablename = 'cinemas'" +
                    ");");

            boolean cinemasExists = false;
            if (directors.next()) {
                cinemasExists = directors.getBoolean(1);
            }

            ResultSet movies = statement.executeQuery("SELECT EXISTS (" +
                    "   SELECT 1 " +
                    "   FROM pg_catalog.pg_tables" +
                    "   WHERE schemaname = 'public'" +
                    "       AND tablename = 'sessions'" +
                    ");");

            boolean sessionsExists = false;
            if (movies.next()) {
                sessionsExists = movies.getBoolean(1);
            }

            return cinemasExists && sessionsExists;

        } catch (SQLException sqlException) {
            logger.error("Error checking tables: {}", sqlException.getMessage(), sqlException);
        }
        return false;
    }


    private static void init(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            logger.info("Initializing tables in the database");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS directors(" +
                    "id UUID PRIMARY KEY DEFAULT gen_random_uuid()," +
                    "name VARCHAR(255) NOT NULL," +
                    "last_name VARCHAR(255) NOT NULL," +
                    "country VARCHAR(255) NOT NULL" +
                    ")");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS movies(" +
                    "id UUID PRIMARY KEY DEFAULT gen_random_uuid()," +
                    "director_id UUID NOT NULL," +
                    "title VARCHAR(255) NOT NULL," +
                    "release_date DATE NOT NULL," +
                    "duration INT NOT NULL," +
                    "hall INT NOT NULL," +
                    "FOREIGN KEY (director_id) REFERENCES directors(id) ON DELETE CASCADE" +
                    ")");
        } catch (SQLException e) {
            logger.error("Failed to init tables to the database{}", e.getMessage(), e);
        }
    }
}
