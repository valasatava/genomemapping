package org.rcsb.genomemapping.dao;

import org.rcsb.mojave.genomemapping.GenomicToStructureMapping;

import java.util.List;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public interface StructuresDao {

    List<GenomicToStructureMapping> getStructuresByGeneName(int taxonomyId, String geneName, boolean canonical);

    List<GenomicToStructureMapping> getStructuresByGeneId(int taxonomyId, String geneId, boolean canonical);

    List<GenomicToStructureMapping> getStructuresByGeneticPosition(int taxonomyId, String chromosome, int position);
}
