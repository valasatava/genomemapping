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

    public List<PositionPropertyMap> mapGenomicPosition(int taxonomyId, String chromosome, int position, boolean canonical) throws Exception {

        List<PositionPropertyMap> results = new ArrayList<>();

        // MAP TO PROTEIN SEQUENCES
        MongoCollection<Document> collection1 = DBUtils.getMongoCollection(MongoCollections.COLL_MAPPING_TRANSCRIPTS_TO_ISOFORMS + "_" + taxonomyId);
        List<Document> query1 = Arrays.asList(
                    new Document("$match", new Document("$or", Arrays.asList(
                            new Document("$and", Arrays.asList(
                                      new Document(NamesConstants.COL_CHROMOSOME, new Document("$eq", chromosome))
                                    , new Document(NamesConstants.COL_CANONICAL, new Document("$eq", canonical))
                                    , new Document(NamesConstants.COL_ORIENTATION, new Document("$eq", "+"))
                                    , new Document(NamesConstants.COL_COORDINATES + "." + CommonConstants.COL_START + "." + NamesConstants.COL_GENOMIC_POSITION, new Document("$lte", position))
                                    , new Document(NamesConstants.COL_COORDINATES + "." + CommonConstants.COL_END + "." + NamesConstants.COL_GENOMIC_POSITION, new Document("$gte", position))))
                            , new Document("$and", Arrays.asList(
                                      new Document(NamesConstants.COL_CHROMOSOME, new Document("$eq", chromosome))
                                    , new Document(NamesConstants.COL_CANONICAL, new Document("$eq", canonical))
                                    , new Document(NamesConstants.COL_ORIENTATION, new Document("$eq", "-"))
                                    , new Document(NamesConstants.COL_COORDINATES + "." + CommonConstants.COL_END + "." + NamesConstants.COL_GENOMIC_POSITION, new Document("$lte", position))
                                    , new Document(NamesConstants.COL_COORDINATES + "." + CommonConstants.COL_START + "." + NamesConstants.COL_GENOMIC_POSITION, new Document("$gte", position))))
                    ))));

        AggregateIterable<Document> output1 = collection1.aggregate(query1);
        List<TranscriptToSequenceFeaturesMap> found1 = new ArrayList<>();
        for (Document document : output1) {
            found1.add(mapper.convertValue(document, TranscriptToSequenceFeaturesMap.class));
        }

        if (found1.size() == 0)
            return  results;

        List<PositionPropertyMap> results1 = CoordinatesController.mapGeneticPositionToSequence(found1, position);

        // MAP TO PROTEIN STRUCTURES
        MongoCollection<Document> collection2 = DBUtils.getMongoCollection(MongoCollections.COLL_MAPPING_ENTITIES_TO_ISOFORMS);
        for (PositionPropertyMap position1 : results1) {

            List<Document> query2 = Arrays.asList(
                    new Document("$match", new Document("$and", Arrays.asList(
                          new Document(NamesConstants.COL_MOLECULE_ID, new Document("$eq", position1.getMoleculeId()))
                        , new Document(NamesConstants.COL_COORDINATES +"."+ CommonConstants.COL_START+"."+ NamesConstants.COL_UNIPROT_POSITION, new Document("$lte", position1.getCoordinate().getUniProtPosition()))
                        , new Document(NamesConstants.COL_COORDINATES +"."+ CommonConstants.COL_END+"."+ NamesConstants.COL_UNIPROT_POSITION, new Document("$gte", position1.getCoordinate().getUniProtPosition()))))));
            AggregateIterable<Document> output2 = collection2.aggregate(query2);

            List<SequenceToStructureFeaturesMap> found2 = new ArrayList<>();
            for (Document document : output2)
                found2.add(mapper.convertValue(document, SequenceToStructureFeaturesMap.class));

            if (found2.size() == 0)
                return results1;

            List<PositionPropertyMap> results2 = CoordinatesController.mapSequencePositionToStructure(found2, position1.getCoordinate().getUniProtPosition());
            for (PositionPropertyMap position2 : results2) {

                PositionPropertyMap clone = new PositionPropertyMap();
                AppHelper.nullAwareBeanCopy(clone, position1);
                clone.setEntryId(position2.getEntryId());
                clone.setEntityId(position2.getEntityId());
                clone.setChainId(position2.getChainId());
                clone.getCoordinate().setPdbSeqPosition(position2.getCoordinate().getPdbSeqPosition());

                results.add(clone);
            }
        }
        return results;
    }

    @Override
    public List<PositionPropertyMap> mapPdbSeqPosition(String entryId, String entityId, int position, boolean canonical) throws Exception {

        List<PositionPropertyMap> results = new ArrayList<>();

        // GET INFORMATION ABOUT THE ORGANISM
        MongoCollection<Document> collection = DBUtils.getMongoCollection(MongoCollections.COLL_ENTITY);
        List<Document> query = Arrays.asList(
                new Document("$match", new Document("$and", Arrays.asList(
                          new Document(NamesConstants.COL_ENTRY_ID, new Document("$eq", entryId))
                        , new Document(NamesConstants.COL_ENTITY_ID, new Document("$eq", entityId))))));
        AggregateIterable<Document> entity = collection.aggregate(query);

        List<String> taxonomyIds = new ArrayList<>();
        for (Document document : entity) {
            List<Document> organisms = (ArrayList<Document>) document.get(CommonConstants.COL_ORGANISM);
            for (Document organism : organisms)
                taxonomyIds.add(organism.getString(CommonConstants.COL_TAXONOMY_ID));
        }

        for (String taxonomyId : taxonomyIds) {
            // MAP TO PROTEIN STRUCTURES
            MongoCollection<Document> collection1 = DBUtils.getMongoCollection(MongoCollections.COLL_MAPPING_ENTITIES_TO_ISOFORMS);
            List<Document> query1 = Arrays.asList(
                    new Document("$match", new Document("$and", Arrays.asList(
                              new Document(NamesConstants.COL_ENTRY_ID, new Document("$eq", entryId))
                            , new Document(NamesConstants.COL_ENTITY_ID, new Document("$eq", entityId))
                            , new Document(NamesConstants.COL_COORDINATES + "." + CommonConstants.COL_START + "." + NamesConstants.COL_PDBSEQ_POSITION, new Document("$lte", position))
                            , new Document(NamesConstants.COL_COORDINATES + "." + CommonConstants.COL_END + "." + NamesConstants.COL_PDBSEQ_POSITION, new Document("$gte", position))))));
            AggregateIterable<Document> output1 = collection1.aggregate(query1);

            List<SequenceToStructureFeaturesMap> found1 = new ArrayList<>();
            for (Document document : output1)
                found1.add(mapper.convertValue(document, SequenceToStructureFeaturesMap.class));

            if (found1.size() == 0)
                return results;

            List<PositionPropertyMap> results1 = CoordinatesController.mapStructurePositionToSequence(found1, position);
            if (!DBUtils.collectionExists(MongoCollections.COLL_MAPPING_TRANSCRIPTS_TO_ISOFORMS + "_" + taxonomyId))
                return results1;

            // MAP TO GENOMIC COORDINATES
            MongoCollection<Document> collection2 = DBUtils.getMongoCollection(MongoCollections.COLL_MAPPING_TRANSCRIPTS_TO_ISOFORMS + "_" + taxonomyId);
            for (PositionPropertyMap position1 : results1) {

                List<Document> query2 = Arrays.asList(
                        new Document("$match", new Document("$and", Arrays.asList(
                                  new Document(NamesConstants.COL_MOLECULE_ID, new Document("$eq", position1.getMoleculeId()))
                                , new Document(NamesConstants.COL_CANONICAL, new Document("$eq", canonical))
                                , new Document(NamesConstants.COL_COORDINATES + "." + CommonConstants.COL_START + "." + NamesConstants.COL_UNIPROT_POSITION, new Document("$lte", position1.getCoordinate().getUniProtPosition()))
                                , new Document(NamesConstants.COL_COORDINATES + "." + CommonConstants.COL_END + "." + NamesConstants.COL_UNIPROT_POSITION, new Document("$gte", position1.getCoordinate().getUniProtPosition()))))));

                AggregateIterable<Document> output2 = collection2.aggregate(query2);
                List<TranscriptToSequenceFeaturesMap> found2 = new ArrayList<>();
                for (Document document : output2)
                    found2.add(mapper.convertValue(document, TranscriptToSequenceFeaturesMap.class));

                List<PositionPropertyMap> results2 = CoordinatesController.mapSequencePositionToGenomic(found2, position1.getCoordinate().getUniProtPosition());

                for (PositionPropertyMap position2 : results2) {

                    PositionPropertyMap clone = (PositionPropertyMap) BeanUtils.cloneBean(position2);
                    clone.setEntryId(position1.getEntryId());
                    clone.setEntityId(position1.getEntityId());
                    clone.setChainId(position1.getChainId());
                    clone.getCoordinate().setPdbSeqPosition(position1.getCoordinate().getPdbSeqPosition());

                    results.add(clone);
                }
            }
        }

        return results;
    }
}