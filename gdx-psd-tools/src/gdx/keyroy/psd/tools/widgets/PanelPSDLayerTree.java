package gdx.keyroy.psd.tools.widgets;

import gdx.keyroy.psd.tools.models.EditorData;
import gdx.keyroy.psd.tools.models.KeyVal;
import gdx.keyroy.psd.tools.models.LayerParam;
import gdx.keyroy.psd.tools.models.PSDData;
import gdx.keyroy.psd.tools.models.ParamData;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.Messager;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.MessageListener;
import gdx.keyroy.psd.tools.util.PSDUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import library.psd.Layer;
import library.psd.Psd;

@SuppressWarnings("serial")
public class PanelPSDLayerTree extends JPanel {
	private JTree layersTree;
	private PSDData psdData;
	private PSDLayerTreeNode model = new PSDLayerTreeNode();

	/**
	 * Create the panel.
	 */
	public PanelPSDLayerTree() {
		setLayout(new BorderLayout(0, 0));
		JLabel lable = new JLabel(L.get("label.psd_layer_tree"));
		lable.setBorder(new EmptyBorder(8, 8, 8, 8));
		add(lable, BorderLayout.NORTH);
		//
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		//
		layersTree = new JTree(new DefaultTreeModel(model));
		layersTree.setCellRenderer(new PsdDataLayerTreeCellRenderer());
		layersTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (layersTree.getSelectionPath() != null) {
					PSDLayerTreeNode treeNode = (PSDLayerTreeNode) layersTree.getSelectionPath()
							.getLastPathComponent();
					Object object = treeNode.getObject();
					if (object instanceof Psd) {
						Messager.send(psdData);
					} else if (object instanceof Layer) {
						Layer layer = (Layer) object;
						Messager.send(layer, psdData);
					}
				}
			}
		});
		scrollPane.setViewportView(layersTree);

		// 点击到图层
		Messager.register(PSDData.class, new MessageListener<PSDData>() {
			@Override
			public void onMessage(PSDData t, Object[] params) {
				psdData = t;
				if (is(MessageKey.SELECTED, params)) {
					model.setPsd(t.getCache());
					layersTree.setModel(new DefaultTreeModel(model));
					layersTree.repaint();
					layersTree.revalidate();
				}
			}
		});

		// 点击到图层
		Messager.register(Layer.class, new MessageListener<Layer>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onMessage(Layer t, Object[] params) {
				if (is(MessageKey.OPEN, params)) {
					Enumeration<PSDLayerTreeNode> enumeration = model.depthFirstEnumeration();
					TreePath treePath = new TreePath(model);
					while (enumeration.hasMoreElements()) {
						PSDLayerTreeNode treeNode = (PSDLayerTreeNode) enumeration.nextElement();
						if (t.equals(treeNode.getLayer())) {
							//
							Stack<TreeNode> treePaths = new Stack<TreeNode>();
							while (treeNode != null) {
								treePaths.push(treeNode);
								treeNode = (PSDLayerTreeNode) treeNode.getParent();
							}
							while (treePaths.isEmpty() == false) {
								TreeNode node = treePaths.pop();
								if (node.equals(model)) {
									continue;
								}
								treePath = treePath.pathByAddingChild(node);
								layersTree.expandPath(treePath);
							}
							layersTree.setSelectionPath(treePath);
							//
							layersTree.revalidate();
							layersTree.repaint();
							break;
						}
					}
				}
			}
		});

		// 清除
		Messager.register(MessageKey.class, new MessageListener<MessageKey>() {
			@Override
			public void onMessage(MessageKey t, Object[] params) {
				if (t == MessageKey.CLEAN) {
					layersTree.setModel(new DefaultTreeModel(model));
				}
			}
		});
		createPopmenu();
	}

	protected final void createPopmenu() {
		addPopup(layersTree);

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
				PSDLayerTreeNode treeNode = (PSDLayerTreeNode) layersTree.getSelectionPath()
						.getLastPathComponent();
				if (treeNode != null) {
					JPopupMenu popup = new JPopupMenu();
					{// 添加事件
						final Layer layer = (Layer) treeNode.getLayer();
						JMenu menu_add_param = new JMenu(L.get("menu.add_param"));
						popup.add(menu_add_param);
						List<ParamData> paramDatas = EditorData.getParamDatas();
						for (final ParamData paramData : paramDatas) {
							JMenu paraMenu = new JMenu(paramData.getFileName());
							menu_add_param.add(paraMenu);
							//
							List<KeyVal> keyVals = paramData.getCache();
							for (final KeyVal keyVal : keyVals) {
								JMenuItem keyValMenuItem = new JMenuItem(keyVal.getKey());
								paraMenu.add(keyValMenuItem);
								keyValMenuItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										//
										showEditor(layer, paramData, keyVal);
									}
								});
							}
						}
					}

					popup.show(e.getComponent(), e.getX(), e.getY());
				}

			}

			// 显示编辑器
			protected final void showEditor(Layer layer, ParamData paramData, KeyVal keyVal) {
				String[] selection = keyVal.getSelection();
				Object val = null;
				if (selection == null) {
					val = JOptionPane.showInputDialog(layersTree, L.get("text.input_param_value") + "\n",
							L.get("text.add_param"), JOptionPane.PLAIN_MESSAGE, null, null, keyVal.getVal());
				} else {
					val = (String) JOptionPane.showInputDialog(layersTree, L.get("text.input_param_value")
							+ "\n", L.get("text.add_param"), JOptionPane.PLAIN_MESSAGE, null, selection,
							selection[0]);
				}

				if (val != null) {
					//
					LayerParam layerParam = new LayerParam();
					layerParam.setLayerId(PSDUtil.getLayerId(layer));
					layerParam.setParamId(paramData.getFileName() + "." + keyVal.getKey());
					layerParam.setData(val.toString());
					psdData.addParam(layerParam);
					//
					EditorData.save();
					if (layer != null) {
						Messager.send(layer, psdData);
					} else {
						Messager.send(Layer.class, psdData);
					}
				}
			}
		});
	}

	private static class PsdDataLayerTreeCellRenderer implements TreeCellRenderer {
		private static EmptyBorder border = new EmptyBorder(4, 4, 4, 4);
		private static Color TRANSLUCENT = new Color(Color.TRANSLUCENT, true);

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			PSDLayerTreeNode treenode = (PSDLayerTreeNode) value;
			Layer layer = treenode.getLayer();
			Psd psd = treenode.getPsd();

			String name = null;
			Icon icon = null;
			if (layer != null) {
				name = layer.getName() + "    (" + PSDUtil.getLayerId(layer) + ")";
				if (layer.isFolder()) {
					icon = Icons.LAYER_FOLDER;
				} else if (layer.isTextLayer()) {
					icon = Icons.LAYER_TEXT;
				} else {
					icon = Icons.LAYER_IMAGE;
				}

			} else if (psd != null) {
				name = psd.getName();
				icon = Icons.PSD_FILE;
			}

			//
			JLabel label = new JLabel(name);
			label.setOpaque(true);
			label.setIcon(icon);
			label.setBorder(border);
			if (selected) {
				label.setBackground(Color.lightGray);
			} else {
				label.setBackground(TRANSLUCENT);
			}
			return label;
		}

	}

}
