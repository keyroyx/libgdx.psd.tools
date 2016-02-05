package gdx.keyroy.psd.tools.models;

public class KeyVal {
	protected String key;
	protected String val;

	public KeyVal() {
	}

	public KeyVal(String key) {
		this.key = key;
	}

	public final String getId(ParamData paramData) {
		return paramData.getFileName() + "." + key;
	}

	// 是否为选项模式
	public final String[] getSelection() {
		try {
			if (val != null && val.startsWith("[") && val.endsWith("]")) {
				String text = val.substring(1, val.length() - 1);
				return text.split(",");
			}
		} catch (Exception e) {
		}
		return null;
	}

	//

	public void setKey(String key) {
		this.key = key;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getKey() {
		return key;
	}

	public String getVal() {
		return val;
	}

	@Override
	public String toString() {
		return key + "=" + val;
	}
}
