package org.rcsb.genomemapping.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.spark.MongoSpark;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SaveMode;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
public class DBUtils {

    private static final Logger logger = LoggerFactory.getLogger(DBUtils.class);

    private static final Properties props = Parameters.getProperties();
    public static String MONGO_DB_IP = props.getProperty("mongodb.ip");
    public static String MONGO_DB_NAME = props.getProperty("mongodb.db.name");

    private static HashMap<String, String> mongoDBOptions;

    public static HashMap<String, String> getMongoDBOptions() {

        if(mongoDBOptions != null)
            return mongoDBOptions;

        mongoDBOptions = new HashMap<>();
        mongoDBOptions.put("spark.mongodb.input.uri","mongodb://"+MONGO_DB_IP+"/"+MONGO_DB_NAME);
        mongoDBOptions.put("spark.mongodb.output.uri","mongodb://"+MONGO_DB_IP+"/"+MONGO_DB_NAME);

        return mongoDBOptions;
    }

    public static MongoCollection<Document> getMongoCollection(String collectionName) {

        MongoClient mongoClient = new MongoClient(MONGO_DB_IP);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DB_NAME);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        return collection;
    }

    public static boolean collectionExists(String collectionName) {

        MongoClient client = new MongoClient(MONGO_DB_IP);
        return client.getDatabase(MONGO_DB_NAME).listCollectionNames()
                .into(new ArrayList<>()).contains(collectionName);
    }

    public static void dropCollection(String collectionName) {
        MongoClient mongoClient = new MongoClient(MONGO_DB_IP);
        DB db = mongoClient.getDB(MONGO_DB_NAME);
        DBCollection collection = db.getCollection(collectionName);
        collection.drop();
    }

    public static <T> void writeListToMongoDB(List<T> list, String collectionName) throws Exception {

        int bulkSize = 10000;
        int count = 0;

        MongoClient mongoClient = new MongoClient(MONGO_DB_IP);
        DB db = mongoClient.getDB(MONGO_DB_NAME);
        DBCollection collection = db.getCollection(collectionName);

        ObjectMapper mapper = new ObjectMapper();

        BulkWriteOperation bulkOperation;
        try {
            bulkOperation = collection.initializeUnorderedBulkOperation();

            for (T object : list) {

                DBObject dbo = mapper.convertValue(object, BasicDBObject.class);

                bulkOperation.insert(dbo);
                count++;

                if (count >= bulkSize) {
                    //time to perform the bulk insert
                    bulkOperation.execute();
                    count = 0;
                    bulkOperation = collection.initializeUnorderedBulkOperation();
                }
            }
            //finish up the last few
            if (count > 0) {
                bulkOperation.execute();
            }

        } catch (RuntimeException e) {
            throw e;
        }
    }

    public static void writeDatasetToMongoDB(Dataset<?> dataset, String collectionName, SaveMode saveMode) {

        if(StringUtils.isNotBlank(collectionName)) {

            logger.info("Starting save data to mongo collection " + collectionName);
            long timeS = System.currentTimeMillis();

            Map<String, String> mongoOptions = new HashMap<String, String>();
            try {
                mongoOptions.put("spark.mongodb.input.uri", "mongodb://" + MONGO_DB_IP + "/" + MONGO_DB_NAME);
                mongoOptions.put("spark.mongodb.output.uri", "mongodb://" + MONGO_DB_IP + "/" + MONGO_DB_NAME);
                mongoOptions.put("spark.mongodb.output.collection", collectionName);

                MongoSpark.write(dataset)
                        .options(mongoOptions)
                        .mode(saveMode).save();

            } catch (Exception e) {
                logger.error("Exception occurred running saveToMongo(). Message: " + e.getMessage());
                logger.debug("Cause: ", e);
                throw e;
            }

            long timeE = System.currentTimeMillis();
            logger.info("Completed save data to mongo collection " + collectionName + ". Time taken: " + DurationFormatUtils.formatPeriod(timeS, timeE, "HH:mm:ss:SS"));
        }else{
            logger.error("Cannot save to null or empty collection name");
        }
    }
}