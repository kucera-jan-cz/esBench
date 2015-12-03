package org.esbench.workload.json.databind;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;

public class TokensIdResolver extends SimpleObjectIdResolver {
	@Override
	public ObjectIdResolver newForDeserialization(Object context) {
		return this;
	}

	public boolean isRegistered(IdKey id) {
		return getItemMap().containsKey(id);
	}

	public Set<Entry<IdKey, Object>> getItems() {
		return getItemMap().entrySet();
	}

	public Map<IdKey, Object> getItemMap() {
		if(_items == null) {
			return Collections.emptyMap();
		} else {
			return _items;
		}
	}
}
