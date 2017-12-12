package org.rcsb.genomemapping.controller;

import org.rcsb.genomemapping.model.GenomicPosition;
import org.rcsb.genomemapping.model.IsoformPosition;
import org.rcsb.genomemapping.model.Position;
import org.rcsb.genomemapping.model.StructurePosition;
import org.rcsb.genomemapping.utils.AppHelper;
import org.rcsb.mojave.genomemapping.GeneTranscriptToProteinSequence;
import org.rcsb.mojave.genomemapping.ProteinSequenceToProteinStructure;
import org.rcsb.mojave.mappers.SegmentMapping;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana Valasatava on 11/27/17.
 */
public class CoordinatesController {

    public static List<Position> mapGeneticPositionToSequence(List<GeneTranscriptToProteinSequence> list, int position) throws InvocationTargetException, IllegalAccessException {

        List<Position> results = new ArrayList<>();

        for (GeneTranscriptToProteinSequence transcript : list) {

            int delta = 0;
            int seqCoord = 0;
            List<SegmentMapping> mapping = transcript.getCoordinatesMapping();
            for (SegmentMapping c : mapping) {

                int start = c.getStart().getGeneticPosition();
                int end = c.getEnd().getGeneticPosition();

                if ((start <= position) && (position <= end)) {
                    if (transcript.getOrientation().equals("-")) {
                        delta = end - position + 1;
                        int seqStart = c.getStart().getUniProtPosition();
                        seqCoord = seqStart + (int) Math.ceil(delta / 3.0f) - 1;
                        break;
                    } else if (transcript.getOrientation().equals("+")) {
                        delta = position - start;
                        int seqStart = c.getStart().getUniProtPosition();
                        seqCoord = seqStart + (int) Math.ceil(delta / 3.0f) - 1;
                        break;
                    }
                }
            }


            GenomicPosition gp = new GenomicPosition();
            AppHelper.nullAwareBeanCopy(gp, transcript);
            gp.setCoordinate(position);

            IsoformPosition ip = new IsoformPosition();
            AppHelper.nullAwareBeanCopy(ip, transcript);
            ip.setCoordinate(seqCoord);

            Position p = new Position();
            p.setGenomicPosition(gp);
            p.setIsoformPosition(ip);
            results.add(p);

        }
        return results;
    }

    public static List<Position> mapSequencePositionToStructure(List<ProteinSequenceToProteinStructure> list, int position) throws InvocationTargetException, IllegalAccessException {

        List<Position> results = new ArrayList<>();

        for (ProteinSequenceToProteinStructure elem : list) {

            int structCoord = 0;
            List<SegmentMapping> coords = elem.getCoordinatesMapping();
            for (int i = 0; i < coords.size(); i++) {

                int startSeq = coords.get(i).getStart().getUniProtPosition();
                int endSeq = coords.get(i).getEnd().getUniProtPosition();
                if ( (startSeq<=position) && (position<=endSeq) ) {
                    int delta = position - startSeq;
                    structCoord = coords.get(i).getStart().getSeqResPosition() + delta;
                    break;
                }
            }

            StructurePosition sp = new StructurePosition();
            AppHelper.nullAwareBeanCopy(sp, elem);
            sp.setCoordinate(structCoord);

            Position p = new Position();
            p.setStructurePosition(sp);

            results.add(p);
        }

        return results;
    }

}