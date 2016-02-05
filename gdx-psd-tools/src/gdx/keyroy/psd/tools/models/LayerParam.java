package gdx.keyroy.psd.tools.models;

public class LayerParam {
	// 图层id
	protected String layerId;
	// 参数ID
	protected String paramId;
	// 参数内容
	protected String data;

	public LayerParam() {
	}

	public LayerParam(LayerParam param) {
		this.layerId = param.layerId;
		this.paramId = param.paramId;
		this.data = param.data;
	}

	public String getLayerId() {
		return layerId;
	}

	public void setLayerId(String layerId) {
		this.layerId = layerId;
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
