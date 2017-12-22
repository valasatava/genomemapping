package org.rcsb.genomemapping.response;

import org.rcsb.mojave.genomemapping.MultipleFeaturesMap;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "response") // needed if we also want to generate XML
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ MultipleFeaturesMap.class })
public class ResponseMessageFeatures extends ResponseMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	public ResponseMessageFeatures() {}
	
	/** list of results, if any */
	@XmlElement(name = "results")
    private List<MultipleFeaturesMap> results;
	public List<MultipleFeaturesMap> getResults() {
		return results;
	}
	public void setResults(List<MultipleFeaturesMap> results) {
		this.results = results;
	}

	
}