package org.rcsb.genomemapping.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.rcsb.genomemapping.constants.MongoCollections;
import org.rcsb.genomemapping.constants.NamesConstants;
import org.rcsb.genomemapping.utils.DBUtils;
import org.rcsb.mojave.genomemapping.MultipleFeaturesMap;
import org.rcsb.mojave.genomemapping.PositionPropertyMap;
import org.rcsb.mojave.util.CommonConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public class StructuresDaoMongoImpl implements StructuresDao {

    private ObjectMapper mapper;
    public StructuresDaoMongoImpl() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    }

    @Override
    public List<MultipleFeaturesMap> getStructuresByGeneName(int taxonomyId, String geneName, boolean canonical) {

        MongoCollection<Document> collection = DBUtils.getMongoCollection(MongoCollections.VIEW_ON_EXONS_IN_3D + "_" + taxonomyId);

        List<Document> query;
        if (canonical)
            query = Arrays.asList(new Document("$match", new Document("$and", Arrays.asList(
                      new Document(NamesConstants.COL_CANONICAL, new Document("$eq", true))
                    , new Document(NamesConstants.COL_GENE_NAME, new Document("$eq", geneName))))));
        else
            query = Arrays.asList(new Document("$match", new Document(NamesConstants.COL_GENE_NAME, new Document("$eq", geneName))));

        AggregateIterable<Document> output = collection.aggregate(query);
        List<MultipleFeaturesMap> found = new ArrayList<>();
        for (Document document : output) {
            found.add(mapper.convertValue(document, MultipleFeaturesMap.class));
        }

        return found;
    }

    @Override
    public List<MultipleFeaturesMap> getStructuresByGeneId(int taxonomyId, String geneId, boolean canonical) {

        MongoCollection<Document> collection = DBUtils.getMongoCollection(MongoCollections.VIEW_ON_EXONS_IN_3D + "_" + taxonomyId);

        List<Document> query;
        if (canonical)
            query = Arrays.asList(new Document("$match", new Document("$and", Arrays.asList(
                      new Document(NamesConstants.COL_CANONICAL, new Document("$eq", true))
                    , new Document(NamesConstants.COL_GENE_ID, new Document("$eq", geneId))))));
        else
            query = Arrays.asList(new Document("$match", new Document(NamesConstants.COL_GENE_ID, new Document("$eq", geneId))));

        AggregateIterable<Document> output = collection.aggregate(query);
        List<MultipleFeaturesMap> found = new ArrayList<>();
        for (Document document : output) {
            found.add(mapper.convertValue(document, MultipleFeaturesMap.class));
        }

        return found;
    }

    @Override
    public List<PositionPropertyMap> getStructuresByGeneticPosition(int taxonomyId, String chromosome, int position, boolean canonical) {

        MongoCollection<Document> collection = DBUtils.getMongoCollection(MongoCollections.VIEW_ON_EXONS_IN_3D + "_" + taxonomyId);
        List<Document> query = Arrays.asList(
                new Document("$match", new Document("$and", Arrays.asList(
                          new Document(NamesConstants.COL_CHROMOSOME, new Document("$eq", chromosome))
                        , new Document(NamesConstants.COL_COORDINATES_MAPPING+"."+ CommonConstants.COL_START+"."+ NamesConstants.COL_GENETIC_POSITION, new Document("$lte", position))
                        , new Document(NamesConstants.COL_COORDINATES_MAPPING+"."+ CommonConstants.COL_END+"."+ NamesConstants.COL_GENETIC_POSITION, new Document("$gte", position))))));

        AggregateIterable<Document> output = collection.aggregate(query);
        List<PositionPropertyMap> found = new ArrayList<>();
        for (Document document : output) {
            found.add(mapper.convertValue(document, PositionPropertyMap.class));
        }

        return found;
    }
}