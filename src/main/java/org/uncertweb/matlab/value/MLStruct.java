package org.uncertweb.matlab.value;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Joiner;

public class MLStruct extends MLValue {

	private final Map<String, MLValue> struct;
	
	/**
	 * Creates a new <code>MLStruct</code> instance.
	 * 
	 */
	public MLStruct() {
		this.struct = new HashMap<String, MLValue>();
	}
	
	public void setField(String field, MLValue value) {
		struct.put(field, value);
	}
	
	public MLValue getField(String field) {
		return struct.get(field);
	}
	
	public Map<String, MLValue> getStruct() {
		return struct;
	}
	
	@Override
	public String toMLString() {
		StringBuilder sb = new StringBuilder();
		sb.append("struct(");
		for (String field : struct.keySet()) {
			sb.append('\'').append(field).append('\'');
			sb.append(',');
			sb.append(struct.get(field).toMLString());
			sb.append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(')');
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("struct: ");
        Joiner.on(",").appendTo(sb, struct.keySet());
		return sb.toString();
	}
	
}
