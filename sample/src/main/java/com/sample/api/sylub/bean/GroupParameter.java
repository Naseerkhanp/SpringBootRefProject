package com.excelacom.century.apolloneoutbound.bean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;



/**
 * The Class GroupParameter.
 */
@XmlRootElement
public class GroupParameter implements Cloneable,Comparator<GroupParameter> {

    /** The entity name. */
    private String entityName;

    /** The description. */
    private String description;

    /** The entity id. */
    private String entityId;

    /** The entity id value. */
    private String entityIdValue;

    /** The event id. */
    private String eventId;

    /** The query id. */
    private String queryId;

    /** The view type. */
    private String viewType;

    /** The entity sequence id. */
    private String entitySequenceId;

    /** The action. */
    private String action;

    /** The parameter list. */
    private List<Parameter> parameterList = new ArrayList<Parameter>();

    /** The group param list. */
    private List<GroupParameter> groupParamList= new ArrayList<GroupParameter>();

    /**
     * Gets the group param list.
     *
     * @return the group param list
     */
    public List<GroupParameter> getGroupParamList() {
		return groupParamList;
	}

	/**
	 * Sets the group param list.
	 *
	 * @param groupParamList the new group param list
	 */
	public void setGroupParamList(List<GroupParameter> groupParamList) {
		this.groupParamList = groupParamList;
	}

	/** The display name. */
	private String displayName;

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
		return displayName;
	}

	/**
	 * Sets the display name.
	 *
	 * @param displayName the new display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone()throws CloneNotSupportedException{
    	GroupParameter gpObj=(GroupParameter)super.clone();

    	//Deep Clone

    	ListIterator<Parameter> paramList=parameterList.listIterator();
    	List<Parameter> parameterList=new ArrayList<Parameter>();
    	while(paramList.hasNext())
    	{
    	Parameter paramObj=(Parameter)paramList.next().clone();
    	//paramObj.setValue("");
    	parameterList.add(paramObj);
    	}
    	gpObj.setParameterList(parameterList);


    	return gpObj;
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
      * @param parameterList the new parameter list
      */
    public final void setParameterList(final List<Parameter> parameterList) {
        this.parameterList = parameterList;
    }

    /**
     * Gets the entity name.
     *
     * @return the entity name
     */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Sets the entity name.
	 *
	 * @param entityName the new entity name
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

 	/**
 	 * Gets the entity id.
 	 *
 	 * @return the entity id
 	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * Sets the entity id.
	 *
	 * @param entityId the new entity id
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

 	/**
 	 * Gets the entity id value.
 	 *
 	 * @return the entity id value
 	 */
	public String getEntityIdValue() {
		return entityIdValue;
	}

 	/**
 	 * Sets the entity id value.
 	 *
 	 * @param entityIdValue the new entity id value
 	 */
	public void setEntityIdValue(String entityIdValue) {
		this.entityIdValue = entityIdValue;
	}

	/**
	 * Gets the event id.
	 *
	 * @return the event id
	 */
    public final String getEventId() {
        return eventId;
    }

    /**
     * Sets the event id.
     *
     * @param eventId the new event id
     */
    public final void setEventId(final String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the query id.
     *
     * @return the query id
     */
	public String getQueryId() {
		return queryId;
	}

	/**
	 * Sets the query id.
	 *
	 * @param queryId the new query id
	 */
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	/**
	 * Gets the view type.
	 *
	 * @return the view type
	 */
	public String getViewType() {
		return viewType;
	}

	/**
	 * Sets the view type.
	 *
	 * @param viewType the new view type
	 */
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Gets the entity sequence id.
	 *
	 * @return the entity sequence id
	 */
	public final String getEntitySequenceId() {
		return entitySequenceId;
	}

	/**
	 * Sets the entity sequence id.
	 *
	 * @param entitySequenceId the new entity sequence id
	 */
	public final void setEntitySequenceId(String entitySequenceId) {
		this.entitySequenceId = entitySequenceId;
	}

	/** The entity map. */
	private Map<String,String> entityMap;

	/**
	 * Gets the entity map.
	 *
	 * @return the entity map
	 */
	public Map<String, String> getEntityMap() {
		if(entityMap == null) {
			entityMap = new HashMap<String, String>();
		}
		return entityMap;
	}

	/**
	 * Sets the entity map.
	 *
	 * @param entityMap the entity map
	 */
	public void setEntityMap(Map<String, String> entityMap) {
		this.entityMap = entityMap;
	}

	/** The sequence. */
	private String sequence;

	/**
	 * Gets the sequence.
	 *
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * Sets the sequence.
	 *
	 * @param sequence the new sequence
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/** The service id. */
	private String serviceId;

	/**
	 * Gets the service id.
	 *
	 * @return the service id
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * Sets the service id.
	 *
	 * @param serviceId the new service id
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(GroupParameter o1, GroupParameter o2) {
		if (o1.getSequence() != null && !o1.getSequence().trim().isEmpty()
				&& o2.getSequence() != null
				&& !o2.getSequence().trim().isEmpty()) {
			return Integer.parseInt(o1.getSequence()) - Integer.parseInt(o2.getSequence());
		}
		return 0;
	}

	/** The leg level. */
	private String legLevel;

	/**
	 * Gets the leg level.
	 *
	 * @return the leg level
	 */
	public String getLegLevel() {
		return legLevel;
	}

	/**
	 * Sets the leg level.
	 *
	 * @param legLevel the new leg level
	 */
	public void setLegLevel(String legLevel) {
		this.legLevel = legLevel;
	}

	/** The condition. */
	private String condition;

	/**
	 * Gets the condition.
	 *
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * Sets the condition.
	 *
	 * @param condition the new condition
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	 private boolean mergeFlag;

	public boolean isMergeFlag() {
		return mergeFlag;
	}

	public void setMergeFlag(boolean mergeFlag) {
		this.mergeFlag = mergeFlag;
	}


	 private String queryData;

	public String getQueryData() {
		return queryData;
	}

	public void setQueryData(String queryData) {
		this.queryData = queryData;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
