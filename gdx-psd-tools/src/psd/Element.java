package psd;

import java.util.List;

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
	// 绑定参数
	public List<Param> params;
	// 是否显示
	public boolean isVisible;
	// 自定义对象
	@JsonAn(skip = true)
	protected Object userObject;

	//
	public final void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	//
	@SuppressWarnings("unchecked")
	public final <T> T getUserObject() {
		return (T) userObject;
	}
}
