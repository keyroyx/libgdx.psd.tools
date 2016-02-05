package gdx.keyroy.psd.tools.widgets;

import javax.swing.tree.DefaultMutableTreeNode;

import library.psd.Layer;
import library.psd.LayersContainer;
import library.psd.Psd;

@SuppressWarnings("serial")
public class PSDLayerTreeNode extends DefaultMutableTreeNode {
	private Psd psd;
	private Layer layer;

	public PSDLayerTreeNode() {
		setAllowsChildren(true);
	}

	public PSDLayerTreeNode(Layer layer) {
		this();
		this.layer = layer;
		setUserObject(layer.getName());
		addChilds(layer);
	}

	public PSDLayerTreeNode(Psd psd) {
		this();
		this.psd = psd;
		setUserObject(psd.getName());
		addChilds(psd);
	}

	public void setPsd(Psd psd) {
		this.psd = psd;
		removeAllChildren();
		addChilds(psd);
	}

	public final Psd getPsd() {
		return psd;
	}

	public final Layer getLayer() {
		return layer;
	}

	public final Object getObject() {
		if (psd != null) {
			return psd;
		} else if (layer != null) {
			return layer;
		} else {
			return null;
		}
	}

	protected final void addChilds(LayersContainer container) {
		for (int i = 0; i < container.getLayersCount(); i++) {
			Layer layer = container.getLayer(i);
			PSDLayerTreeNode treeNode = new PSDLayerTreeNode(layer);
			add(treeNode);
		}
	}

}
