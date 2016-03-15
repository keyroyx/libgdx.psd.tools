package gdx.keyroy.data.tools.widgets;

import gdx.keyroy.data.tools.DataManage;
import gdx.keyroy.data.tools.models.ResoucePath;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.Messager;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.SwingUtil;
import gdx.keyroy.psd.tools.util.SwingUtil.DropInAdapter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

// 类管理的面板
@SuppressWarnings("serial")
public class PanelImagePathList extends JPanel {
	private JList<ResoucePath> list;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("unchecked")
	public PanelImagePathList() {
		setLayout(new BorderLayout(0, 0));

		list = new JList<ResoucePath>();
		list.setModel(new ImagePathListModel());
		list.setCellRenderer(new ImagePathListModel());

		JScrollPane scrollPane = new JScrollPane(list);
		add(scrollPane, BorderLayout.CENTER);

		createPopmenu();
		SwingUtil.addDropIn(list, new DropInAdapter() {
			@Override
			public void onDropIn(File file) {
				DataManage.addResource(file, true);
				updateList();
			}
		});
	}

	protected final void createPopmenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(list, popupMenu);

		JMenuItem menuItem_delete = new JMenuItem(L.get("Menu.delete_psd_file"));
		menuItem_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // 删除
				if (list.getSelectedIndices() != null) {
					int n = JOptionPane.showConfirmDialog(null, L.get("Dialog.delete_psd_file"),
							L.get("Menu.delete_psd_file"), JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.OK_OPTION) {
						int[] indexes = list.getSelectedIndices();
						for (int i : indexes) {
							ResoucePath classPath = (ResoucePath) list.getModel().getElementAt(i);
							DataManage.getResourcePaths().remove(classPath);
						}
						DataManage.save();
						updateList();
						Messager.send(MessageKey.CLEAN);
					}
				}
			}
		});
		popupMenu.add(menuItem_delete);
	}

	@SuppressWarnings("unchecked")
	protected final void updateList() {
		list.setModel(new ImagePathListModel());
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
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
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private class ImagePathListModel extends DefaultListModel implements ListCellRenderer<ResoucePath> {
		private EmptyBorder border = new EmptyBorder(4, 4, 4, 4);
		private Color TRANSLUCENT = new Color(Color.TRANSLUCENT, true);

		@SuppressWarnings("unchecked")
		public ImagePathListModel() {
			List<ResoucePath> list = DataManage.getResourcePaths();
			for (ResoucePath path : list) {
				addElement(path);
			}
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends ResoucePath> list, ResoucePath value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (value == null) {
				return null;
			}
			//
			JLabel label = new JLabel(value.getAssetsPath());
			label.setOpaque(true);
			if (value.isAtlas()) {
				label.setIcon(Icons.IMAGE_ATLAS_FILE);
			} else {
				label.setIcon(Icons.RESOURCE_FILE);
			}
			label.setBorder(border);
			if (isSelected) {
				label.setBackground(Color.lightGray);
			} else {
				label.setBackground(TRANSLUCENT);
			}

			return label;
		}

	}

}
