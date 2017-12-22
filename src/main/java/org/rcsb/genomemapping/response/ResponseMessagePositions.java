package org.rcsb.genomemapping.response;

import org.rcsb.mojave.genomemapping.PositionPropertyMap;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "response") // needed if we also want to generate XML
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ PositionPropertyMap.class })
public class ResponseMessagePositions extends ResponseMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public ResponseMessagePositions() {}
	
	/** list of results, if any */
	@XmlElement(name = "results")
    private List<PositionPropertyMap> results;
	public List<PositionPropertyMap> getResults() {
		return results;
	}
	public void setResults(List<PositionPropertyMap> results) {
		this.results = results;
	}
}