package psd;

public class Param {
	// 参数ID
	protected String paramId;
	// 参数内容
	protected String data;

	public Param() {
	}

	public Param(String paramId, String data) {
		this.paramId = paramId;
		this.data = data;
	}

	public String getParamId() {
		return paramId;
	}

	public void setParamId(String paramId) {
		this.paramId = paramId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		if (data == null) {
			return paramId;
		} else {
			return paramId + " [" + data + "]";
		}
	}

}
