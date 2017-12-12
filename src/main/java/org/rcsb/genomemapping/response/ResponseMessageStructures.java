package org.rcsb.genomemapping.response;

import org.rcsb.genomemapping.model.Position;
import org.rcsb.mojave.genomemapping.GenomicToStructureMapping;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "response") // needed if we also want to generate XML
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ Position.class })
public class ResponseMessageStructures extends ResponseMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	public ResponseMessageStructures() {}
	
	/** list of results, if any */
	@XmlElement(name = "results")
    private List<GenomicToStructureMapping> results;
	public List<GenomicToStructureMapping> getResults() {
		return results;
	}
	public void setResults(List<GenomicToStructureMapping> results) {
		this.results = results;
	}

	
}