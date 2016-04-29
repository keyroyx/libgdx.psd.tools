package psd;

import java.util.List;

public interface ParamProvider {

	void setParams(List<Param> params);

	List<Param> getParams();

	public Param getParam(String key);
}
