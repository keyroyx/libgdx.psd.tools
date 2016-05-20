package psd;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.keyroy.util.json.JsonAn;

/**
 * PSD 元素
 * 
 * @author roy
 */
public class Element {
	// 图层名称
	public String layerName;
	// 坐标 , 大小
	public int x, y, width, height;
	// // 去掉参数以后 , 剩余的名称
	// public String name;
	// 绑定参数
	public List<Param> params;
	// 是否显示
	public boolean isVisible;
	// 父类对象
	@JsonAn(skip = true)
	protected Folder parent;
	// 文件对象
	@JsonAn(skip = true)
	protected PsdFile psdFile;

	// 自定义对象
	@JsonAn(skip = true)
	protected Object userObject;
	// 自定义对象
	// @JsonAn(skip = true)
	// protected Actor actor;

	// 更新参数
	protected void updateParam() {
		if (layerName != null) {
			int idx = layerName.indexOf("@");
			if (idx != -1) {
				String src = layerName;
				if (idx == 0) {
				} else {
					layerName = src.substring(0, idx);
					src = src.substring(idx + 1);
				}
				String[] ps = src.split("@");
				params = new ArrayList<Param>(ps.length);
				for (String param : ps) {
					onParam(param);
				}
			}
		}
	}

	protected void onParam(String paramString) {
		try {
			params.add(new Param(paramString));
		} catch (Exception e) {
			System.out.println("path : " + layerName);
			params.add(new Param(paramString));
		}
	}

	// 设置 文件夹
	protected void updateParent(Folder parent) {
		this.parent = parent;
	}

	// 设置 文件夹
	protected void updatePsdFile(PsdFile psdFile) {
		this.psdFile = psdFile;
	}

	// 设置用户自定义的缓存数据
	public final void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	//
	@SuppressWarnings("unchecked")
	public final <T> T getUserObject() {
		return (T) userObject;
	}

	public final PsdFile getPsdFile() {
		return psdFile;
	}

	// 获取 父文件夹
	public final Folder getParent() {
		return parent;
	}

	public final List<Param> getParams() {
		return params;
	}

	// 获取当前的路径
	public final String getPath() {
		Array<String> paths = new Array<String>();
		paths.add(layerName);
		Folder folder = parent;
		while (folder != null) {
			if (folder.layerName != null) {
				paths.add(folder.layerName);
			} else if (folder instanceof PsdFile) {
				paths.add(((PsdFile) folder).psdName);
			}
			folder = folder.parent;
		}

		StringBuffer buffer = new StringBuffer();
		for (int i = paths.size - 1; i >= 0; i--) {
			String name = paths.get(i);
			buffer.append(name);
			if (name.equals(layerName) == false) {
				buffer.append("/");
			}
		}
		return buffer.toString();
	}

	@Override
	public String toString() {
		return getPath();
	}

	public static void main(String[] args) {
		Element element = new Element();
		element.layerName = "test@p{\"firstName\":\"John\" , \"lastName\":\"Doe\"}@p2{\"first\":\"1\" , \"last\":\"2\"}";
		element.updateParam();
		System.out.println(element.params);
	}

}
