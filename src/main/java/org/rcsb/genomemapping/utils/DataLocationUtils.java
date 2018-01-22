package org.rcsb.genomemapping.utils;

import java.util.Properties;

/**
 * Created by Yana Valasatava on 12/25/17.
 */
public class DataLocationUtils {

    private static final Properties props = Parameters.getProperties();

    public static final String HUMAN_GENOME_FILE = props.getProperty("human.genome.file.path");
    public static final String MOUSE_GENOME_FILE = props.getProperty("mouse.genome.file.path");

    public static final String HUMAN_GENE_SET_ENSEMBLE_PATH = props.getProperty("ensemble.human.geneset.file.path");
    public static final String HUMAN_GENE_SET_UCSC_PATH = props.getProperty("ucsc.human.geneset.file.path");
    public static final String MOUSE_GENE_SET_ENSEMBLE_PATH = props.getProperty("ensemble.mouse.geneset.file.path");
    public static final String MOUSE_GENE_SET_UCSC_PATH = props.getProperty("ucsc.mouse.geneset.file.path");

    public static final String HUMAN_UNIPROT_IDMAPPING_PATH = props.getProperty("human.uniprot.idmapping.file.path");
    public static final String MOUSE_UNIPROT_IDMAPPING_PATH = props.getProperty("mouse.uniprot.idmapping.file.path");

    public static String getGeneSetFileLocation(int taxonomyId, String format) {

        if (format.equals("refFlat")) {
            if (taxonomyId == 9606) {
                return HUMAN_GENE_SET_UCSC_PATH;
            } else if (taxonomyId == 10090) {
                return MOUSE_GENE_SET_UCSC_PATH;
            }
        } else if (format.equals("gtf")) {
            if (taxonomyId == 9606) {
                return HUMAN_GENE_SET_ENSEMBLE_PATH;
            } else if (taxonomyId == 10090) {
                return MOUSE_GENE_SET_ENSEMBLE_PATH;
            }
        }
        return null;
    }

    public static String getUniprotMappingFileLocation(int taxonomyId) {

        if (taxonomyId == 9606) {
            return HUMAN_UNIPROT_IDMAPPING_PATH;
        } else if (taxonomyId == 10090) {
            return MOUSE_UNIPROT_IDMAPPING_PATH;
        }
        return null;
    }

    public static String getGenome2bitFileLocation(int taxonomyId) {
        if (taxonomyId == 9606) {
            return HUMAN_GENOME_FILE;
        } else if (taxonomyId == 10090) {
            return MOUSE_GENOME_FILE;
        }
        return null;
    }
}