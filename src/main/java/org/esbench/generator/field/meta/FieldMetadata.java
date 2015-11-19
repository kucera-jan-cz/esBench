package org.esbench.generator.field.meta;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.esbench.config.ConfigurationConstants;
import org.esbench.config.json.databind.FieldMetadataTypeIdResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = ConfigurationConstants.TYPE_PROP, visible = true)
@JsonTypeIdResolver(FieldMetadataTypeIdResolver.class)
@JsonPropertyOrder(value = { ConfigurationConstants.TYPE_PROP, ConfigurationConstants.ARRAY_PROP }, alphabetic = true)
public abstract class FieldMetadata {
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldMetadata.class);
	@JsonIgnore
	private String name;
	@JsonIgnore
	private String fullPath;
	@JsonProperty(value = ConfigurationConstants.TYPE_PROP)
	private MetaType metaType;
	@JsonProperty(value = ConfigurationConstants.ARRAY_PROP)
	private Integer valuesPerDocument;
	private Strategy strategy = Strategy.SEQUENCE;

	public FieldMetadata() {
	}

	public FieldMetadata(String fullPath, MetaType metaType, int valuesPerDoc) {
		Validate.notNull(fullPath);
		setFullPath(fullPath);
		this.metaType = metaType;
		this.valuesPerDocument = valuesPerDoc;
	}

	public String getName() {
		return name;
	}

	@JsonIgnore
	public String getFullPath() {
		return fullPath;
	}

	@JsonIgnore
	public MetaType getMetaType() {
		return metaType;
	}

	public Integer getValuesPerDocument() {
		return valuesPerDocument;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	void setName(String name) {
		this.name = name;
	}

	@JsonProperty(value = ConfigurationConstants.NAME_PROP)
	public void setFullPath(String value) {
		if(value == null) {
			LOGGER.warn("Trying to set null fullpath");
			return;
		}
		this.fullPath = value;
		this.name = fullPath.substring(fullPath.lastIndexOf('.') + 1);
	}

	public void setMetaType(MetaType metaType) {
		this.metaType = metaType;
	}

	public void setValuesPerDocument(Integer valuesPerDocument) {
		this.valuesPerDocument = valuesPerDocument;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
