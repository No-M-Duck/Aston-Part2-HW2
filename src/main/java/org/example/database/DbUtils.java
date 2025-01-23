package org.example.database;

import org.apache.logging.log4j.Logger;
import org.example.LoggerUtil;

import java.sql.*;

public class DbUtils {

    private DbUtils(){
        throw new IllegalStateException("Utility class");
    }

    private static final Logger logger = LoggerUtil.getLogger(DbUtils.class);

    public static void start(){
        pgcrypto();
        if(!checkTables()){
            init();
        }
    }
    public static void startTest(boolean servlet){
        pgcrypto();
        init();
        if(servlet){populateInitialTestData();}
    }




    private static void pgcrypto() {
        try (Connection connection = DataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE EXTENSION IF NOT EXISTS pgcrypto;");
            logger.info("pgcrypto successfully installed in the database");
        } catch (SQLException e) {
            logger.error("Failed to download pgcrypto to the database{}", e.getMessage(), e);
        }
    }

    private static boolean checkTables() {
        try (Connection connection = DataSource.getConnection();
             Statement statement = connection.createStatement()) {

            logger.info("Check tables in database");

            // Выполняем первый запрос
            ResultSet directors = statement.executeQuery("SELECT EXISTS (" +
                    "   SELECT 1 " +
                    "   FROM pg_catalog.pg_tables" +
                    "   WHERE schemaname = 'public'" +
                    "       AND tablename = 'cinemas'" +
                    ");");

            // Проверяем результат первого запроса
            boolean cinemasExists = false;
            if (directors.next()) {
                cinemasExists = directors.getBoolean(1);
            }

            // Выполняем второй запрос
            ResultSet movies = statement.executeQuery("SELECT EXISTS (" +
                    "   SELECT 1 " +
                    "   FROM pg_catalog.pg_tables" +
                    "   WHERE schemaname = 'public'" +
                    "       AND tablename = 'sessions'" +
                    ");");

            // Проверяем результат второго запроса
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


    private static void init() {
        try (Connection connection = DataSource.getConnection();
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

    private static void populateInitialTestData() {
        String insertDirectorsSql = "INSERT INTO directors (id, name, last_name, country) VALUES " +
                "('550e8400-e29b-41d4-a716-446655440000', 'Steven', 'Spielberg', 'USA'), " +
                "('550e8400-e29b-41d4-a716-446655440001', 'Christopher', 'Nolan', 'UK') ";

        String insertMoviesSql = "INSERT INTO movies (id, director_id, title, release_date, duration, hall) VALUES " +
                "('660e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', 'Jurassic Park', '1993-06-11', 127, 1), " +
                "('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Inception', '2010-07-16', 148, 2)";

        try (Connection connection = DataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(insertDirectorsSql);
            statement.executeUpdate(insertMoviesSql);
            logger.info("Initial test data inserted successfully");
        } catch (SQLException e) {
            logger.error("Failed to insert initial test data: {}", e.getMessage(), e);
        }
    }
}
