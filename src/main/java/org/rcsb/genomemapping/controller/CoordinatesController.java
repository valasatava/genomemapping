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

    public static int convertGenomicToProteinCoordinate(String orientation, int seqStart, int genStart, int genPos) {

        int seqPos = -1;
        if (orientation.equals("-")) {
            int delta = genStart - genPos + 1;
            seqPos = seqStart + (int) Math.ceil(delta / 3.0f) - 1;
        } else if (orientation.equals("+")) {
            int delta = genPos - genStart + 1;
            seqPos = seqStart + (int) Math.ceil(delta / 3.0f) - 1;
        }
        return seqPos;
    }

    public static int convertProteinCoordinateToGenomic(String orientation, int startTo, int startFrom, int pos) {

        int posTo = -1;
        int delta = (pos - startFrom)*3;

        if (orientation.equals("-")) {
            posTo = startTo - delta;
        } else if (orientation.equals("+")) {
            posTo = startTo + delta;
        }
        return posTo;
    }

    public static int convertGenomicToMRNACoordinate(String orientation, int mRNAStart, int genStart, int genPos) {

        if (orientation.equals("+"))
            return mRNAStart + (genPos - genStart);
        else
            return mRNAStart + (genStart - genPos);
    }

    public static List<PositionPropertyMap> mapGeneticPositionToSequence(List<TranscriptToSequenceFeaturesMap> list, int genPos) throws InvocationTargetException, IllegalAccessException {

        List<PositionPropertyMap> results = new ArrayList<>();

        for (TranscriptToSequenceFeaturesMap transcript : list) {

            boolean mapped = false;
            PositionMapping position = new PositionMapping();
            List<SegmentMapping> mapping = transcript.getCoordinates();
            for (SegmentMapping c : mapping) {

                int genStart = c.getStart().getGenomicPosition();
                int genEnd = c.getEnd().getGenomicPosition();

                int seqStart = c.getStart().getUniProtPosition();
                int mRNAStart = c.getStart().getmRNAPosition();

                if ( ( transcript.getOrientation().equals("+") && (genStart <= genPos) && (genPos <= genEnd) )
                        || (transcript.getOrientation().equals("-") && (genEnd <= genPos) && (genPos <= genStart)) ){

                    int mRNAPos = convertGenomicToMRNACoordinate(transcript.getOrientation(), mRNAStart, genStart, genPos);
                    position.setmRNAPosition(mRNAPos);

                    int seqPos = convertGenomicToProteinCoordinate(transcript.getOrientation(), seqStart, genStart, genPos);
                    position.setGenomicPosition(genPos);
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

    public static int convertCoordinateBaseOne(int startTo, int endTo, int startFrom, int pos) {

        int structPos = -1;
        int delta = pos - startFrom;
        structPos = startTo+delta;
        if (structPos > endTo)
            structPos = endTo;

        return structPos;
    }

    public static List<PositionPropertyMap> mapSequencePositionToStructure(List<SequenceToStructureFeaturesMap> list, int seqPos) throws InvocationTargetException, IllegalAccessException {

        List<PositionPropertyMap> results = new ArrayList<>();

        for (SequenceToStructureFeaturesMap isoform : list) {

            boolean mapped = false;
            PositionMapping positionMapping = new PositionMapping();
            List<SegmentMapping> mapping = isoform.getCoordinates();
            for (SegmentMapping c : mapping) {

                int structStart = c.getStart().getPdbSeqPosition();
                int structEnd = c.getEnd().getPdbSeqPosition();
                int seqStart = c.getStart().getUniProtPosition();
                int seqEnd = c.getEnd().getUniProtPosition();

                if ( (seqStart<=seqPos) && (seqPos<=seqEnd) ) {
                    int structPos = convertCoordinateBaseOne(structStart, structEnd, seqStart, seqPos);
                    positionMapping.setUniProtPosition(seqPos);
                    positionMapping.setPdbSeqPosition(structPos);
                    mapped = true;
                    break;
                }
            }

            if (mapped) {
                PositionPropertyMap position = new PositionPropertyMap();
                AppHelper.nullAwareBeanCopy(position, isoform);
                position.setCoordinate(positionMapping);
                results.add(position);
            }
        }

        return results;
    }

    public static List<PositionPropertyMap> mapStructurePositionToSequence(List<SequenceToStructureFeaturesMap> list, int pos) throws InvocationTargetException, IllegalAccessException {

        List<PositionPropertyMap> results = new ArrayList<>();

        for (SequenceToStructureFeaturesMap isoform : list) {

            boolean mapped = false;
            PositionMapping positionMapping = new PositionMapping();
            List<SegmentMapping> mapping = isoform.getCoordinates();
            for (SegmentMapping c : mapping) {

                int structStart = c.getStart().getPdbSeqPosition();
                int structEnd = c.getEnd().getPdbSeqPosition();
                int seqStart = c.getStart().getUniProtPosition();
                int seqEnd = c.getEnd().getUniProtPosition();

                if ( (structStart<=pos) && (pos<=structEnd) ) {
                    int seqPos = convertCoordinateBaseOne(seqStart, seqEnd, structStart, pos);
                    positionMapping.setUniProtPosition(seqPos);
                    positionMapping.setPdbSeqPosition(pos);
                    mapped = true;
                    break;
                }
            }

            if (mapped) {
                PositionPropertyMap position = new PositionPropertyMap();
                AppHelper.nullAwareBeanCopy(position, isoform);
                position.setCoordinate(positionMapping);
                results.add(position);
            }
        }

        return results;
    }

    public static List<PositionPropertyMap> mapSequencePositionToGenomic(List<TranscriptToSequenceFeaturesMap> list, int pos) throws InvocationTargetException, IllegalAccessException {

        List<PositionPropertyMap> results = new ArrayList<>();

        for (TranscriptToSequenceFeaturesMap transcript : list) {

            boolean mapped = false;
            PositionMapping position = new PositionMapping();
            List<SegmentMapping> mapping = transcript.getCoordinates();
            for (SegmentMapping c : mapping) {

                int genStart = c.getStart().getGenomicPosition();
                int mRNAStart = c.getStart().getmRNAPosition();

                int seqStart = c.getStart().getUniProtPosition();
                int seqEnd = c.getEnd().getUniProtPosition();

                if ( (seqStart <= pos) && (pos <= seqEnd) ){

                    position.setUniProtPosition(pos);

                    int genPos = convertProteinCoordinateToGenomic(transcript.getOrientation(), genStart, seqStart, pos);
                    position.setGenomicPosition(genPos);

                    int mRNAPos = convertGenomicToMRNACoordinate(transcript.getOrientation(), mRNAStart, genStart, genPos);
                    position.setmRNAPosition(mRNAPos);

                    mapped = true;
                    break;
                }
            }

            if (mapped) {
                PositionPropertyMap posprop = new PositionPropertyMap();
                AppHelper.nullAwareBeanCopy(posprop, transcript);
                posprop.setCoordinate(position);
                results.add(posprop);
            }
        }

        return results;
    }
}