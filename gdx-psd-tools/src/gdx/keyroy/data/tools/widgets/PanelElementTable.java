package gdx.keyroy.data.tools.widgets;

import gdx.keyroy.data.tools.models.ClassPath;
import gdx.keyroy.data.tools.models.ImagePath;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.MessageListener;
import gdx.keyroy.psd.tools.util.Messager;
import gdx.keyroy.psd.tools.util.SwingUtil;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

// 类管理的面板
@SuppressWarnings("serial")
public class PanelElementTable extends JPanel {
	private JTabbedPane tabbedPane;

	/**
	 * Create the panel.
	 */
	public PanelElementTable() {
		setLayout(new BorderLayout(0, 0));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		//
		initMessageListener();
	}

	private final void initMessageListener() {
		Messager.register(ClassPath.class, new MessageListener<ClassPath>() {
			@Override
			public void onMessage(ClassPath t, Object[] params) {
				if (is(gdx.keyroy.psd.tools.util.MessageKey.SELECTED, params)) {
					addTab(t, t.getClassName(), Icons.CLASS_FILE, new TabClassPathPanel(t));
				}
			}
		});

		Messager.register(ImagePath.class, new MessageListener<ImagePath>() {
			@Override
			public void onMessage(ImagePath t, Object[] params) {
				if (t.isAtlas()) {
					addTab(t, t.getFileName(), Icons.IMAGE_ATLAS_FILE, new TabImageDrawerPanel(t));
				} else {
					addTab(t, t.getFileName(), Icons.IMAGE_FILE, new TabImageDrawerPanel(t));
				}
			}
		});

		tabbedPane.addContainerListener(new ContainerAdapter() {
			@Override
			public void componentRemoved(ContainerEvent e) {
				Component component = e.getChild();
				if (component instanceof ContainerListener) {
					((ContainerListener) component).componentRemoved(e);
				}
			}

			@Override
			public void componentAdded(ContainerEvent e) {
				Component component = e.getChild();
				if (component instanceof ContainerListener) {
					((ContainerListener) component).componentAdded(e);
				}
			}
		});
	}

	private final void addTab(Object key, String label, Icon icon, Component component) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			TabLable tabLable = (TabLable) tabbedPane.getTabComponentAt(i);
			if (tabLable.key.equals(key)) {
				tabbedPane.setSelectedIndex(i);
				return;
			}
		}

		tabbedPane.addTab(label, icon, component);
		tabbedPane.setSelectedComponent(component);
		tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(), new TabLable(key, label, icon));
	}

	private class TabLable extends JLabel {
		Object key;
		JPopupMenu popup;

		public TabLable(Object key, String label, Icon icon) {
			this.key = key;
			setText(label);
			setIcon(icon);
			//
			addMouseListener(new SwingUtil.MouseLeftButtonListener() {
				@Override
				public void mousePressed(MouseEvent e) {
					tabbedPane.setSelectedIndex(tabbedPane.indexOfTabComponent(TabLable.this));
					Messager.send(TabLable.this.key);
				}
			});
			//
			popup = new JPopupMenu();
			{//
				JMenuItem menuItem = new JMenuItem(L.get("menu.close_all"));
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tabbedPane.removeAll();
						tabbedPane.revalidate();
						tabbedPane.repaint();
					}
				});
				popup.add(menuItem);
			}
			{
				JMenuItem menuItem = new JMenuItem(L.get("menu.close_others"));
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						List<Component> removeList = new ArrayList<Component>();
						for (int i = 0; i < tabbedPane.getTabCount(); i++) {
							TabLable tabLable = (TabLable) tabbedPane.getTabComponentAt(i);
							if (tabLable.equals(TabLable.this)) {

							} else {
								removeList.add(tabbedPane.getComponentAt(tabbedPane
										.indexOfTabComponent(tabLable)));
							}
						}

						for (Component component : removeList) {
							tabbedPane.remove(component);
							tabbedPane.revalidate();
							tabbedPane.repaint();
						}
					}
				});
				popup.add(menuItem);
			}
			{
				JMenuItem menuItem = new JMenuItem(L.get("menu.close_this"));
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tabbedPane.remove(tabbedPane.getComponentAt(tabbedPane
								.indexOfTabComponent(TabLable.this)));
						tabbedPane.revalidate();
						tabbedPane.repaint();
					}
				});
				popup.add(menuItem);
			}

			addMouseListener(new SwingUtil.MouseRightButtonListener() {
				@Override
				protected void onMouseClicked(MouseEvent e) {
					showMenu(e);
				}

				private void showMenu(MouseEvent e) {
					popup.show(e.getComponent(), e.getX(), e.getY());
					popup.repaint();
				}
			});
		}

	}

}
