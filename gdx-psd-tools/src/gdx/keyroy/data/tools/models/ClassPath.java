package gdx.keyroy.data.tools.models;

import java.util.ArrayList;
import java.util.List;

import com.keyroy.util.json.JsonAn;

//类 文件的描述
public class ClassPath {
	// class 的名称
	protected String className;

	// 元素
	@JsonAn(skip = true)
	protected List<ClassElement> elements;

	@JsonAn(skip = true)
	protected Class<?> clazz;

	public ClassPath() {

	}

	public ClassPath(Class<?> clazz) {
		this.className = clazz.getName();
	}

	// 类名
	public String getClassName() {
		return className;
	}

	public final boolean has(String id) {
		if (elements != null) {
			for (ClassElement element : elements) {
				if (element.getObjId().equals(id)) {
					return true;
				}
			}
		}
		return false;
	}

	public Class<?> getClazz() {
		if (clazz == null) {
			try {
				clazz = Class.forName(className);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return clazz;
	}

	public void addElement(ClassElement classElement) {
		if (elements == null) {
			elements = new ArrayList<ClassElement>();
		}
		if (elements.contains(classElement) == false) {
			elements.add(classElement);
			classElement.parent = this;
		}
	}

	public void delElement(ClassElement classElement) {
		if (elements != null) {
			elements.remove(classElement);
		}
	}

	public void setElements(List<ClassElement> elements) {
		this.elements = elements;
		// 检查父类是否被设置
		for (ClassElement element : this.elements) {
			if (element.parent == null) {
				element.parent = this;
			}
		}
	}

	public List<ClassElement> getElements() {
		if (elements == null) {
			elements = new ArrayList<ClassElement>();
		}
		// 检查父类是否被设置
		for (ClassElement element : elements) {
			if (element.parent == null) {
				element.parent = this;
			}
		}

		return elements;
	}

}
