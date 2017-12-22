package org.rcsb.genomemapping.dao;

import org.rcsb.mojave.genomemapping.PositionPropertyMap;

import java.util.List;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
public interface CoordinatesDao {

    List<PositionPropertyMap> mapGenomicPosition(int taxonomyId, String chromosome, int position, boolean canonical) throws Exception;
    List<PositionPropertyMap> mapPdbSeqPosition(String entryId, String entityId, int position, boolean canonical) throws Exception;
}
