
package com.excelacom.century.apolloneoutbound.bean;

import java.util.Comparator;
import java.util.List;


/**
 * The Class Parameter.
 */
public class Parameter implements Cloneable, Comparator<Parameter> {

    /** The name. */
    private String name;

    /** The name. */
    private String description;

    /** The value. */
    private String value;

    /** The table name. */
    private String tableName;

    /** The field name. */
    private String fieldName;

    /** The schema name. */
    private String schemaName;

    /** The rule. */
    private String rule;

    /** The value type. */
    private String valueType;

    /** The editable. */
    private String editable;

    /** The sub type. */
    private String subType;

    /** The column name. */
    private String columnName;

    /** The value list. */
    private List<String> valueList;

    /** The table type. */
    private String tableType;

    /** The property. */
    private String property;

    /** The display name. */
    private String displayName;

    /** The sequence id. */
    private String sequenceId;

    /** The min length. */
    private String minLength;

	/** The max length. */
    private String maxLength;

    /** The conditional. */
    private String conditional;

    /** The parameter type. */
    private String parameterType;

    /** The content id. */
    private String content_id;

    /**
     * Gets the content id.
     *
     * @return the content id
     */
    public String getContent_id() {
		return content_id;
	}

	/**
	 * Sets the content id.
	 *
	 * @param contentId the new content id
	 */
	public void setContent_id(String contentId) {
		content_id = contentId;
	}

	/** The param values. */
	private List<ParameterValue> paramValues;


    /** The apiname. */
    private String apiname;

    /**
     * Gets the apiname.
     *
     * @return the apiname
     */
    public String getApiname() {
		return apiname;
	}

	/**
	 * Sets the apiname.
	 *
	 * @param apiname the new apiname
	 */
	public void setApiname(String apiname) {
		this.apiname = apiname;
	}

	/** The lookup type. */
	private String lookup_type;

    /**
     * Gets the lookup type.
     *
     * @return the lookup type
     */
    public String getLookup_type() {
		return lookup_type;
	}

	/**
	 * Sets the lookup type.
	 *
	 * @param lookupType the new lookup type
	 */
	public void setLookup_type(String lookupType) {
		lookup_type = lookupType;
	}

	/**
	 * Gets the param values.
	 *
	 * @return the param values
	 */
	public List<ParameterValue> getParamValues() {
		return paramValues;
	}

	/**
	 * Sets the param values.
	 *
	 * @param paramValues the new param values
	 */
	public void setParamValues(List<ParameterValue> paramValues) {
		this.paramValues = paramValues;
	}

	/**
	 * Gets the parameter type.
	 *
	 * @return the parameter type
	 */
	public String getParameterType() {
		return parameterType;
	}

	/**
	 * Sets the parameter type.
	 *
	 * @param parameterType the new parameter type
	 */
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone()throws CloneNotSupportedException{
    	return super.clone();
    	}

    /**
     * Gets the rule.
     *
     * @return the rule
     */
    public final String getRule() {
        return rule;
    }

    /**
     * Sets the rule.
     *
     * @param rule the new rule
     */
    public final void setRule(final String rule) {
        this.rule = rule;
    }

    /**
     * Gets the value type.
     *
     * @return the value type
     */
    public final String getValueType() {
        return valueType;
    }

    /**
     * Sets the value type.
     *
     * @param valueType the new value type
     */
    public final void setValueType(final String valueType) {
        this.valueType = valueType;
    }

    /**
     * Gets the editable.
     *
     * @return the editable
     */
    public final String getEditable() {
        return editable;
    }

