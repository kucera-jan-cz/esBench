package org.esbench.elastic.stats;

import static org.esbench.cmd.CommandPropsConstants.INDEX_OPT;
import static org.esbench.cmd.CommandPropsConstants.TYPE_OPT;
import static org.esbench.cmd.CommandPropsConstants.WORKLOAD_OPT;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.esbench.cmd.EsBenchAction;
import org.esbench.core.DefaultProperties;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.workload.Workload;
import org.esbench.workload.WorkloadConstants;
import org.esbench.workload.json.WorkloadParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectWorkloadAction implements EsBenchAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectWorkloadAction.class);
	private static final String SEPARATOR = ",";

	private final StatsCollector collector;
	private final WorkloadParser parser;

	public CollectWorkloadAction(StatsCollector collector, WorkloadParser parser) {
		this.collector = collector;
		this.parser = parser;
	}

	@Override
	public void perform(DefaultProperties props) throws IOException {
		String[] indices = props.getProperty(INDEX_OPT).split(SEPARATOR);
		String[] types = props.get(TYPE_OPT, StringUtils.EMPTY).split(SEPARATOR);

		List<IndexTypeMetadata> workloads = new ArrayList<>();
		for(String indexName : indices) {
			workloads.addAll(collector.collectIndex(indexName));
		}
		if(types.length > 0) {
			workloads = workloads.stream().filter(w -> ArrayUtils.contains(types, w.getTypeName())).collect(Collectors.toList());
			String errMsg = String.format("None of defined types %s does not existing in index", ArrayUtils.toString(types));
			Validate.notEmpty(workloads, errMsg);
		}

		Path workloadFilePath = Paths.get(props.getProperty(WORKLOAD_OPT));
		Files.createDirectories(workloadFilePath.getParent());
		try(Writer writer = new FileWriter(workloadFilePath.toFile(), false)) {

			Workload config = new Workload(WorkloadConstants.CURRENT_VERSION, MetadataConstants.DEFAULT_META_BY_TYPE, workloads);
			parser.parse(writer, config);
			LOGGER.info("Workload sucessfully write to {}", workloadFilePath);
		}
	}

}
