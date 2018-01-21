package org.rcsb.genomemapping.functions;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.rcsb.genomemapping.constants.CommonConstants;
import org.rcsb.mojave.genomemapping.TranscriptToSequenceFeaturesMap;
import org.rcsb.mojave.mappers.PositionMapping;
import org.rcsb.mojave.mappers.SegmentMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana Valasatava on 10/2/17.
 */
public class MapGenomicToUniProtCoordinates implements Function<Row, TranscriptToSequenceFeaturesMap> {

    private static final Logger logger = LoggerFactory.getLogger(MapGenomicToUniProtCoordinates.class);

    private static SegmentMapping getGenomicCoordinates(Row r) {

        SegmentMapping coordinates = new SegmentMapping();

        coordinates.setId(r.getInt(r.fieldIndex(CommonConstants.COL_ID)));

        PositionMapping start = new PositionMapping();
        start.setGenomicPosition(r.getInt(r.fieldIndex(CommonConstants.COL_START)));
        coordinates.setStart(start);

        PositionMapping end = new PositionMapping();
        end.setGenomicPosition(r.getInt(r.fieldIndex(CommonConstants.COL_END)));
        coordinates.setEnd(end);

        return coordinates;
    }

    private static List<SegmentMapping> setProteinCoordinatesOnForwardStrand(List<SegmentMapping> coordinates) {

        int mRNAPosEnd;
        int mRNAPosStart = 1;

        for (SegmentMapping c : coordinates) {

            mRNAPosEnd = mRNAPosStart + (c.getEnd().getGenomicPosition() - c.getStart().getGenomicPosition() + 1) - 1;

            c.getStart().setmRNAPosition(mRNAPosStart);
            c.getEnd().setmRNAPosition(mRNAPosEnd);

            c.getStart().setUniProtPosition((int) Math.ceil(mRNAPosStart / 3.0f));
            c.getEnd().setUniProtPosition((int) Math.ceil(mRNAPosEnd/3.0f));

            mRNAPosStart = mRNAPosEnd+1;
        }
        return coordinates;
    }

    private static List<SegmentMapping> setProteinCoordinatesOnReverseStrand(List<SegmentMapping> coordinates) {

        int mRNAPosEnd;
        int mRNAPosStart = 1;

        for (SegmentMapping c : coordinates) {

            Integer genStart = c.getEnd().getGenomicPosition();
            Integer genEnd = c.getStart().getGenomicPosition();

            mRNAPosEnd = mRNAPosStart + (c.getEnd().getGenomicPosition() - c.getStart().getGenomicPosition() + 1) - 1;

            c.getStart().setmRNAPosition(mRNAPosStart);
            c.getEnd().setmRNAPosition(mRNAPosEnd);

            c.getStart().setUniProtPosition((int) Math.ceil(mRNAPosStart / 3.0f));
            c.getEnd().setUniProtPosition((int) Math.ceil(mRNAPosEnd/3.0f));

            c.getStart().setGenomicPosition(genStart);
            c.getEnd().setGenomicPosition(genEnd);

            mRNAPosStart = mRNAPosEnd+1;
        }
        return coordinates;
    }
    
    @Override
    public TranscriptToSequenceFeaturesMap call(Row row) throws Exception {

        TranscriptToSequenceFeaturesMap m = new TranscriptToSequenceFeaturesMap();

        m.setChromosome(row.getString(row.fieldIndex(CommonConstants.COL_CHROMOSOME)));
        m.setGeneId(row.getString(row.fieldIndex(CommonConstants.COL_GENE_ID)));
        m.setGeneName(row.getString(row.fieldIndex(CommonConstants.COL_GENE_NAME)));
        m.setOrientation(row.getString(row.fieldIndex(CommonConstants.COL_ORIENTATION)));
        m.setUniProtId(row.getString(row.fieldIndex(CommonConstants.COL_UNIPROT_ACCESSION)));

        m.setTranscriptId(row.getString(row.fieldIndex(CommonConstants.COL_TRANSCRIPT_ID)));
        m.setTranscriptName(row.getString(row.fieldIndex(CommonConstants.COL_TRANSCRIPT_NAME)));

        m.setMoleculeId(row.getString(row.fieldIndex(CommonConstants.COL_MOLECULE_ID)));
        m.setSequence(row.getString(row.fieldIndex(CommonConstants.COL_PROTEIN_SEQUENCE)));
        m.setSequenceStatus(row.getString(row.fieldIndex(CommonConstants.COL_SEQUENCE_STATUS)));
        m.setCanonical(row.getBoolean(row.fieldIndex(CommonConstants.COL_CANONICAL)));

        List<SegmentMapping> coordinates = new ArrayList<>();
        for (Object o : row.getList(row.fieldIndex(CommonConstants.COL_CODING)))
            coordinates.add(getGenomicCoordinates((Row) o));

        if (m.getOrientation().equals("+"))
            coordinates = setProteinCoordinatesOnForwardStrand(coordinates);
        else
            coordinates = setProteinCoordinatesOnReverseStrand(coordinates);

        m.setCoordinates(coordinates);

        return m;
    }
}