    /**
     * Sets the editable.
     *
     * @param editable the new editable
     */
    public final void setEditable(final String editable) {
        this.editable = editable;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the table name.
     *
     * @return the table name
     */
    public final String getTableName() {
        return tableName;
    }

    /**
     * Sets the table name.
     *
     * @param tableName the new table name
     */
    public final void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    /**
     * Gets the field name.
     *
     * @return the field name
     */
    public final String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the field name.
     *
     * @param fieldName the new field name
     */
    public final void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name
     */
    public final String getSchemaName() {
        return schemaName;
    }

    /**
     * Sets the schema name.
     *
     * @param schemaName the new schema name
     */
    public final void setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * Gets the column name.
     *
     * @return the column name
     */
    public final String getColumnName() {
        return columnName;
    }

    /**
     * Sets the column name.
     *
     * @param columnName the new column name
     */
    public final void setColumnName(final String columnName) {
        this.columnName = columnName;
    }

    /**
     * Gets the property.
     *
     * @return the property
     */
    public final String getProperty() {
        return property;
    }

    /**
     * Sets the property.
     *
     * @param property the new property
     */
    public final void setProperty(final String property) {
        this.property = property;
    }

    /**
     * Gets the value list.
     *
     * @return the value list
     */
    public final List<String> getValueList() {
        return valueList;
    }

    /**
     * Sets the value list.
     *
     * @param valueList the new value list
     */
    public final void setValueList(final List<String> valueList) {
        this.valueList = valueList;
    }

    /**
     * Gets the table type.
     *
     * @return the table type
     */
    public final String getTableType() {
        return tableType;
    }

    /**
     * Sets the table type.
     *
     * @param tableType the new table type
     */
    public final void setTableType(final String tableType) {
        this.tableType = tableType;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public final String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName the new display name
     */
    public final void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

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

	/**
	 * Gets the sub type.
	 *
	 * @return the sub type
	 */
	public final String getSubType() {
		return subType;
	}

	/**
	 * Sets the sub type.
	 *
	 * @param subType the new sub type
	 */
	public final void setSubType(String subType) {
		this.subType = subType;
	}

	/**
	 * Gets the min length.
	 *
	 * @return the min length
	 */
	public final String getMinLength() {
		return minLength;
	}

	/**
	 * Sets the min length.
	 *
	 * @param minLength the new min length
	 */
	public final void setMinLength(String minLength) {
		this.minLength = minLength;
	}

	/**
	 * Gets the max length.
	 *
	 * @return the max length
	 */
	public final String getMaxLength() {
		return maxLength;
	}

	/**
	 * Sets the max length.
	 *
	 * @param maxLength the new max length
	 */
	public final void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * Gets the sequence id.
	 *
	 * @return the sequence id
	 */
	public String getSequenceId() {
		return sequenceId;
	}

	/**
	 * Sets the sequence id.
	 *
	 * @param sequenceId the new sequence id
	 */
	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	/**
	 * Gets the conditional.
	 *
	 * @return the conditional
	 */
	public String getConditional() {
		return conditional;
	}

	/**
	 * Sets the conditional.
	 *
	 * @param conditional the new conditional
	 */
	public void setConditional(String conditional) {
		this.conditional = conditional;
	}

	/** The query id. */
	private String queryId;

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

	/** The type. */
	private String type;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
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

	/** The selectedval list. */
	private List<String> selectedvalList;

	/**
	 * Gets the selectedval list.
	 *
	 * @return the selectedval list
	 */
	public List<String> getSelectedvalList() {
		return selectedvalList;
	}

	/**
	 * Sets the selectedval list.
	 *
	 * @param selectedvalList the new selectedval list
	 */
	public void setSelectedvalList(List<String> selectedvalList) {
		this.selectedvalList = selectedvalList;
	}

	/** The default value. */
	private String defaultValue;

	/**
	 * Gets the default value.
	 *
	 * @return the default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value.
	 *
	 * @param defaultValue the new default value
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Parameter o1, Parameter o2) {
		if (o1.getSequence() != null && !o1.getSequence().trim().isEmpty()
				&& o2.getSequence() != null
				&& !o2.getSequence().trim().isEmpty()) {
			return Integer.parseInt(o1.getSequence()) - Integer.parseInt(o2.getSequence());
		}
		return 0;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
