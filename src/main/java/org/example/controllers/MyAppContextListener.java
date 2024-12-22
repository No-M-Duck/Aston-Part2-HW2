package org.example.controllers;
import com.zaxxer.hikari.HikariConfig;
import org.example.database.DataSource;
import org.example.database.DbUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAppContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(MyAppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            logger.info("Initializing application context...");
            HikariConfig config = new HikariConfig("datasource.properties");
            DataSource.init(config);
            DbUtils.start();
            logger.info("Application context initialized successfully.");
        } catch (Exception e) {
            logger.error("Error initializing application context", e);
            throw new RuntimeException("Failed to initialize application context", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            logger.info("Destroying application context...");
            DataSource.close();
            logger.info("Application context destroyed successfully.");
        } catch (Exception e) {
            logger.error("Error destroying application context", e);
        }
    }
}
