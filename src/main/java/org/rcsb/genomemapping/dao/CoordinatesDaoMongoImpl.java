package org.rcsb.genomemapping.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.Document;
import org.rcsb.genomemapping.constants.MongoCollections;
import org.rcsb.genomemapping.constants.NamesConstants;
import org.rcsb.genomemapping.controller.CoordinatesController;
import org.rcsb.genomemapping.utils.AppHelper;
import org.rcsb.genomemapping.utils.DBUtils;
import org.rcsb.mojave.genomemapping.PositionPropertyMap;
import org.rcsb.mojave.genomemapping.SequenceToStructureFeaturesMap;
import org.rcsb.mojave.genomemapping.TranscriptToSequenceFeaturesMap;
import org.rcsb.mojave.util.CommonConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
public class CoordinatesDaoMongoImpl implements CoordinatesDao {

    private ObjectMapper mapper;

    public CoordinatesDaoMongoImpl() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    }

    @Override
    public List<PositionPropertyMap> mapGeneticPosition(int taxonomyId, String chromosome, int position, boolean canonical) throws Exception {

        List<PositionPropertyMap> results = new ArrayList<>();

        MongoCollection<Document> collection1 = DBUtils.getMongoCollection(MongoCollections.COLL_MAPPING_TRANSCRIPTS_TO_ISOFORMS + "_" + taxonomyId);
        List<Document> query1 = Arrays.asList(
                new Document("$match", new Document("$and", Arrays.asList(
                                  new Document(NamesConstants.COL_CHROMOSOME, new Document("$eq", chromosome))
                                , new Document(NamesConstants.COL_COORDINATES_MAPPING+"."+ CommonConstants.COL_START+"."+ NamesConstants.COL_GENETIC_POSITION, new Document("$lte", position))
                                , new Document(NamesConstants.COL_COORDINATES_MAPPING+"."+ CommonConstants.COL_END+"."+ NamesConstants.COL_GENETIC_POSITION, new Document("$gte", position))))));

        AggregateIterable<Document> output1 = collection1.aggregate(query1);
        List<TranscriptToSequenceFeaturesMap> found1 = new ArrayList<>();
        for (Document document : output1) {
            found1.add(mapper.convertValue(document, TranscriptToSequenceFeaturesMap.class));
        }

        List<PositionPropertyMap> results1 = CoordinatesController.mapGeneticPositionToSequence(found1, position);

        for (PositionPropertyMap position1 : results1) {

            MongoCollection<Document> collection2 = DBUtils.getMongoCollection(MongoCollections.COLL_MAPPING_ENTITIES_TO_ISOFORMS + "_" + taxonomyId);
            List<Document> query2 = Arrays.asList(
                    new Document("$match", new Document("$and", Arrays.asList(
                          new Document(NamesConstants.COL_MOLECULE_ID, new Document("$eq", position1.getMoleculeId()))
                        , new Document(NamesConstants.COL_COORDINATES_MAPPING+"."+ CommonConstants.COL_START+"."+ NamesConstants.COL_UNIPROT_POSITION, new Document("$lte", position1.getCoordinates().getUniProtPosition()))
                        , new Document(NamesConstants.COL_COORDINATES_MAPPING+"."+ CommonConstants.COL_END+"."+ NamesConstants.COL_UNIPROT_POSITION, new Document("$gte", position1.getCoordinates().getUniProtPosition()))))));

            AggregateIterable<Document> output2 = collection2.aggregate(query2);

            List<SequenceToStructureFeaturesMap> found2 = new ArrayList<>();
            for (Document document : output2) {
                found2.add(mapper.convertValue(document, SequenceToStructureFeaturesMap.class));
            }

            List<PositionPropertyMap> results2 = CoordinatesController.mapSequencePositionToStructure(found2, position1.getCoordinates().getUniProtPosition());
            for (PositionPropertyMap position2 : results2) {
                PositionPropertyMap clone = (PositionPropertyMap) BeanUtils.cloneBean(position1);
                AppHelper.nullAwareBeanCopy(clone, position2);
                results.add(clone);
            }
        }
        return results;
    }
}