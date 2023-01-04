package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;

/**
 * The Class DefaultValue.
 */
public class DefaultValue implements Serializable{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The parameter condition. */
	protected ParameterCondition parameterCondition;

	/** The depend parameter. */
	protected DependParameter dependParameter;

	/** The name. */
	protected String name;

	/** The id. */
	protected String id;

	/**
	 * Gets the parameter condition.
	 *
	 * @return the parameter condition
	 */
	public ParameterCondition getParameterCondition() {
		return parameterCondition;
	}

	/**
	 * Sets the parameter condition.
	 *
	 * @param parameterCondition the new parameter condition
	 */
	public void setParameterCondition(ParameterCondition parameterCondition) {
		this.parameterCondition = parameterCondition;
	}

	/**
	 * Gets the depend parameter.
	 *
	 * @return the depend parameter
	 */
	public DependParameter getDependParameter() {
		return dependParameter;
	}

	/**
	 * Sets the depend parameter.
	 *
	 * @param dependParameter the new depend parameter
	 */
	public void setDependParameter(DependParameter dependParameter) {
		this.dependParameter = dependParameter;
	}

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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}
}
