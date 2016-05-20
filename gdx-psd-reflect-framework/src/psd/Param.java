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
			String jsonStr = text.substring(st, ed + 1);
			try {
				this.json = new JSONObject(jsonStr);
			} catch (Exception e) {// 错误的字符串尝试新的解析方式
				jsonStr = jsonStr.replace("{", "").replace("}", "");
				this.json = new JSONObject();
				try {
					for (String keyVal : jsonStr.split(",")) {
						String[] sp = keyVal.split(":");
						if (sp.length == 2) {
							json.put(sp[0], sp[1]);
						}
					}
				} catch (Exception e2) {
				}
			}
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
