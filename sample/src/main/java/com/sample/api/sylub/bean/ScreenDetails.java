package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class Entity.
 */
public class ScreenDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private String screenId;

	private String screenName;

	private String displayName;

	private String description;

	private String catalogId;

	private List<Parameter> parameterList = new ArrayList<Parameter>();

	private List<GroupParameter> groupParamList = new ArrayList<GroupParameter>();

	public List<GroupParameter> getGroupParamList() {
		return groupParamList;
	}

	public void setGroupParamList(List<GroupParameter> groupParamList) {
		this.groupParamList = groupParamList;
	}

	public String getScreenId() {
		return screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	/**
	 * Gets the parameter list.
	 * 
	 * @return the parameter list
	 */
	public final List<Parameter> getParameterList() {
		return parameterList;
	}

	/**
	 * Sets the parameter list.
	 * 
	 * @param parameterList
	 *            the new parameter list
	 */
	public final void setParameterList(final List<Parameter> parameterList) {
		this.parameterList = parameterList;
	}
}
