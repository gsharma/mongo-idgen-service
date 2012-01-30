package com.github.mongoidgen.conf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.github.mongoidgen.service.Utils.StringUtils;

public class MongoFactoryBean extends AbstractFactoryBean<Mongo> {
    private List<ServerAddress> replicaSetSeeds = new ArrayList<ServerAddress>();
    private MongoOptions mongoOptions;
    private Configuration configuration;

    @Override
    public Class<?> getObjectType() {
        return Mongo.class;
    }

    protected DB createDB() throws Exception {
        DB db = createInstance().getDB(configuration.getDataStoreName());
        if (!StringUtils.isNullOrEmpty(configuration.getDataStoreUsername())
                && !StringUtils.isNullOrEmpty(configuration.getDataStorePassword())) {
            db.authenticate(configuration.getDataStoreUsername(), configuration.getDataStorePassword().toCharArray());
        }
        return db;
    }

    protected Mongo createInstance() throws Exception {
        if (mongoOptions == null) {
            mongoOptions = new MongoOptions();
            mongoOptions.safe = true;
        }
        if (replicaSetSeeds.size() > 0) {
            if (mongoOptions != null) {
                return new Mongo(replicaSetSeeds, mongoOptions);
            }
            return new Mongo(replicaSetSeeds);
        }
        return new Mongo();
    }

    public void setMultiAddress(String[] serverAddresses) {
        replSeeds(serverAddresses);
    }

    private void replSeeds(String... serverAddresses) {
        try {
            replicaSetSeeds.clear();
            for (String addr : serverAddresses) {
                String[] a = addr.split(":");
                String host = a[0];
                if (a.length > 2) {
                    throw new IllegalArgumentException("Invalid Server Address : " + addr);
                } else if (a.length == 2) {
                    replicaSetSeeds.add(new ServerAddress(host, Integer.parseInt(a[1])));
                } else {
                    replicaSetSeeds.add(new ServerAddress(host));
                }
            }
        } catch (Exception e) {
            throw new BeanCreationException("Error while creating replicaSetAddresses", e);
        }
    }

    public void setAddress(String serverAddress) {
        replSeeds(serverAddress);
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}