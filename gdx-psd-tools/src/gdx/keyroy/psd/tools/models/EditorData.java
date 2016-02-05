package gdx.keyroy.psd.tools.models;

import gdx.keyroy.psd.tools.util.FileUtil;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.Message;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.keyroy.util.json.Json;
import com.keyroy.util.json.JsonAn;

//编辑器的数据
public class EditorData {
	@JsonAn(skip = true)
	private static int hashCode;
	// 当前的数据项
	private static List<PSDData> psdDatas = new ArrayList<PSDData>();
	// 当前的数据项
	private static List<ParamData> paramDatas = new ArrayList<ParamData>();

	// 获取当前 PSD 文件列表
	public static List<PSDData> getPsdDatas() {
		return psdDatas;
	}

	public static final KeyVal getKeyVal(LayerParam layerParam) {
		for (ParamData paramData : getParamDatas()) {
			for (KeyVal keyVal : paramData.getCache()) {
				if (keyVal.getId(paramData).equals(layerParam.getParamId())) {
					return keyVal;
				}
			}
		}
		return null;
	}

	public static List<ParamData> getParamDatas() {
		return paramDatas;
	}

	public static final void verify(File psdFile) {
		verify(psdFile, true);
	}

	// 校验 和 添加 PSD 或者 Param 文件
	public static final void verify(File psdFile, boolean autoSave) {
		if (psdFile != null && psdFile.exists()) {
			addFile(psdFile);
			if (autoSave) {
				save();
			}
		}
	}

	// 添加文件
	private static final void addFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File cFile : files) {
					addFile(cFile);
				}
			}
		} else if (file.getName().toString().endsWith(".psd")) {
			for (PSDData data : psdDatas) {
				if (data.getFilePath().equals(file.getPath())) {
					return;
				}
			}
			try {
				PSDData data = new PSDData(file);
				psdDatas.add(data);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, file.getPath(), L.get("error.parse_psd_file_failed"),
						JOptionPane.ERROR_MESSAGE);
			}

		} else if (file.getName().toString().toLowerCase().endsWith(".ini")) {
			for (ParamData data : paramDatas) {
				if (data.getFilePath().equals(file.getPath())) {
					return;
				}
			}
			try {
				ParamData data = new ParamData(file);
				paramDatas.add(data);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, file.getPath(), L.get("error.parse_ini_file_failed"),
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	/**
	 * 
	 * 
	 * 
	 */
	public static final void load() {
		try {
			File file = getFile();
			if (file.exists()) {
				FileInputStream inputStream = new FileInputStream(file);
				Json json = new Json(inputStream);
				inputStream.close();
				json.toObject(EditorData.class);
				//
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void save() {
		try {
			Json json = new Json(new EditorData());
			String text = json.toString();
			if (text.hashCode() != hashCode) {
				hashCode = text.hashCode();
				System.out.println("save data");
				FileUtil.save(getFile(), text);

				Message.send(L.get("Message.data_save") + "    " + new Date().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final File getFile() {
		return new File(EditorData.class.getSimpleName());
	}
}
