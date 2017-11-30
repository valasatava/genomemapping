package org.rcsb.genomemapping.controllers;

import org.rcsb.genomemapping.utils.AppHelper;
import org.rcsb.mojave.genomemapping.mappers.EntityToIsoform;
import org.rcsb.mojave.genomemapping.mappers.GeneToUniProt;
import org.rcsb.mojave.genomemapping.mappers.TranscriptToIsoform;
import org.rcsb.mojave.genomemapping.models.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana Valasatava on 11/27/17.
 */
public class CoordinatesController {

    public static List<Position> mapGeneticPositionToSequence(List<GeneToUniProt> list, int position) throws InvocationTargetException, IllegalAccessException {

        List<Position> results = new ArrayList<>();

        for (GeneToUniProt gene : list) {

            for (TranscriptToIsoform elem : gene.getIsoforms()) {

                int cdsId = 0;
                int delta = 0;
                List<CoordinatesRange> cds = elem.getCodingCoordinates();
                for (CoordinatesRange c : cds) {

                    int start = c.getStart();
                    int end = c.getEnd();

                    if ((start <= position) && (position <= end)) {
                        if (gene.getOrientation().equals("-")) {
                            delta = end - position + 1;
                            cdsId = c.getId();
                            break;
                        } else if (gene.getOrientation().equals("+")) {
                            delta = position - start;
                            cdsId = c.getId();
                            break;
                        }
                    }
                }

                int seqCoord = 0;
                List<CoordinatesRange> isoformCoordinates = elem.getIsoformCoordinates();
                for (CoordinatesRange ic : isoformCoordinates) {
                    if (ic.getId() != cdsId)
                        continue;
                    int start = ic.getStart();
                    seqCoord = start + (int) Math.ceil(delta / 3.0f) - 1;
                    break;
                }

                GenomicPosition gp = new GenomicPosition();
                AppHelper.nullAwareBeanCopy(gp, gene);
                AppHelper.nullAwareBeanCopy(gp, elem);
                gp.setCoordinate(position);

                IsoformPosition ip = new IsoformPosition();
                AppHelper.nullAwareBeanCopy(ip, gene);
                AppHelper.nullAwareBeanCopy(ip, elem);
                ip.setCoordinate(seqCoord);

                Position p = new Position();
                p.setGenomicPosition(gp);
                p.setIsoformPosition(ip);
                results.add(p);
            }
        }
        return results;
    }

    public static List<Position> mapSequencePositionToStructure(List<EntityToIsoform> list, int position) throws InvocationTargetException, IllegalAccessException {

        List<Position> results = new ArrayList<>();

        for (EntityToIsoform elem : list) {

            int structCoord = 0;
            List<CoordinatesRange> sequenceCoords = elem.getIsoformCoordinates();
            List<CoordinatesRange> structureCoords = elem.getStructureCoordinates();

            for (int i = 0; i < sequenceCoords.size(); i++) {
                int startSeq = sequenceCoords.get(i).getStart();
                int endSeq = sequenceCoords.get(i).getEnd();
                if ( (startSeq<=position) && (position<=endSeq) ) {
                    int delta = position - startSeq;
                    structCoord = structureCoords.get(i).getStart() + delta;
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