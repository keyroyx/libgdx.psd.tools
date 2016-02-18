package gdx.keyroy.psd.tools.util;

import gdx.keyroy.psd.tools.util.SwingUtil.OnPopOutListener;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

@SuppressWarnings("serial")
public class DefaultTreeNode extends DefaultMutableTreeNode implements TreeCellRenderer {
	private EmptyBorder border = new EmptyBorder(4, 4, 4, 4);
	private Color TRANSLUCENT = new Color(Color.TRANSLUCENT, true);
	//
	private Icon icon;
	private String labelText;
	private String tip;
	private String dropOut;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		final DefaultTreeNode treeNode = (DefaultTreeNode) value;
		//
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBorder(border);
		if (treeNode.labelText != null) {
			label.setText(treeNode.labelText);
		} else if (treeNode.getUserObject() != null) {
			label.setText(treeNode.getUserObject().toString());
		}
		if (treeNode.icon != null) {
			label.setIcon(treeNode.icon);
		}
		if (treeNode.tip != null) {
			label.setToolTipText(treeNode.tip);
		}
		if (selected) {
			label.setBackground(Color.LIGHT_GRAY);
		} else {
			label.setBackground(TRANSLUCENT);
		}
		if (treeNode.dropOut != null) {
			SwingUtil.addDropOut(label, new OnPopOutListener() {
				@Override
				public String onPopOut() {
					return treeNode.dropOut;
				}
			});
		}
		return label;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public void setLabel(String label) {
		this.labelText = label;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public void setDropOut(String dropOut) {
		this.dropOut = dropOut;
	}

	protected void clean() {
		labelText = null;
		tip = null;
		icon = null;
		dropOut = null;
	}

}
