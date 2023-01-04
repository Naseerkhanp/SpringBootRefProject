package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;

/**
 * The Class ParameterCondition.
 */
public class ParameterCondition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The param id. */
	String param_id;

	/** The param name. */
	String param_name;

	/** The ref id. */
	String ref_id;

	/**
	 * Gets the param id.
	 *
	 * @return the param id
	 */
	public String getParam_id() {
		return param_id;
	}

	/**
	 * Sets the param id.
	 *
	 * @param paramId the new param id
	 */
	public void setParam_id(String paramId) {
		param_id = paramId;
	}

	/**
	 * Gets the param name.
	 *
	 * @return the param name
	 */
	public String getParam_name() {
		return param_name;
	}

	/**
	 * Sets the param name.
	 *
	 * @param paramName the new param name
	 */
	public void setParam_name(String paramName) {
		param_name = paramName;
	}

	/**
	 * Gets the ref id.
	 *
	 * @return the ref id
	 */
	public String getRef_id() {
		return ref_id;
	}

	/**
	 * Sets the ref id.
	 *
	 * @param refId the new ref id
	 */
	public void setRef_id(String refId) {
		ref_id = refId;
	}
}
