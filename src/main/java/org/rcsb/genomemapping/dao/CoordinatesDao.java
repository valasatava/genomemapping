package org.rcsb.genomemapping.dao;

import org.rcsb.mojave.genomemapping.PositionPropertyMap;

import java.util.List;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
public interface CoordinatesDao {

    List<PositionPropertyMap> mapGeneticPosition(int taxonomyId, String chromosome, int position, boolean canonical) throws Exception;
}
