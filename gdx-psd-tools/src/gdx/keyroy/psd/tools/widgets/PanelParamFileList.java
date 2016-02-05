package gdx.keyroy.psd.tools.widgets;

import gdx.keyroy.psd.tools.models.EditorData;
import gdx.keyroy.psd.tools.models.KeyVal;
import gdx.keyroy.psd.tools.models.ParamData;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.SwingUtil;
import gdx.keyroy.psd.tools.util.SwingUtil.DropInAdapter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeCellRenderer;

@SuppressWarnings({ "serial" })
public class PanelParamFileList extends JPanel {
	private JTree filesTree;
	private ParamDataListModel model = new ParamDataListModel();

	/**
	 * Create the panel.
	 */
	public PanelParamFileList() {
		setLayout(new BorderLayout(0, 0));
		JLabel lable = new JLabel(L.get("label.param_file_tree"));
		lable.setBorder(new EmptyBorder(8, 8, 8, 8));
		add(lable, BorderLayout.NORTH);

		filesTree = new JTree();
		filesTree.setModel(model);
		filesTree.setCellRenderer(model);
		filesTree.setBorder(new EmptyBorder(4, 4, 4, 4));

		JScrollPane scrollPane = new JScrollPane(filesTree);
		add(scrollPane, BorderLayout.CENTER);

		addPopup(filesTree);

		SwingUtil.addDropIn(filesTree, new DropInAdapter() {
			@Override
			public void onDropIn(List<File> files) {
				for (File file : files) {
					onDropIn(file);
				}
				EditorData.save();
				updateList();
			}

			@Override
			public void onDropIn(File file) {
				EditorData.verify(file, false);
			}
		});
	}

	protected final void updateList() {
		model.setParams(EditorData.getParamDatas());
	}

	private class ParamDataListModel extends ParamLayerTreeModel implements TreeCellRenderer {
		private EmptyBorder border = new EmptyBorder(4, 4, 4, 4);
		private Color TRANSLUCENT = new Color(Color.TRANSLUCENT, true);

		public ParamDataListModel() {
			super(EditorData.getParamDatas());

		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			if (value == null) {
				return null;
			}

			String name = null;
			String tip = null;
			Icon icon = null;
			if (value instanceof List) {
				List<?> list = (List<?>) value;
				name = L.get("label.param_file_count") + " " + list.size();
				icon = Icons.PRARM_LIST_FILE;
			} else if (value instanceof ParamData) {
				ParamData paramData = (ParamData) value;
				name = paramData.getFileName();
				icon = Icons.PRARM_FILE;
				tip = paramData.getFilePath();
			} else if (value instanceof KeyVal) {
				KeyVal keyVal = (KeyVal) value;
				if (keyVal.getVal() != null) {
					name = keyVal.toString();
				} else {
					name = keyVal.getKey();
				}
				icon = Icons.PRARM_KEY_VAL;
			}

			//
			File file = new File(name);
			JLabel label = new JLabel(file.getName());
			label.setOpaque(true);
			label.setIcon(icon);
			label.setToolTipText(tip);
			label.setBorder(border);
			if (selected) {
				label.setBackground(Color.lightGray);
			} else {
				label.setBackground(TRANSLUCENT);
			}
			return label;
		}
	}

	private void addPopup(Component component) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				Object object = filesTree.getSelectionPath().getLastPathComponent();
				if (object instanceof ParamData) {
					final ParamData paramData = (ParamData) object;
					JPopupMenu popupMenu = new JPopupMenu();
					//
					JMenuItem menuItem_open = new JMenuItem(L.get("Menu.open_param_file"));
					menuItem_open.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {// 更新文件信息
							Desktop desktop = Desktop.getDesktop();
							try {
								desktop.open(new File(paramData.getFilePath()));
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					});
					popupMenu.add(menuItem_open);
					//
					JMenuItem menuItem_update = new JMenuItem(L.get("Menu.update_param_file"));
					menuItem_update.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {// 更新文件信息
							paramData.cleanCache();
							updateList();
						}
					});
					popupMenu.add(menuItem_update);
					//
					JMenuItem menuItem_delete = new JMenuItem(L.get("Menu.delete_param_file"));
					menuItem_delete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {// 删除
							int n = JOptionPane.showConfirmDialog(null, L.get("Dialog.delete_param_file"),
									L.get("Menu.delete_param_file"), JOptionPane.YES_NO_OPTION);
							if (n == JOptionPane.OK_OPTION) {
								EditorData.getParamDatas().remove(paramData);
								EditorData.save();
								updateList();
							}
						}
					});
					popupMenu.add(menuItem_delete);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}
}
