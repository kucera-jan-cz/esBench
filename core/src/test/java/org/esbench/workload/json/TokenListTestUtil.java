package org.esbench.workload.json;

import java.util.List;

import org.esbench.generator.field.meta.TokenList;
import org.esbench.workload.json.databind.TokensIdGenerator;
import org.esbench.workload.json.databind.TokensIdResolver;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenListTestUtil {
	public static void registerTokens(ObjectMapper mapper, List<String> tokens) {
		TokenList list = new TokenList();
		list.setTokens(tokens);

		TokensIdGenerator generator = MapperFactory.getTokenIdGenerator(mapper);
		TokensIdResolver resolver = MapperFactory.getTokenIdResolver(mapper);
		Integer keyAsId = generator.generateId(list);
		IdKey id = generator.key(keyAsId);
		resolver.bindItem(id, list);
	}

	public static void registerTokens(ObjectMapper mapper, Integer id, List<String> tokens) {
		TokenList list = new TokenList();
		list.setTokens(tokens);

		TokensIdGenerator generator = MapperFactory.getTokenIdGenerator(mapper);
		TokensIdResolver resolver = MapperFactory.getTokenIdResolver(mapper);

		if(!generator.isRegistered(id)) {
			generator.register(id, list);
		}
		IdKey idKey = generator.key(id);
		if(!resolver.isRegistered(idKey)) {
			resolver.bindItem(idKey, list);
		}
	}
}
