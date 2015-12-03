package org.esbench.elastic.sender;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.lang3.StringUtils;
import org.esbench.generator.document.DocumentFactory;
import org.esbench.generator.document.simple.SimpleDocumentFactory;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.workload.Workload;
import org.esbench.workload.json.WorkloadParser;

public abstract class AbstractInsertAction {

	protected final DocumentFactory<String> getFactory(InsertProperties insProperties) throws IOException {
		Reader reader = new FileReader(insProperties.getWorkloadLocation());
		return getFactory(insProperties, reader);
	}

	protected final DocumentFactory<String> getFactory(InsertProperties insProperties, Reader reader) throws IOException {
		Workload workload = new WorkloadParser().parse(reader);
		IndexTypeMetadata indexTypeMetadata = getIndexType(insProperties, workload);
		DocumentFactory<String> factory = new SimpleDocumentFactory(indexTypeMetadata, insProperties.getFieldCacheLimit());
		return factory;
	}

	protected final IndexTypeMetadata getIndexType(InsertProperties properties, Workload configuration) {
		if(configuration.getIndiceTypes().size() == 1) {
			return configuration.getIndiceTypes().get(0);
		}
		String indexName = properties.getWorkloadIndex();
		String type = properties.getWorkloadType();
		for(IndexTypeMetadata meta : configuration.getIndiceTypes()) {
			if(!indexName.equals(meta.getIndexName())) {
				continue;
			}
			if(StringUtils.isEmpty(type) || type.equals(meta.getTypeName())) {
				return meta;
			}
		}
		String msg = String.format("Can't locate index %s and type %s in configuration", indexName, type);
		throw new IllegalArgumentException(msg);
	}
}
