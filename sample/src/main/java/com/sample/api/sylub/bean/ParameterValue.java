package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;
import java.util.List;
// TODO: Auto-generated Javadoc

/**
 * The Class ParameterValue.
 */
public class ParameterValue implements Serializable, Cloneable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5067967881441553481L;

	/** The parameter condition. */
	private List<ParameterCondition> parameterCondition;

	/** The depend parameter. */
	protected List<DependParameter> dependParameter;

	/** The name. */
	protected String name;

	/** The default value. */
	protected String default_value;

	/** The default values. */
	protected List<DefaultValue> defaultValues;

	/**
	 * Gets the parameter condition.
	 *
	 * @return the parameter condition
	 */
	public List<ParameterCondition> getParameterCondition() {
		return parameterCondition;
	}

	/**
	 * Sets the parameter condition.
	 *
	 * @param parameterCondition the new parameter condition
	 */
	public void setParameterCondition(List<ParameterCondition> parameterCondition) {
		this.parameterCondition = parameterCondition;
	}

	/**
	 * Gets the depend parameter.
	 *
	 * @return the depend parameter
	 */
	public List<DependParameter> getDependParameter() {
		return dependParameter;
	}

	/**
	 * Sets the depend parameter.
	 *
	 * @param dependParameter the new depend parameter
	 */
	public void setDependParameter(List<DependParameter> dependParameter) {
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
	 * Gets the default values.
	 *
	 * @return the default values
	 */
	public List<DefaultValue> getDefaultValues() {
		return defaultValues;
	}

	/**
	 * Sets the default values.
	 *
	 * @param defaultValues the new default values
	 */
	public void setDefaultValues(List<DefaultValue> defaultValues) {
		this.defaultValues = defaultValues;
	}

	/**
	 * Gets the default value.
	 *
	 * @return the default value
	 */
	public String getDefault_value() {
		return default_value;
	}

	/**
	 * Sets the default value.
	 *
	 * @param defaultValue the new default value
	 */
	public void setDefault_value(String defaultValue) {
		default_value = defaultValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}




	/** The value. */
	private String value;

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}



	/** The screen name. */
	private String screenName;

	/**
	 * Gets the screen name.
	 *
	 * @return the screen name
	 */
	public String getScreenName() {
		return screenName;
	}

	/**
	 * Sets the screen name.
	 *
	 * @param screenName the new screen name
	 */
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}








}
