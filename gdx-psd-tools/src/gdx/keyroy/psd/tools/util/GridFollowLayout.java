package gdx.keyroy.psd.tools.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

public class GridFollowLayout implements LayoutManager {
	private int offset = 4;
	private boolean averageOffset = true;

	private final Rectangle setup(Container parent) {
		if (parent.getComponentCount() == 0 || parent.getWidth() == 0) {
			return null;
		}
		int parentWidth = getParentWidth(parent);
		int maxWidth = 0;
		int maxHeight = 0;
		for (int i = 0; i < parent.getComponentCount(); i++) {
			Component component = parent.getComponent(i);
			Dimension dimension = component.getPreferredSize();
			maxWidth = Math.max(maxWidth, dimension.width);
			maxHeight = Math.max(maxHeight, dimension.height);
		}

		int cols = (parentWidth - getPaddingLeft() - getPaddingRight()) / maxWidth;

		int check = getPaddingLeft() + getPaddingRight() + cols * maxWidth + (cols - 1)
				* getPaddingHorizontal();
		while (check > parentWidth && cols > 0) {
			cols -= 1;
			check = getPaddingLeft() + getPaddingRight() + cols * maxWidth + (cols - 1)
					* getPaddingHorizontal();
		}

		if (cols == 0) {
			cols = 1;
		} else if (cols > parent.getComponentCount()) {
			cols = parent.getComponentCount();
		}
		int rows = parent.getComponentCount() / cols;
		if (parent.getComponentCount() % cols != 0) {
			rows += 1;
		}
		Rectangle rectangle = new Rectangle(cols, rows, maxWidth, maxHeight);
		return rectangle;
	}

	private int getPaddingHorizontal() {
		return offset;
	}

	private int getPaddingRight() {
		return offset;
	}

	private int getPaddingLeft() {
		return offset;
	}

	private int getPaddingVertical() {
		return offset;
	}

	private int getPaddingTop() {
		return offset;
	}

	private int getPaddingBottom() {
		return offset;
	}

	public void setAverageOffset(boolean averageOffset) {
		this.averageOffset = averageOffset;
	}

	private final int getParentWidth(Container parent) {
		if (parent.getParent() != null) {
			parent = parent.getParent();
		}
		return parent.getWidth();
	}

	@Override
	public void layoutContainer(Container parent) {
		Rectangle rectangle = setup(parent);
		if (rectangle == null) {
			return;
		} else {
			float ox = 0;
			if (averageOffset) {
				ox = (float) (getParentWidth(parent) - (getPaddingLeft() + getPaddingRight() + rectangle.x
						* rectangle.width + (rectangle.x - 1) * getPaddingHorizontal()))
						/ ((float) rectangle.x + 1);
			}

			for (int i = 0; i < parent.getComponentCount(); i++) {
				Component component = parent.getComponent(i);
				int colIndex = i % rectangle.x;
				int rowIndex = i / rectangle.x;
				float x = getPaddingLeft() + ox + colIndex * rectangle.width;
				if (colIndex > 0) {
					x += (colIndex) * (getPaddingHorizontal() + ox);
				}

				int y = getPaddingTop() + rowIndex * rectangle.height;
				if (rowIndex > 0) {
					y += (rowIndex) * getPaddingVertical();
				}
				component.setBounds((int) x, y, rectangle.width, rectangle.height);
			}
		}

	}

	protected Dimension checkSize(Container parent, boolean usedPreferredSize) {
		Rectangle size = setup(parent);
		if (size == null) {
			return parent.getSize();
		} else {
			int reHeight = getPaddingTop() + getPaddingBottom() + (size.y - 1) * getPaddingVertical()
					+ size.y * size.height;
			return new Dimension(getParentWidth(parent), reHeight);
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
		return checkSize(parent, true);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return checkSize(parent, true);
	}
}
