package gdx.keyroy.psd.tools.widgets;

import gdx.keyroy.psd.tools.models.EditorData;
import gdx.keyroy.psd.tools.models.PSDData;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.Message;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.PSDUtil;
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
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
public class PanelPSDFileList extends JPanel {
	private JPopupMenu popupMenu;
	private JList list;
	private JMenuItem menuItem_delete;
	private PsdDataListModel model = new PsdDataListModel();

	/**
	 * Create the panel.
	 */
	public PanelPSDFileList() {
		setLayout(new BorderLayout(0, 0));
		JLabel lable = new JLabel(L.get("label.psd_file_tree"));
		lable.setBorder(new EmptyBorder(8, 8, 8, 8));
		add(lable, BorderLayout.NORTH);

		list = new JList();
		list.setBorder(new EmptyBorder(4, 4, 4, 4));
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		//
		list.setModel(model);
		list.setCellRenderer(model);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (list.getSelectedIndex() != -1) {
					try {
						PSDData psdData = (PSDData) list.getSelectedValue();
						PSDUtil.updateLayerParent(psdData.getCache());
						Message.send(psdData, MessageKey.SELECTED);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(list);
		add(scrollPane, BorderLayout.CENTER);

		createPopmenu();
		SwingUtil.addDropIn(list, new DropInAdapter() {
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

	protected final void createPopmenu() {
		popupMenu = new JPopupMenu();
		addPopup(list, popupMenu);

		menuItem_delete = new JMenuItem(L.get("Menu.delete_psd_file"));
		menuItem_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // É¾³ý
				if (list.getSelectedIndices() != null) {
					int n = JOptionPane.showConfirmDialog(null, L.get("Dialog.delete_psd_file"),
							L.get("Menu.delete_psd_file"), JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.OK_OPTION) {
						int[] indexes = list.getSelectedIndices();
						for (int i : indexes) {
							PSDData psdData = (PSDData) list.getModel().getElementAt(i);
							EditorData.getPsdDatas().remove(psdData);
						}
						EditorData.save();
						updateList();
						Message.send(MessageKey.CLEAN);
					}
				}

				if (list.getSelectedIndex() != -1) {
					PSDData psdData = (PSDData) list.getSelectedValue();
					int n = JOptionPane.showConfirmDialog(null, L.get("Dialog.delete_psd_file") + "\n"
							+ psdData.getFilePath(), L.get("Menu.delete_psd_file"), JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.OK_OPTION) {
						EditorData.getPsdDatas().remove(psdData);
						EditorData.save();
						updateList();
					}
				}
			}
		});
		popupMenu.add(menuItem_delete);
	}

	protected final void updateList() {
		list.setModel(new PsdDataListModel());
	}

	private class PsdDataListModel extends DefaultListModel implements ListCellRenderer<PSDData> {
		private EmptyBorder border = new EmptyBorder(4, 4, 4, 4);
		private Color TRANSLUCENT = new Color(Color.TRANSLUCENT, true);

		public PsdDataListModel() {
			List<PSDData> datas = EditorData.getPsdDatas();
			for (PSDData data : datas) {
				addElement(data);
			}
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends PSDData> list, PSDData value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (value == null) {
				return null;
			}
			//
			File file = new File(value.getFilePath());
			JLabel label = new JLabel(file.getName());
			label.setOpaque(true);
			label.setIcon(Icons.PSD_FILE);
			label.setToolTipText(file.getPath());
			label.setBorder(border);
			if (isSelected) {
				label.setBackground(Color.lightGray);
			} else {
				label.setBackground(TRANSLUCENT);
			}

			return label;
		}
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
}
