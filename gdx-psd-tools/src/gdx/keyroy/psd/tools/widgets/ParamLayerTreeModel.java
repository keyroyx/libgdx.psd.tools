package gdx.keyroy.psd.tools.widgets;

import gdx.keyroy.psd.tools.models.KeyVal;
import gdx.keyroy.psd.tools.models.ParamData;

import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ParamLayerTreeModel implements TreeModel {
	private LinkedList<TreeModelListener> listeners = new LinkedList<TreeModelListener>();
	private List<ParamData> paramDatas;

	public ParamLayerTreeModel(List<ParamData> paramDatas) {
		this.paramDatas = paramDatas;
	}

	public void setParams(List<ParamData> paramDatas) {
		this.paramDatas = paramDatas;
		if (!listeners.isEmpty()) {
			TreeModelEvent event = new TreeModelEvent(this, new TreePath(paramDatas));
			for (TreeModelListener l : listeners) {
				l.treeStructureChanged(event);
			}
		}
	}

	@Override
	public Object getRoot() {
		if (paramDatas != null) {
			return paramDatas;
		}
		return null;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent.equals(paramDatas)) {
			return paramDatas.get(index);
		} else if (parent instanceof ParamData) {
			ParamData paramData = (ParamData) parent;
			return paramData.getCache().get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent.equals(paramDatas)) {
			return paramDatas.size();
		} else if (parent instanceof ParamData) {
			ParamData paramData = (ParamData) parent;
			return paramData.getCache().size();
		}
		return 0;
	}

	@Override
	public boolean isLeaf(Object node) {
		return node instanceof KeyVal;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {

	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent.equals(paramDatas)) {
			return paramDatas.indexOf(child);
		} else if (parent instanceof ParamData) {
			ParamData paramData = (ParamData) parent;
			return paramData.getCache().indexOf(child);
		}
		return 0;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

}
