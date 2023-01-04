package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;
import java.util.List;

/**
 * The Class DependParameter.
 */
public class DependParameter implements Serializable{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The name. */
	String name;

	/** The depend param value list. */
	List<DependParameterValue> dependParamValueList;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the depend param value list.
	 *
	 * @return the depend param value list
	 */
	public List<DependParameterValue> getDependParamValueList() {
		return dependParamValueList;
	}

	/**
	 * Sets the depend param value list.
	 *
	 * @param dependParamValueList the new depend param value list
	 */
	public void setDependParamValueList(
			List<DependParameterValue> dependParamValueList) {
		this.dependParamValueList = dependParamValueList;
	}
}
