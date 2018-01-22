package org.rcsb.genomemapping.functions;

import com.google.common.collect.Range;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.exceptions.TranslationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rcsb.genomemapping.constants.CommonConstants;
import org.rcsb.genomemapping.utils.GenomeDataUtils;
import org.rcsb.genomemapping.utils.RowUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.*;

/**
 * Created by Yana Valasatava on 10/20/17.
 */
public class MapGeneTranscriptsToProteinIsoforms implements FlatMapFunction<Tuple2<String, Iterable<Row>>, Row> {

    private static final Logger logger = LoggerFactory.getLogger(MapGeneTranscriptsToProteinIsoforms.class);

    public static int taxonomyId;

    public MapGeneTranscriptsToProteinIsoforms(Broadcast<Integer> bc) {
        taxonomyId = bc.getValue();
    }

    public static Map<String, JSONObject> getTranscriptsMap(JSONArray isoforms) {

        Map<String, JSONObject> map = new HashMap<>();
        for (int i=0; i<isoforms.length(); i++) {
            JSONArray txpts = isoforms.getJSONObject(i).getJSONArray("transcriptIds");
            for (int j=0; j<txpts.length(); j++) {
                map.put(txpts.getString(j), isoforms.getJSONObject(i));
            }
        }
        return map;
    }

    public static Map<Integer, List<JSONObject>> getLengthMap(JSONArray isoforms) {

        Map<Integer, List<JSONObject>> map = new HashMap<>();
        for (int i=0; i<isoforms.length(); i++) {
            String seq = isoforms.getJSONObject(i).getString("sequence");
            if (!map.keySet().contains(seq.length())) {
                map.put(seq.length(), new ArrayList<>());
            }
            map.get(seq.length()).add(isoforms.getJSONObject(i));
        }
        return map;
    }

    public static int getCodingLength(List<Row> coding) {

        int len = 0;
        for (Row cds : coding) {
            int start = cds.getInt(cds.fieldIndex(CommonConstants.COL_START));
            int end = cds.getInt(cds.fieldIndex(CommonConstants.COL_END));
            len += ( Math.abs(end-start) + 1);
        }
        return len;
    }

    private static boolean translationLength(int transcriptLength){
        if (transcriptLength < 6)
            return false;
        if (transcriptLength % 3 != 0)
            return false;
        return true;
    }

    public static JSONObject parseIsoformObject(JSONObject isoObj) {

        JSONObject iso = new JSONObject();

        if (isoObj.getString("accession").contains("-"))
            iso.put("id", isoObj.getString("accession"));
        else {
            iso.put("id", isoObj.getString("accession") + "-1");
            iso.put(CommonConstants.COL_CANONICAL, true);
        }

        iso.put("sequence", isoObj.getJSONObject("sequence").getString("sequence"));

        JSONArray transcriptIds = new JSONArray();
        if (isoObj.has("dbReferences")) {
            JSONArray references = isoObj.getJSONArray("dbReferences");
            for (int k=0; k<references.length();k++) {
                if (references.getJSONObject(k).getString("type").equals("Ensembl"))
                    transcriptIds.put(references.getJSONObject(k).getString("id"));
            }
        }
        iso.put("transcriptIds", transcriptIds);

        if (isoObj.has("comments")) {
            JSONArray comments = isoObj.getJSONArray("comments");
            for (int c=0; c<comments.length(); c++) {
                JSONObject comment = comments.getJSONObject(c);
                if (comment.has("isoforms")) {
                    JSONArray isoComments = comment.getJSONArray("isoforms");
                    for (int q=0; q<isoComments.length(); q++) {
                        JSONObject isoComment = isoComments.getJSONObject(q);
                        if (isoComment.getJSONArray("ids").toString().contains(iso.getString("id"))) {
                            iso.put("sequenceStatus", isoComment.getString("sequenceStatus"));
                            iso.put(CommonConstants.COL_CANONICAL, iso.get("sequenceStatus").equals("displayed") ? true : false);
                            return iso;
                        }
                    }
                }
            }
        }
        if (!iso.has("sequenceStatus"))
            iso.put("sequenceStatus", "displayed");
        return iso;
    }

    public static JSONArray getSequenceIsoforms(String uniProtId) throws Exception {

        HttpResponse<JsonNode> response = Unirest
                .get("https://www.ebi.ac.uk/proteins/api/proteins/{id}/isoforms.json")
                .routeParam("id", uniProtId)
                .asJson();

        if (response.getStatus() == 404)
            response = Unirest
                    .get("https://www.ebi.ac.uk/proteins/api/proteins/{id}.json")
                    .routeParam("id", uniProtId)
                    .asJson();

        if (response.getStatus() == 400)
            return new JSONArray();

        JSONArray iso = new JSONArray();
        if ( response.getBody().isArray() ) {
            for (Object o : response.getBody().getArray())
                iso.put(parseIsoformObject((JSONObject) o));
        } else {
            iso.put(parseIsoformObject(response.getBody().getObject()));
        }
        return iso;
    }

