package org.example.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.Logger;
import org.example.LoggerUtil;


import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private static final Logger logger = LoggerUtil.getLogger(DataSource.class);

    private static HikariDataSource ds;
    private static boolean initialized = false; // флаг, чтобы избежать повторной инициализации


    private DataSource() {
    }

    public static void init(HikariConfig config) {
        if (initialized) {
            throw new IllegalStateException("Connection pool is already initialized");
        }
        try {
            logger.info("Initializing the connection pool with provided configuration");
            ds = new HikariDataSource(config);
        } catch (Exception e) {
            logger.error("Error initializing connection pool: {}", e.getMessage(), e);
            throw new RuntimeException("Database connection pool initialization failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (ds == null) {
            throw new IllegalStateException("Connection pool is not initialized");
        }
        return ds.getConnection();
    }

    public static void setHikariConfig(HikariConfig newConfig) {
        logger.info("Reinitializing connection pool with new config");
        ds.close(); // Закрываем старый пул
        ds = new HikariDataSource(newConfig); // Создаем новый пул с новыми настройками
    }

    public static void close() {
        if (ds != null) {
            ds.close();
            logger.info("Connection pool closed successfully.");
        }
    }
}
