package com.gl.ceir.config.configuration;

import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Repository;

@Repository
public class ConnectionConfiguration {
    @PersistenceContext
    private EntityManager em;

    private final Logger log = LogManager.getLogger(com.gl.ceir.config.configuration.ConnectionConfiguration.class);

    public Connection getConnection() {
        try {
            EntityManagerFactoryInfo info = (EntityManagerFactoryInfo)this.em.getEntityManagerFactory();
            return info.getDataSource().getConnection();
        } catch (SQLException e) {
            this.log.error("Error " + e + " :: " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }
}
