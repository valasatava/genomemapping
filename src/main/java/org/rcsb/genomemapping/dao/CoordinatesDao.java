package org.rcsb.genomemapping.dao;

import org.rcsb.mojave.genomemapping.models.Position;
import java.util.List;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
public interface CoordinatesDao {

    public List<Position> mapGeneticPosition(int taxonomyId, String chromosome, int position) throws Exception;
}
