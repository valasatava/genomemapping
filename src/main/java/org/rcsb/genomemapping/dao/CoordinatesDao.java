package org.rcsb.genomemapping.dao;

import org.rcsb.genomemapping.model.Position;

import java.util.List;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
public interface CoordinatesDao {

    public List<Position> mapGeneticPosition(int taxonomyId, String chromosome, int position) throws Exception;
}
