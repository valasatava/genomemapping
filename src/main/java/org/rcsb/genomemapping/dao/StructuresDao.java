package org.rcsb.genomemapping.dao;

import org.rcsb.mojave.genomemapping.MultipleFeaturesMap;
import org.rcsb.mojave.genomemapping.PositionPropertyMap;

import java.util.List;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public interface StructuresDao {

    List<MultipleFeaturesMap> getStructuresByGeneName(int taxonomyId, String geneName, boolean canonical);

    List<MultipleFeaturesMap> getStructuresByGeneId(int taxonomyId, String geneId, boolean canonical);

    List<PositionPropertyMap> getStructuresByGeneticPosition(int taxonomyId, String chromosome, int position, boolean canonical);
}
