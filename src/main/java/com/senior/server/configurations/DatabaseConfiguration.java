package com.senior.server.configurations;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {

    private AdminCredentialsConfiguration adminCredentialsConfiguration;
    private MongoDatabase mongoDatabase;

    @Autowired
    public DatabaseConfiguration(AdminCredentialsConfiguration adminCredentialsConfiguration) {
        this.adminCredentialsConfiguration = adminCredentialsConfiguration;
        MongoClient mongoClient = MongoClients.create(buildDatabaseURI());
        mongoDatabase = mongoClient.getDatabase(adminCredentialsConfiguration.getDatabaseName());
    }

    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }

    private String buildDatabaseURI(){
        return String.format(
                "mongodb+srv://%s:%s@seniorprojectdb.iqfpg.mongodb.net/%s?retryWrites=true&w=majority",
                adminCredentialsConfiguration.getAdminName(),
                adminCredentialsConfiguration.getAdminPassword(),
                adminCredentialsConfiguration.getDatabaseName()
        );
    }
}
