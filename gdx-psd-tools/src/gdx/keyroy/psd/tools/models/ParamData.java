package gdx.keyroy.psd.tools.models;

import gdx.keyroy.psd.tools.util.FileUtil;

import java.io.File;
import java.util.List;

import com.keyroy.util.json.JsonAn;

//参数 文件的描述
public class ParamData {
	// 文件的地址
	protected String filePath;

	@JsonAn(skip = true)
	protected List<KeyVal> cache;

	public ParamData() {

	}

	public ParamData(File file) {
		this.filePath = file.getPath();

	}

	// 文件的地址
	public String getFilePath() {
		return filePath;
	}

	// 文件的地址
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public final void cleanCache(){
		cache = null;
	}
	
	public final List<KeyVal> getCache() {
		if (cache == null) {
			cache = FileUtil.readIni(new File(filePath));
		}
		return cache;
	}

	public final String getFileName() {
		return new File(filePath).getName().replace(".ini", "");
	}

}
