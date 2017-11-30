package org.rcsb.genomemapping.response;

import org.rcsb.mojave.genomemapping.models.Position;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "response") // needed if we also want to generate XML
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ Position.class })
public class ResponseMessagePositions extends ResponseMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public ResponseMessagePositions() {}
	
	/** list of results, if any */
	@XmlElement(name = "results")
    private List<Position> results;
	public List<Position> getResults() {
		return results;
	}
	public void setResults(List<Position> results) {
		this.results = results;
	}

	
}