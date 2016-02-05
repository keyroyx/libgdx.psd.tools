package gdx.keyroy.psd.tools.models;

import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.PSDUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import library.psd.Layer;
import library.psd.Psd;

import com.keyroy.util.json.JsonAn;

// PSD 文件的描述
public class PSDData {
	// PSD 文件的地址
	protected String filePath;
	// 参数
	protected List<LayerParam> params;

	// PSD 的缓存文件
	@JsonAn(skip = true)
	protected Psd cache;
	@JsonAn(skip = true)
	protected HashMap<String, List<LayerParam>> paramCache;

	public PSDData() {

	}

	public PSDData(File file) {
		this.filePath = file.getPath();
	}

	// 添加一个参数
	public final void addParam(LayerParam layerParam) {
		if (params == null) {
			params = new ArrayList<LayerParam>();
		}
		params.add(layerParam);
		updateParamCache();
	}

	// 删除一个参数
	public final boolean removeParam(LayerParam layerParam) {
		if (params != null) {
			params.remove(layerParam);
			updateParamCache();
			return true;
		}
		return false;
	}

	protected final void updateParamCache() {
		if (paramCache == null) {
			paramCache = new HashMap<String, List<LayerParam>>();
		}
		paramCache.clear();
		for (LayerParam param : getLayerParams()) {
			List<LayerParam> list = paramCache.get(param.getLayerId());
			if (list == null) {
				list = new ArrayList<LayerParam>(3);
				paramCache.put(param.getLayerId(), list);
			}
			list.add(param);
		}
	}

	public List<LayerParam> getLayerParams() {
		if (params == null) {
			params = new ArrayList<LayerParam>();
		}
		return params;
	}

	public final List<LayerParam> getLayerParams(Layer layer) {
		if (paramCache == null) {
			updateParamCache();
		}
		return paramCache.get(PSDUtil.getLayerId(layer));
	}

	//
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String psdPath) {
		this.filePath = psdPath;
	}

	public final String getFileName() {
		return new File(filePath).getName().replace(".psd", "");
	}

	public Psd getCache() {
		if (cache == null) {
			try {
				cache = new Psd(new File(filePath));
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, filePath, L.get("error.parse_psd_file_failed"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return cache;
	}

	public void setCache(Psd cache) {
		this.cache = cache;
	}

}
