package gdx.keyroy.psd.tools.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import library.psd.Layer;
import library.psd.LayersContainer;
import library.psd.Psd;

public class PSDUtil {

	public static final Rectangle getMaxSize(Psd psd) {
		Rectangle rectangle = new Rectangle();
		for (Layer layer : psd.getLayers()) {
			getMaxSize(layer, rectangle);
		}
		return rectangle;
	}

	public static final void getMaxSize(Layer layer, Rectangle out) {
		LayerBoundary layerBoundary = LayerBoundary.getLayerBoundary(layer);
		out.setSize((int) Math.max(out.getWidth(), layerBoundary.right),
				(int) Math.max(out.getHeight(), layerBoundary.bottom));
	}

	public static final String getLayerId(Layer layer) {
		if (layer == null) {
			return null;
		}
		return Integer.toHexString(layer.getPath().hashCode());
	}

	public static final Layer getLayerById(LayersContainer container, String id) {
		if (id == null) {
			return null;
		} else {
			for (int i = 0; i < container.getLayersCount(); i++) {
				Layer layer = container.getLayer(i);
				if (id.equals(getLayerId(layer))) {
					return layer;
				}
				Layer rt = getLayerById(layer, id);
				if (rt != null) {
					return rt;
				}
			}
			return null;
		}
	}

	public static final void updateLayerParent(Psd psd) {
		updatePsdLayerPosition(psd);
	}

	public static final void updatePsdLayerPosition(Psd psd) {
		List<Layer> layers = psd.getLayers();
		for (Layer layer : layers) {
			updateParent(layer);
			updateLayerBoundary(layer);
			updateLayerPosition(layer);
			sortByY(layer);
		}
	}

	protected static final void updateParent(Layer parent) {
		for (Layer cLayer : parent.getLayers()) {
			cLayer.setParent(parent);
			updateParent(cLayer);
		}
	}

	protected static final LayerBoundary updateLayerBoundary(Layer layer) {
		if (layer.isFolder()) {
			LayerBoundary layerBoundary = null;
			for (Layer cLayer : layer.getLayers()) {
				if (layerBoundary == null) {
					layerBoundary = LayerBoundary.getLayerBoundary(updateLayerBoundary(cLayer));
				} else {
					layerBoundary.mix(updateLayerBoundary(cLayer));
				}
			}
			if (layerBoundary == null) {
				layerBoundary = new LayerBoundary(layer);
			}
			LayerBoundary.setLayerBoundary(layer, layerBoundary);
			return layerBoundary;
		} else {
			return LayerBoundary.setLayerBoundary(layer);
		}

	}

	protected static final void printChilds(Layer layer) {
		if (layer.getLayers() != null) {
			for (Layer cLayer : layer.getLayers()) {
				System.out.println(layer.getName() + " : " + cLayer.getName());
			}
		}
	}

	protected static final void sortByY(Layer layer) {
		// System.out.println();
		// System.out.println("sortByY : " + layer.getName() + " " +
		// layer.getAlpha());
		if (layer.getLayers() != null) {
			for (Layer cLayer : layer.getLayers()) {
				sortByY(cLayer);
			}
		}
	}

	protected static final void updateLayerPosition(Layer layer) {
		List<Layer> layers = getQueue(layer);
		for (int i = layers.size() - 1; i >= 0; i--) {
			Layer cLayer = layers.get(i);
			if (cLayer.getParent() != null) {
				LayerBoundary layerBoundary = LayerBoundary.getLayerBoundary(cLayer);
				LayerBoundary parentBoundary = LayerBoundary.getLayerBoundary(cLayer.getParent());
				if (layerBoundary != null && parentBoundary != null) {
					layerBoundary.setOffset(-parentBoundary.getLeft(), -parentBoundary.getTop());
				}
			}
		}
	}

	protected static final List<Layer> getQueue(Layer layer) {
		List<Layer> layers = new ArrayList<Layer>();
		queue(layer, layers);
		return layers;
	}

	protected static final void queue(Layer layer, List<Layer> layers) {
		layers.add(layer);
		if (layer.isFolder()) {
			for (Layer cLayer : layer.getLayers()) {
				queue(cLayer, layers);
			}
		}
	}

	public static class LayerBoundary {
		private int top = 0;
		private int left = 0;
		private int bottom = 0;
		private int right = 0;

		private LayerBoundary(Layer layer) {
			this.top = layer.getTop();
			this.left = layer.getLeft();
			this.bottom = layer.getBottom();
			this.right = layer.getRight();
		}

		private LayerBoundary(LayerBoundary boundary) {
			set(boundary);
		}

		public final void set(LayerBoundary boundary) {
			this.top = boundary.top;
			this.left = boundary.left;
			this.bottom = boundary.bottom;
			this.right = boundary.right;
		}

		public final void mix(LayerBoundary boundary) {
			if (boundary != null) {
				this.top = Math.min(top, boundary.top);
				this.left = Math.min(left, boundary.left);
				this.bottom = Math.max(bottom, boundary.bottom);
				this.right = Math.max(right, boundary.right);
			}
		}

		public int getX() {
			return left;
		}

		public int getY() {
			return top;
		}

		public int getWidth() {
			return right - left;
		}

		public int getHeight() {
			return bottom - top;
		}

		public final void setOffset(int ox, int oy) {
			setBoundary(left + ox, top + oy, right + ox, bottom + oy);
		}

		public final void setPosition(int x, int y) {
			int width = getWidth();
			int height = getHeight();
			setBoundary(x, y, x + width, y + height);
		}

		public final void setBoundary(int left, int top, int right, int bottom) {
			this.top = top;
			this.left = left;
			this.bottom = bottom;
			this.right = right;
		}

		public final int getTop() {
			return top;
		}

		public final void setTop(int top) {
			this.top = top;
		}

		public final int getLeft() {
			return left;
		}

		public final void setLeft(int left) {
			this.left = left;
		}

		public final int getBottom() {
			return bottom;
		}

		public final void setBottom(int bottom) {
			this.bottom = bottom;
		}

		public final int getRight() {
			return right;
		}

		public final void setRight(int right) {
			this.right = right;
		}

		public static final LayerBoundary setLayerBoundary(Layer layer) {
			LayerBoundary boundary = new LayerBoundary(layer);
			setLayerBoundary(layer, boundary);
			return boundary;
		}

		public static final void setLayerBoundary(Layer layer, LayerBoundary layerBoundary) {
			layer.setLayerObject(layerBoundary);
		}

		public static final LayerBoundary getLayerBoundary(Layer layer) {
			return layer.getLayerObject(LayerBoundary.class);
		}

		public static final LayerBoundary getLayerBoundary(LayerBoundary boundary) {
			return new LayerBoundary(boundary);
		}
	}

}
