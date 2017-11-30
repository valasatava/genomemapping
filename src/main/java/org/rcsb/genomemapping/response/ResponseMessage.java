package org.rcsb.genomemapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "response") // needed if we also want to generate XML
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	/** contains the same HTTP Status code returned by the server */
	@XmlElement(name = "status")
	private int status;

	/** application specific error code */
	@XmlElement(name = "code")
	private int code;

	/** message describing the error */
	@XmlElement(name = "message")
	private String message;

	/** link to page with further info */
	@XmlElement(name = "link")
	private String link;

	/** result count */
	@XmlElement(name = "count")
	private int count;

	@XmlAnyElement(lax = true)
	private List<Object> ignoredProperties;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<Object> getIgnoredProperties() {
		return ignoredProperties;
	}

	public void setIgnoredProperties(List<Object> ignoredProperties) {
		this.ignoredProperties = ignoredProperties;
	}
}
