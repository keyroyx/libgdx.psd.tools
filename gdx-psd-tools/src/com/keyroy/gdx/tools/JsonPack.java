package com.keyroy.gdx.tools;

import org.json.m.JSONArray;

public class JsonPack {
	private String name;
	private JSONArray jsonArray;

	public JsonPack(String name, JSONArray jsonArray) {
		this.name = name;
		this.jsonArray = jsonArray;
	}

	public String getName() {
		return name;
	}

	public JSONArray getJsonArray() {
		return jsonArray;
	}
}
