package org.example.repo.configurations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Configuration;
import org.example.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ConfigurationRepositoryDB implements ConfigurationRepository{
    private static final Logger logger = LogManager.getLogger();
    //private HibernateUtils hibernateUtils;

    public ConfigurationRepositoryDB() {}

    public SessionFactory getSession() {
        logger.traceEntry();
        try {
            if (sessionFactory == null || sessionFactory.isClosed())
                sessionFactory = getNewSession();
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(sessionFactory);
        return sessionFactory;
    }

    public SessionFactory getNewSession() {
        SessionFactory ses = null;
        try {
            final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure() // configures settings from hibernate.cfg.xml
                    .build();
            ses = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error getting connection " + e);
        }
        return ses;
    }

    private static SessionFactory sessionFactory;

    public void initialize() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Exception " + e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Override
    public List<Configuration> getAll() {
        logger.traceEntry("getting all the configurations");
        SessionFactory ses = getSession();
        try (Session session = ses.openSession()) {
            Transaction transact = null;
            try {
                transact = session.beginTransaction();
                List<Configuration> configs = session.createQuery("FROM Configuration ", Configuration.class).list();
                System.out.println(configs.size() + " configurations");
                transact.commit();
                return configs;
            } catch (Exception e) {
                System.err.println("Error when getting all the configurations " + e);
                if (transact != null) {
                    transact.rollback();
                }
            }
        }
        return null;
    }

    @Override
    public boolean delete(Configuration entity) throws IOException {
        return false;
    }

    @Override
    public Configuration update(Configuration entity) throws IOException {
        return null;
    }

    @Override
    public Configuration save(Configuration entity) throws IOException {
        SessionFactory ses = getSession();
        logger.traceEntry("saving configurations {}", entity);
        try (Session session = ses.openSession()) {
            Transaction transact = null;
            try {
                transact = session.beginTransaction();
                session.save(entity);
                transact.commit();
            } catch (Exception e) {
                System.err.println("Error when saving the configuration " + e);
                if (transact != null) {
                    transact.rollback();
                }
            }
        }
        return entity;
    }

    @Override
    public int size() {
        return 0;
    }
}
