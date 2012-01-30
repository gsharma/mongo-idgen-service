package com.github.mongoidgen.service;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.github.mongoidgen.conf.Configuration;
import com.github.mongoidgen.service.Utils.StringUtils;

public class MongoIdGeneratorService implements IdGeneratorService<String> {
    private static final Logger logger = LoggerFactory.getLogger(MongoIdGeneratorService.class);

    private final ConcurrentLinkedQueue<String> idCache = new ConcurrentLinkedQueue<String>();
    private final ScheduledExecutorService cachePopulator = Executors.newScheduledThreadPool(1);
    private DBCollection sequencesCollection;
    private final Object sequenceMutex = new Object();
    private final static String ID_KEY = "ID";
    private volatile boolean shutdown;
    private Mongo mongo;
    private Configuration configuration;

    // this can absolutely not fail
    public String generate() {
        String jobId = idCache.poll();
        if (jobId != null) {
            return jobId;
        }
        // pay the price once every idCacheCapacity times
        if (logger.isDebugEnabled()) {
            logger.debug("Generating hot id");
        }
        populateIdCache(configuration.getIdCacheCapacity());
        return idCache.poll();
    }

    public boolean ping() {
        return !shutdown;
    }

    @Override
    public void start() {
        if (logger.isDebugEnabled()) {
            logger.debug("MongoIdGenerator Service is starting up");
        }

        // prep mongo
        // final Configuration configuration = (Configuration)
        // AppContext.getApplicationContext().getBean("configuration");
        // mongo = ((Mongo)
        // AppContext.getApplicationContext().getBean("mongo"));
        DB db = mongo.getDB(configuration.getDataStoreName());
        if (!StringUtils.isNullOrEmpty(configuration.getDataStoreUsername())) {
            if (!db.isAuthenticated()) {
                db.authenticate(configuration.getDataStoreUsername(), configuration.getDataStorePassword()
                        .toCharArray());
            }
        }

        sequencesCollection = db.getCollection("idgen_sequences");
        BasicDBObject key = new BasicDBObject();
        key.put(ID_KEY, 1);
        sequencesCollection.ensureIndex(key, null, true);
        if (sequencesCollection.find().count() == 0) {
            BasicDBObject root = new BasicDBObject();
            StringBuffer buffer = new StringBuffer(configuration.getIdRootPrefix());
            buffer.append(Long.toString(System.currentTimeMillis()).substring(2));
            root.put(ID_KEY, Long.parseLong(buffer.toString()));
            sequencesCollection.insert(root);
        }

        try {
            cachePopulator.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (logger.isDebugEnabled()) {
                        logger.debug("cachePopulator woke up, checking if jobIdCache needs a refill..");
                    }
                    populateIdCache(configuration.getIdCacheCapacity());
                }
            }, 0L, configuration.getIdCacheRejuvenatorFreqMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception exception) {
            String message = "Failed to cleanly startup MongoIdGenerator Service with " + configuration;
            logger.error(message, exception);
            throw new RuntimeException(message, exception);
        }
        shutdown = false;
        if (logger.isDebugEnabled()) {
            logger.debug("MongoIdGenerator Service started successfully");
        }
    }

    @Override
    public void stop() {
        if (logger.isDebugEnabled()) {
            logger.debug("MongoIdGenerator Service is shutting down");
        }
        try {
            cachePopulator.shutdown();
            try {
                // Wait a while for existing tasks to terminate
                if (!cachePopulator.awaitTermination(5L, TimeUnit.MILLISECONDS)) {
                    cachePopulator.shutdownNow();
                    if (!cachePopulator.awaitTermination(5L, TimeUnit.MILLISECONDS)) {
                        logger.error("cachePopulator did not terminate");
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (InterruptedException ie) {
                logger.error("Re-cancel if current thread also interrupted");
                cachePopulator.shutdownNow();
                Thread.currentThread().interrupt();
            }
        } catch (Exception exception) {
        } finally {
            if (!cachePopulator.isShutdown()) {
                logger.error("Failed to cleanly shutdown cachePopulator");
                Thread.currentThread().interrupt();
            } else {
                logger.debug("Successfully shutdown cachePopulator");
            }
        }
        shutdown = true;
        if (logger.isDebugEnabled()) {
            logger.debug("MongoIdGenerator Service shut down successfully");
        }
    }

    private void populateIdCache(int capacity) {
        int currentSize = idCache.size();
        int toFill = capacity - currentSize;
        if (toFill > 0) {
            while (toFill-- > 0) {
                idCache.add(generateId());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Rejuvenated jobIdCache, prevSize=" + currentSize + ", currentSize=" + idCache.size());
            }
        }
    }

    private String generateId() {
        synchronized (sequenceMutex) {
            BasicDBObject root = (BasicDBObject) sequencesCollection.findOne();
            BasicDBObject increment = new BasicDBObject();
            increment.put("$inc", new BasicDBObject(ID_KEY, 1));
            return ((Long) sequencesCollection.findAndModify(root, null, null, false, increment, true, false).get(
                    ID_KEY)).toString();
        }
    }

    public Mongo getMongo() {
        return mongo;
    }

    @Required
    public void setMongo(Mongo mongo) {
        this.mongo = mongo;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Required
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}