package org.rcsb.genomemapping.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Position {

    protected GenomicPosition genomicPosition;
    protected IsoformPosition isoformPosition;
    protected StructurePosition structurePosition;

    public GenomicPosition getGenomicPosition() {
        return genomicPosition;
    }

    public void setGenomicPosition(GenomicPosition genomicPosition) {
        this.genomicPosition = genomicPosition;
    }

    public IsoformPosition getIsoformPosition() {
        return isoformPosition;
    }

    public void setIsoformPosition(IsoformPosition isoformPosition) {
        this.isoformPosition = isoformPosition;
    }

    public StructurePosition getStructurePosition() {
        return structurePosition;
    }

    public void setStructurePosition(StructurePosition structurePosition) {
        this.structurePosition = structurePosition;
    }
}
