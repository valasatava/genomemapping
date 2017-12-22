package org.rcsb.genomemapping.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
public class DBUtils {

    private static String MONGO_DB_IP = "132.249.213.154";
    private static String MONGO_DB_NAME = "dw_v1";

    public static MongoCollection<Document> getMongoCollection(String collectionName) {

        MongoClient mongoClient = new MongoClient(MONGO_DB_IP);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DB_NAME);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        return collection;
    }

    public static boolean collectionExists(String collectionName) {

        MongoClient client = new MongoClient(MONGO_DB_IP);
        return client.getDatabase(MONGO_DB_NAME).listCollectionNames()
                .into(new ArrayList<String>()).contains(collectionName);
    }
}
