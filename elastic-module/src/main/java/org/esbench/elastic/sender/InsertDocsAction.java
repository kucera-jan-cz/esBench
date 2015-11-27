package org.esbench.elastic.sender;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.esbench.cmd.ElasticClientBuilder;
import org.esbench.cmd.EsBenchAction;
import org.esbench.core.DefaultProperties;
import org.esbench.generator.document.simple.SimpleDocumentFactory;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.workload.Workload;
import org.esbench.workload.json.WorkloadParser;

/**
 * Action for inserting docs to Elasticsearch.
 */
public class InsertDocsAction implements EsBenchAction {

	/**
	 * Based on given props perform docs inserting. 
	 * @param props holding information properties for establish ClientSender and other components necessary for inserting.
	 * @throws IOException when sending or configuration loading fails for any reason
	 */
	@Override
	public void perform(DefaultProperties props) throws IOException {
		Client client = new ElasticClientBuilder().withProperties(props).build();

		InsertProperties insProperties = new InsertProperties(props);

		WorkloadParser parser = new WorkloadParser();
		Reader reader = new FileReader(insProperties.getWorkloadLocation());
		Workload configuration = parser.parse(reader);
		IndexTypeMetadata indexType = getIndexType(insProperties, configuration);
		ClientSender sender = new ClientSender(client);
		SimpleDocumentFactory factory = new SimpleDocumentFactory(indexType);
		sender.send(factory, insProperties);
	}

	IndexTypeMetadata getIndexType(InsertProperties properties, Workload configuration) {
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
