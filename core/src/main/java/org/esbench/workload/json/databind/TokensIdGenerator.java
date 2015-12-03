package org.esbench.workload.json.databind;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;

public class TokensIdGenerator extends ObjectIdGenerator<Integer> {
	private static final long serialVersionUID = 1L;
	protected final Class<?> _scope;
	protected transient int _nextValue;
	private transient Map<Integer, Object> objectMap = new HashMap<>();

	public TokensIdGenerator() {
		this(Object.class, 0);
	}

	public TokensIdGenerator(Class<?> scope, int fv) {
		this._scope = scope;
		_nextValue = fv;
	}

	@Override
	public ObjectIdGenerator<Integer> forScope(Class<?> scope) {
		return this;
	}

	@Override
	public ObjectIdGenerator<Integer> newForSerialization(Object context) {
		return this;
	}

	@Override
	public IdKey key(Object key) {
		return new IdKey(getClass(), _scope, key);
	}

	@Override
	public Integer generateId(Object forPojo) {
		int id = _nextValue;
		objectMap.put(id, forPojo);
		++_nextValue;
		return id;
	}

	public void register(Integer id, Object value) {
		objectMap.put(id, value);
	}

	public boolean isRegistered(Integer id) {
		return objectMap.containsKey(id);
	}

	@Override
	public Class<?> getScope() {
		return _scope;
	}

	@Override
	public boolean canUseFor(ObjectIdGenerator<?> gen) {
		return (gen.getClass() == getClass()) && (gen.getScope() == _scope);
	}

	public Map<Integer, Object> getObjectMap() {
		return new HashMap<>(this.objectMap);
	}

}
