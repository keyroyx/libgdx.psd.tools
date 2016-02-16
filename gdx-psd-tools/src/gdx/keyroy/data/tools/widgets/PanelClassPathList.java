package gdx.keyroy.data.tools.widgets;

import gdx.keyroy.data.tools.models.ClassPath;
import gdx.keyroy.data.tools.util.DataManage;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.Message;
import gdx.keyroy.psd.tools.util.MessageKey;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
public class PanelClassPathList extends JPanel {
	private JList<ClassPath> list;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("unchecked")
	public PanelClassPathList() {
		setLayout(new BorderLayout(0, 0));

		list = new JList<ClassPath>();
		list.setModel(new ClassPathListModel());
		list.setCellRenderer(new ClassPathListModel());

		JScrollPane scrollPane = new JScrollPane(list);
		add(scrollPane, BorderLayout.CENTER);

		createPopmenu();
	}

	protected final void createPopmenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(list, popupMenu);

		JMenuItem menuItem_delete = new JMenuItem(L.get("Menu.delete_class_path"));
		menuItem_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // 删除
				if (list.getSelectedIndices() != null) {
					int n = JOptionPane.showConfirmDialog(null, L.get("Dialog.delete_class_path"),
							L.get("Menu.delete_class_path"), JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.OK_OPTION) {
						int[] indexes = list.getSelectedIndices();
						for (int i : indexes) {
							ClassPath classPath = (ClassPath) list.getModel().getElementAt(i);
							DataManage.getClassPaths().remove(classPath);
						}
						DataManage.save();
						updateList();
						Message.send(MessageKey.CLEAN);
					}
				}
			}
		});
		popupMenu.add(menuItem_delete);
	}

	@SuppressWarnings("unchecked")
	protected final void updateList() {
		list.setModel(new ClassPathListModel());
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
	private class ClassPathListModel extends DefaultListModel implements ListCellRenderer<ClassPath> {
		private EmptyBorder border = new EmptyBorder(4, 4, 4, 4);
		private Color TRANSLUCENT = new Color(Color.TRANSLUCENT, true);

		@SuppressWarnings("unchecked")
		public ClassPathListModel() {
			List<ClassPath> list = DataManage.getClassPaths();
			for (ClassPath path : list) {
				addElement(path);
			}
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends ClassPath> list, ClassPath value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (value == null) {
				return null;
			}
			//
			JLabel label = new JLabel(value.getClassName());
			label.setOpaque(true);
			label.setIcon(Icons.CLASS_FILE);
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
