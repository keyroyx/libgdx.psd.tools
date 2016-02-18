package gdx.keyroy.psd.tools.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

public class CenterLayout implements LayoutManager {

	@Override
	public void layoutContainer(Container parent) {
		Rectangle rectangle = parent.getBounds();
		if (rectangle.width <= 0 || rectangle.height <= 0 || parent.getComponentCount() == 0) {
			return;
		}
		int mw = rectangle.width;
		int mh = rectangle.height;
		for (int i = 0; i < parent.getComponentCount(); i++) {
			Component component = parent.getComponent(i);
			Dimension dimension = component.getPreferredSize();

			int cw = Math.min(dimension.width, mw);
			int ch = Math.min(dimension.height, mh);
			int cx = (rectangle.width - cw) / 2;
			int cy = (rectangle.height - ch) / 2;
			component.setLocation(cx, cy);
			component.setSize(cw, ch);
		}
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {

	}

	@Override
	public void removeLayoutComponent(Component comp) {

	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		int mw = 0;
		int mh = 0;
		for (int i = 0; i < parent.getComponentCount(); i++) {
			Component component = parent.getComponent(i);
			Dimension dimension = component.getPreferredSize();

			mw = Math.min(dimension.width, mw);
			mh = Math.min(dimension.height, mh);
		}

		return new Dimension(mw, mh);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}
}
