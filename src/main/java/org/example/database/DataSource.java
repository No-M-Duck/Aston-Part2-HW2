package org.example.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.Logger;
import org.example.LoggerUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private static final Logger logger = LoggerUtil.getLogger(DataSource.class);
    private final HikariDataSource dataSource;
    private final HikariConfig config = new HikariConfig("datasource.properties");

    public DataSource() {
        this.config = config;
        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void reinitialize(HikariConfig newConfig) {
        logger.info("Reinitializing connection pool with new config");
        dataSource.close();
        dataSource.setUsername(newConfig.getUsername());
        dataSource.setPassword(newConfig.getPassword());
        dataSource.setJdbcUrl(newConfig.getJdbcUrl());
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("Connection pool closed successfully.");
        }
    }
}