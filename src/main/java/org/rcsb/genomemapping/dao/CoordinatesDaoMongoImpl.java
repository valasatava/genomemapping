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
import org.rcsb.genomemapping.model.Position;
import org.rcsb.genomemapping.utils.DBUtils;
import org.rcsb.mojave.genomemapping.GeneTranscriptToProteinSequence;
import org.rcsb.mojave.genomemapping.ProteinSequenceToProteinStructure;
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
    public List<Position> mapGeneticPosition(int taxonomyId, String chromosome, int position) throws Exception {

        MongoCollection<Document> collection1 = DBUtils.getMongoCollection(MongoCollections.COLL_MAPPING_TRANSCRIPTS_TO_ISOFORMS + "_" + taxonomyId);
        List<Document> query1 = Arrays.asList(
                new Document("$match", new Document("$and", Arrays.asList(
                                  new Document(NamesConstants.COL_CHROMOSOME, new Document("$eq", chromosome))
                                , new Document(NamesConstants.COL_COORDINATES_MAPPING+"."+ CommonConstants.COL_START+"."+ NamesConstants.COL_GENETIC_POSITION, new Document("$lte", position))
                                , new Document(NamesConstants.COL_COORDINATES_MAPPING+"."+ CommonConstants.COL_END+"."+ NamesConstants.COL_GENETIC_POSITION, new Document("$gte", position))))));

        AggregateIterable<Document> output1 = collection1.aggregate(query1);
        List<GeneTranscriptToProteinSequence> isoformsFound = new ArrayList<>();
        for (Document document : output1) {
            isoformsFound.add(mapper.convertValue(document, GeneTranscriptToProteinSequence.class));
        }

        List<Position> results = new ArrayList<>();
        List<Position> isoforms = CoordinatesController.mapGeneticPositionToSequence(isoformsFound, position);

        for (Position ip : isoforms) {

            MongoCollection<Document> collection2 = DBUtils.getMongoCollection(MongoCollections.COLL_MAPPING_ENTITIES_TO_ISOFORMS + "_" + taxonomyId);
            List<Document> query2 = Arrays.asList(
                    new Document("$match", new Document("$and", Arrays.asList(
                          new Document(NamesConstants.COL_MOLECULE_ID, new Document("$eq", ip.getIsoformPosition().getMoleculeId()))
                        , new Document(NamesConstants.COL_COORDINATES_MAPPING+"."+ CommonConstants.COL_START+"."+ NamesConstants.COL_UNIPROT_POSITION, new Document("$lte", ip.getIsoformPosition().getCoordinate()))
                        , new Document(NamesConstants.COL_COORDINATES_MAPPING+"."+ CommonConstants.COL_END+"."+ NamesConstants.COL_UNIPROT_POSITION, new Document("$gte", ip.getIsoformPosition().getCoordinate()))))));

            AggregateIterable<Document> output2 = collection2.aggregate(query2);

            List<ProteinSequenceToProteinStructure> entitiesFound = new ArrayList<>();
            for (Document document : output2) {
                entitiesFound.add(mapper.convertValue(document, ProteinSequenceToProteinStructure.class));
            }

            List<Position> entities = CoordinatesController.mapSequencePositionToStructure(entitiesFound
                    , ip.getIsoformPosition().getCoordinate());
            for (Position ep : entities) {
                Position clone = (Position) BeanUtils.cloneBean(ip);
                clone.setStructurePosition(ep.getStructurePosition());
                results.add(clone);
            }
        }
        return results;
    }
}