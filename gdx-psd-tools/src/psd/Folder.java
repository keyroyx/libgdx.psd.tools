package psd;

import java.util.ArrayList;
import java.util.List;

import psd.utils.ElementFilter;
import psd.utils.ElementNameFilter;

/**
 * PSD 的文件夹图层
 * 
 * @author roy
 */
public class Folder extends Element {
	// 子对象
	public List<Element> childs = new ArrayList<Element>();

	// 过滤元素
	public final List<Element> filter(ElementFilter filter) {
		List<Element> list = new ArrayList<Element>();
		filter(this, filter, list);
		return list;
	}

	public final Element get(String layerName) {
		return get(new ElementNameFilter(layerName));
	}

	// 获取一个
	public final Element get(ElementFilter filter) {
		return filterOne(this, filter);
	}

	// 过滤 函数
	private static final void filter(Element element, ElementFilter filter, List<Element> out) {
		if (filter.accept(element)) {
			out.add(element);
		}
		if (element instanceof Folder) {
			Folder folder = (Folder) element;
			for (Element child : folder.childs) {
				filter(child, filter, out);
			}
		}
	}

	//
	private static final Element filterOne(Element element, ElementFilter filter) {
		if (filter.accept(element)) {
			return element;
		}
		if (element instanceof Folder) {
			Element rt = null;
			Folder folder = (Folder) element;
			for (Element child : folder.childs) {
				rt = filterOne(child, filter);
				if (rt != null) {
					return rt;
				}
			}
		}
		return null;
	}
}
