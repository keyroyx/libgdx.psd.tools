package psd;

import org.json.m.JSONObject;

public class Param {
	// 参数ID
	protected String id;
	// 参数内容
	protected JSONObject json;

	public Param() {
	}

	public Param(String text) {
		setText(text);
	}

	public final void setText(String text) {
		int st, ed;
		if ((st = text.indexOf("{")) != -1 && (ed = text.lastIndexOf("}")) != -1) {
			this.id = text.substring(0, st);
			this.json = new JSONObject(text.substring(st, ed + 1));
		} else {
			this.id = text;
		}
	}

	// 获取参数的 ID
	public final String getId() {
		return id;
	}

	// 获取 JSON 参数对象
	public final JSONObject getJson() {
		return json;
	}

	//
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("@");
		buffer.append(id);
		if (json != null) {
			buffer.append(json.toString());
		}
		return buffer.toString();
	}

	public static void main(String[] args) {
		String src = "id{\"p\":123,\"p2\":\"aljdlwadlajw dwajlkajwd a\"}";
		Param param = new Param(src);
		System.out.println(param.toString());
	}
}
