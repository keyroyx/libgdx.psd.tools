package psd;

import gdx.keyroy.psd.tools.models.LayerParam;

public class Param {
	// 参数ID
	protected String paramId;
	// 参数内容
	protected String data;

	public Param() {
	}

	public Param(LayerParam param) {
		this.paramId = param.getParamId();
		this.data = param.getData();
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
