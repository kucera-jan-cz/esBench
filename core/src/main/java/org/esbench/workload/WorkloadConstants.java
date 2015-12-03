package org.esbench.workload;

/**
 * Constant class representing allowed fields in workload JSON format.
 */
public final class WorkloadConstants {
	// Versions
	public static final String CURRENT_VERSION = "0.1";
	// Global
	public static final String VERSION_PROP = "version";
	public static final String DEFAULTS_PROP = "defaults";
	public static final String REFS_PROP = "token_references";

	// Index related
	public static final String INDEX_PROP = "index";
	public static final String FIELDS_PROP = "fields";
	public static final String HISTOGRAM_PROP = "histogram";

	// Generic properties
	public static final String NAME_PROP = "name";
	public static final String TYPE_PROP = "type";
	public static final String ARRAY_PROP = "array";
	public static final String STRATEGY_PROP = "strategy";

	// Numeric and date properties
	public static final String FROM_PROP = "from";
	public static final String TO_PROP = "to";
	public static final String STEP_PROP = "step";
	public static final String PATTERN_PROP = "pattern";

	// String properties
	public static final String TOKENS_PROP = "tokens";
	public static final String WORDS_PROP = "words";
	public static final String REF_ID_PROP = "@id";

	// IP properties
	public static final String CIDR_PROP = "cidr";

	private WorkloadConstants() {

	}
}
