package org.rcsb.genomemapping.controller;

import org.rcsb.genomemapping.utils.AppHelper;
import org.rcsb.mojave.genomemapping.PositionPropertyMap;
import org.rcsb.mojave.genomemapping.SequenceToStructureFeaturesMap;
import org.rcsb.mojave.genomemapping.TranscriptToSequenceFeaturesMap;
import org.rcsb.mojave.mappers.PositionMapping;
import org.rcsb.mojave.mappers.SegmentMapping;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana Valasatava on 11/27/17.
 */
public class CoordinatesController {

    public static int convertGenomicToProteinCoordinate(String orientation, int seqStart, int genStart, int genEnd, int genPos) {

        int seqPos = -1;
        if (orientation.equals("-")) {
            int delta = genEnd - genPos + 1;
            seqPos = seqStart + (int) Math.ceil(delta / 3.0f) - 1;
        } else if (orientation.equals("-")) {
            int delta = genPos - genStart;
            seqPos = seqStart + (int) Math.ceil(delta / 3.0f) - 1;
        }
        return seqPos;
    }

    public static int convertGenomicToMRNACoordinate(String orientation, int mRNAStart, int genStart, int genEnd, int genPos) {

        if (orientation.equals("+"))
            return mRNAStart + (genPos - genStart);
        else
            return mRNAStart + (genEnd - genPos);
    }

    public static List<PositionPropertyMap> mapGeneticPositionToSequence(List<TranscriptToSequenceFeaturesMap> list, int genPos) throws InvocationTargetException, IllegalAccessException {

        List<PositionPropertyMap> results = new ArrayList<>();

        for (TranscriptToSequenceFeaturesMap transcript : list) {

            boolean mapped = false;
            PositionMapping position = new PositionMapping();
            List<SegmentMapping> mapping = transcript.getCoordinates();
            for (SegmentMapping c : mapping) {

                int genStart = c.getStart().getGeneticPosition();
                int genEnd = c.getEnd().getGeneticPosition();
                int seqStart = c.getStart().getUniProtPosition();

                int mRNAStart = c.getStart().getmRNAPosition();

                if ((genStart <= genPos) && (genPos <= genEnd)) {

                    int seqPos = convertGenomicToProteinCoordinate(transcript.getOrientation(), seqStart, genStart, genEnd, genPos);
                    position.setGeneticPosition(genPos);

                    int mRNAPos = convertGenomicToMRNACoordinate(transcript.getOrientation(), mRNAStart, genStart, genEnd, genPos);
                    position.setmRNAPosition(mRNAPos);
                    position.setUniProtPosition(seqPos);

                    mapped = true;
                    break;
                }
            }

            if (mapped) {
                PositionPropertyMap pos = new PositionPropertyMap();
                AppHelper.nullAwareBeanCopy(pos, transcript);
                pos.setCoordinate(position);
                results.add(pos);
            }
        }
        return results;
    }

    public static int convertProteinToStructureCoordinate(int structStart, int structEnd, int seqStart, int seqPos) {

        int structPos = -1;

        int delta = seqPos - seqStart;
        structPos = structStart+delta;
        if (structPos > structEnd)
            structPos = structEnd;

        return structPos;
    }

    public static List<PositionPropertyMap> mapSequencePositionToStructure(List<SequenceToStructureFeaturesMap> list, int seqPos) throws InvocationTargetException, IllegalAccessException {

        List<PositionPropertyMap> results = new ArrayList<>();

        for (SequenceToStructureFeaturesMap isoform : list) {

            boolean mapped = false;
            PositionMapping positionMapping = new PositionMapping();
            List<SegmentMapping> mapping = isoform.getCoordinates();
            for (SegmentMapping c : mapping) {

                int structStart = c.getStart().getSeqResPosition();
                int structEnd = c.getEnd().getSeqResPosition();
                int seqStart = c.getStart().getUniProtPosition();
                int seqEnd = c.getEnd().getUniProtPosition();

                if ( (seqStart<=seqPos) && (seqPos<=seqEnd) ) {
                    int structPos = convertProteinToStructureCoordinate(structStart, structEnd, seqStart, seqPos);
                    positionMapping.setUniProtPosition(seqPos);
                    positionMapping.setSeqResPosition(structPos);
                    mapped = true;
                    break;
                }
            }

            if (mapped) {
                PositionPropertyMap pos = new PositionPropertyMap();
                AppHelper.nullAwareBeanCopy(pos, isoform);
                pos.setCoordinate(positionMapping);
                results.add(pos);
            }
        }

        return results;
    }
}