    @Override
    public Iterator<Row> call(Tuple2<String, Iterable<Row>> t) throws Exception {

        String uniProtId = t._1.split(CommonConstants.KEY_SEPARATOR)[3];
        Iterable<Row> it = t._2;

        JSONArray isoforms = getSequenceIsoforms(uniProtId);
        if (isoforms.length() == 0) {
            logger.error("Could not retrieve data for {}", uniProtId);
            return new ArrayList<Row>().iterator();
        }

        Map<String, JSONObject> txptsMap = getTranscriptsMap(isoforms);
        Map<Integer, List<JSONObject>> lengthMap = getLengthMap(isoforms);

        List<Row> list = new ArrayList<>();
        it.iterator().forEachRemaining(e -> list.add(e));

        List<Row> transcripts = new ArrayList<>();
        for (Row txpt : list) {

            String txptId = txpt.getString(txpt.fieldIndex(CommonConstants.COL_TRANSCRIPT_ID));

            if (txptsMap.keySet().contains(txptId)) {
                JSONObject isoform = txptsMap.get(txptId);
                txpt = RowUpdater.addField(txpt, CommonConstants.COL_MOLECULE_ID, isoform.getString("id"), DataTypes.StringType);
                txpt = RowUpdater.addField(txpt, CommonConstants.COL_PROTEIN_SEQUENCE, isoform.getString("sequence"), DataTypes.StringType);
                txpt = RowUpdater.addField(txpt, CommonConstants.COL_SEQUENCE_STATUS, isoform.getString("sequenceStatus"), DataTypes.StringType);
                txpt = RowUpdater.addField(txpt, CommonConstants.COL_CANONICAL, isoform.getBoolean(CommonConstants.COL_CANONICAL), DataTypes.BooleanType);
                transcripts.add(txpt);
                logger.info("The sequence of transcript {} is mapped to isoform sequence {}", txptId, isoform.getString("id"));
                
            } else {

                List<Row> coding = txpt.getList(txpt.fieldIndex(CommonConstants.COL_CODING));
                int codingLength = getCodingLength(coding);

                if (!translationLength(codingLength)) {
                    logger.debug("Chromosome positions for {} cannot be translated to protein sequence because of short or not mod3 length", txptId);
                    continue;
                }

                int proteinLength = codingLength / 3;
                if ( !lengthMap.keySet().contains(proteinLength)) {
                    logger.info("The sequence of transcript {} doesn't match any isoform sequence of {} entry", txptId, uniProtId);
                    continue;
                }

                if ( lengthMap.get(proteinLength).size() == 1 ) {

                    JSONObject isoform = lengthMap.get(proteinLength).get(0);
                    txpt = RowUpdater.addField(txpt, CommonConstants.COL_MOLECULE_ID, isoform.getString("id"), DataTypes.StringType);
                    txpt = RowUpdater.addField(txpt, CommonConstants.COL_PROTEIN_SEQUENCE, isoform.getString("sequence"), DataTypes.StringType);
                    txpt = RowUpdater.addField(txpt, CommonConstants.COL_SEQUENCE_STATUS, isoform.getString("sequenceStatus"), DataTypes.StringType);
                    txpt = RowUpdater.addField(txpt, CommonConstants.COL_CANONICAL, isoform.getBoolean(CommonConstants.COL_CANONICAL), DataTypes.BooleanType);
                    transcripts.add(txpt);
                    logger.info("The sequence of transcript {} is mapped to isoform sequence {}", txptId, isoform.getString("id"));

                } else {
                    String chr = txpt.getString(txpt.fieldIndex(CommonConstants.COL_CHROMOSOME));
                    String strand = txpt.getString(txpt.fieldIndex(CommonConstants.COL_ORIENTATION));
                    List<Range<Integer>> cds = new ArrayList<>();
                    for (Row range : coding) {
                        cds.add(Range.closed(
                                  range.getInt(range.fieldIndex(CommonConstants.COL_START))
                                , range.getInt(range.fieldIndex(CommonConstants.COL_END))));
                    }

                    String sequence;
                    try {
                        GenomeDataUtils.setTaxonomyId(taxonomyId);
                        sequence = GenomeDataUtils.getProteinSequence(strand, GenomeDataUtils.getTranscriptSequence(chr, cds));
                    } catch (CompoundNotFoundException e) {
                        logger.error("Could not construct DNA sequence for {}: {}", txptId, e.getCause());
                        continue;
                    }
                    catch (TranslationException e) {
                        logger.error("Could not construct protein sequence for {}: {}", txptId, e.getCause());
                        continue;
                    }

                    for (JSONObject isoform : lengthMap.get(proteinLength)) {
                        if (isoform.getString("sequence").equals(sequence)) {
                            txpt = RowUpdater.addField(txpt, CommonConstants.COL_MOLECULE_ID, isoform.getString("id"), DataTypes.StringType);
                            txpt = RowUpdater.addField(txpt, CommonConstants.COL_PROTEIN_SEQUENCE, isoform.getString("sequence"), DataTypes.StringType);
                            txpt = RowUpdater.addField(txpt, CommonConstants.COL_SEQUENCE_STATUS, isoform.getString("sequenceStatus"), DataTypes.StringType);
                            txpt = RowUpdater.addField(txpt, CommonConstants.COL_CANONICAL, isoform.getBoolean(CommonConstants.COL_CANONICAL), DataTypes.BooleanType);
                            transcripts.add(txpt);
                            logger.info("The sequence of transcript {} is mapped to isoform sequence {}", txptId, isoform.getString("id"));
                            continue;
                        }
                    }
                    logger.info("The sequence of transcript {} is NOT mapped to any isoform sequence of {}", txptId, uniProtId);
                }
            }
        }
        return transcripts.iterator();
    }
}