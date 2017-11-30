package org.rcsb.genomemapping.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.Document;
import org.rcsb.genomemapping.controllers.CoordinatesController;
import org.rcsb.genomemapping.utils.DBUtils;
import org.rcsb.mojave.genomemapping.constants.FieldNames;
import org.rcsb.mojave.genomemapping.constants.MongoCollections;
import org.rcsb.mojave.genomemapping.mappers.EntityToIsoform;
import org.rcsb.mojave.genomemapping.mappers.GeneToUniProt;
import org.rcsb.mojave.genomemapping.models.Position;
import org.rcsb.mojave.util.CommonConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
public class CoordinatesDaoMongoImpl implements CoordinatesDao {

    private List<Document> query;
    private AggregateIterable<Document> output;
    private MongoCollection<Document> collection;
    private ObjectMapper mapper;

    public CoordinatesDaoMongoImpl() {
        mapper = new ObjectMapper();
    }

    @Override
    public List<Position> getResidueByGeneticPosition(int taxonomyId, String chromosome, int position) throws Exception {

        collection = DBUtils.getMongoCollection(MongoCollections.COLL_MAPPING_TRANSCRIPTS_TO_ISOFORMS + "_" + taxonomyId);
        query = Arrays.asList(
                new Document("$match", new Document("$or", Arrays.asList(
                        new Document("$and", Arrays.asList(
                                new Document(FieldNames.COL_CHROMOSOME, new Document("$eq", chromosome))
                                , new Document(FieldNames.COL_ORIENTATION, new Document("$eq", "+"))
                                , new Document(FieldNames.COL_ISOFORMS+"."+ FieldNames.COL_CODING_COORDINATES+"."+ CommonConstants.COL_START, new Document("$gte", position))
                                , new Document(FieldNames.COL_ISOFORMS+"."+ FieldNames.COL_CODING_COORDINATES+"."+ CommonConstants.COL_END, new Document("$lte", position))))
                        , new Document("$and", Arrays.asList(
                                new Document(FieldNames.COL_CHROMOSOME, new Document("$eq", chromosome))
                                , new Document(FieldNames.COL_ORIENTATION, new Document("$eq", "-"))
                                , new Document(FieldNames.COL_ISOFORMS+"."+ FieldNames.COL_CODING_COORDINATES+"."+ CommonConstants.COL_END, new Document("$gte", position))
                                , new Document(FieldNames.COL_ISOFORMS+"."+ FieldNames.COL_CODING_COORDINATES+"."+ CommonConstants.COL_START, new Document("$lte", position)))))))
        );

        output = collection.aggregate(query);
        List<GeneToUniProt> isoformsFound = new ArrayList<>();
        for (Document document : output) {
            isoformsFound.add(mapper.convertValue(document, GeneToUniProt.class));
        }

        List<Position> results = new ArrayList<>();
        List<Position> isoformPositions = CoordinatesController.mapGeneticPositionToSequence(isoformsFound, position);

        for (Position ip : isoformPositions) {

            collection = DBUtils.getMongoCollection(MongoCollections.COLL_MAPPING_ENTITIES_TO_ISOFORMS + "_" + taxonomyId);
            query = Arrays.asList(
                    new Document("$match", new Document("$and", Arrays.asList(
                            new Document(FieldNames.COL_MOLECULE_ID, new Document("$eq", ip.getIsoformPosition().getMoleculeId()))
                            , new Document(FieldNames.COL_ISOFORM_COORDINATES, new Document("$elemMatch"
                                    , new Document("$and", Arrays.asList(
                                    new Document(CommonConstants.COL_START, new Document("$lte", ip.getIsoformPosition().getCoordinate()))
                                    , new Document(CommonConstants.COL_END, new Document("$gte", ip.getIsoformPosition().getCoordinate())))))))))
            );
            output = collection.aggregate(query);

            List<EntityToIsoform> entitiesFound = new ArrayList<>();
            for (Document document : output) {
                entitiesFound.add(mapper.convertValue(document, EntityToIsoform.class));
            }

            List<Position> structPositions = CoordinatesController.mapSequencePositionToStructure(entitiesFound
                    , ip.getIsoformPosition().getCoordinate());
            
            for (Position sp : structPositions) {
                Position clone = (Position) BeanUtils.cloneBean(ip);
                clone.setStructurePosition(sp.getStructurePosition());
                results.add(clone);
            }
        }
        return results;
    }
}
