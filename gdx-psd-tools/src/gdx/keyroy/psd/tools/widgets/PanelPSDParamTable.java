package gdx.keyroy.psd.tools.widgets;

import gdx.keyroy.psd.tools.models.EditorData;
import gdx.keyroy.psd.tools.models.KeyVal;
import gdx.keyroy.psd.tools.models.PSDData;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.Message;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.MessageListener;
import gdx.keyroy.psd.tools.util.PSDUtil;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import psd.LayerParam;
import library.psd.Layer;

@SuppressWarnings("serial")
public class PanelPSDParamTable extends JPanel {

	private JTable table;
	private PsdParamTableModel model = new PsdParamTableModel();

	/**
	 * Create the panel.
	 */
	public PanelPSDParamTable() {
		setLayout(new BorderLayout(0, 0));
		JLabel lable = new JLabel(L.get("label.layer_param_table"));
		lable.setBorder(new EmptyBorder(8, 8, 8, 8));
		add(lable, BorderLayout.NORTH);
		//
		table = new JTable();
		table.setModel(model);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
		//
		addPopup(table);
		// 注册事件
		initMessageListener();
	}

	private final void initMessageListener() {

		// 点击到 PSD 文件
		Message.register(PSDData.class, new MessageListener<PSDData>() {
			@Override
			public void onMessage(PSDData t, Object[] params) {// psd 文件变化
				model.show(t, null);
			}
		});

		// 点击到图层
		Message.register(Layer.class, new MessageListener<Layer>() {
			@Override
			public void onMessage(Layer t, Object[] params) { // 图层变化
				PSDData psdData = get(PSDData.class, params);
				if (psdData != null) {
					model.show(psdData, t);
				}
			}
		});

		// 清除
		Message.register(MessageKey.class, new MessageListener<MessageKey>() {

			@Override
			public void onMessage(MessageKey t, Object[] params) {
				if (t == MessageKey.CLEAN) {
					model.clean();
					revalidate();
				}
			}
		});
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
				final int[] indexes = table.getSelectedRows();
				if (indexes != null && indexes.length > 0) {
					JPopupMenu popup = new JPopupMenu();
					if (indexes.length == 1) { // 只选择了一个
						JMenuItem menuItem_jump = new JMenuItem(L.get("Menu.jump_param"));
						menuItem_jump.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								LayerParam param = model.getLayerParams().get(indexes[0]);
								Layer layer = PSDUtil.getLayerById(model.psdData.getCache(),
										param.getLayerId());
								if (layer != null) {
									Message.send(layer, MessageKey.OPEN);
								}
							}
						});
						popup.add(menuItem_jump);
						popup.add(new JSeparator());
					}

					JMenuItem menuItem_delete = new JMenuItem(L.get("Menu.delete_layer_param"));
					menuItem_delete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) { // 删除

							if (indexes != null) {
								int n = JOptionPane.showConfirmDialog(table,
										L.get("Dialog.delete_layer_param"), L.get("Menu.delete_layer_param"),
										JOptionPane.YES_NO_OPTION);
								if (n == JOptionPane.OK_OPTION) {
									List<LayerParam> params = model.getLayerParams();
									if (params != null) {
										List<LayerParam> removes = new ArrayList<LayerParam>(indexes.length);
										for (int i : indexes) {
											removes.add(params.get(i));
										}
										PSDData psdData = model.psdData;
										for (LayerParam layerParam : removes) {
											psdData.removeParam(layerParam);
										}
										EditorData.save();
										model.show(psdData, model.layer);
									}
								}
							}
						}
					});
					popup.add(menuItem_delete);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}

			}

		});
	}

	private final class PsdParamTableModel extends DefaultTableModel {
		protected PSDData psdData;
		protected Layer layer;
		protected List<LayerParam> params;

		public PsdParamTableModel() {
			addColumn(L.get("text.layer_id"));
			addColumn(L.get("text.param_key"));
			addColumn(L.get("text.param_val"));
			addColumn(L.get("text.layer_name"));
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == 2) {
				final LayerParam layerParam = params.get(row);
				KeyVal keyVal = EditorData.getKeyVal(layerParam);
				final String[] selection = keyVal.getSelection();
				if (selection != null) {
					final JComboBox<String> comboBox = new JComboBox<String>(selection);
					comboBox.setSelectedItem(layerParam.getData());
					table.getColumnModel().getColumn(column).setCellEditor(new DefaultCellEditor(comboBox));
					comboBox.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							String data = selection[comboBox.getSelectedIndex()];
							layerParam.setData(data);
							EditorData.save();
						}
					});
				} else {
					final JTextField tf = new JTextField();
					tf.addFocusListener(new FocusListener() {
						@Override
						public void focusLost(FocusEvent e) {
							layerParam.setData(tf.getText());
							EditorData.save();
						}

						@Override
						public void focusGained(FocusEvent e) {

						}
					});

					tf.setText(layerParam.getData());
					tf.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
					tf.setSelectionStart(0);
					tf.setSelectionEnd(tf.getText().length());
					table.getColumnModel().getColumn(column).setCellEditor(new DefaultCellEditor(tf));
				}

				return true;
			}
			return false;
		}

		protected final void show(PSDData psdData, Layer layer) {
			clean();
			this.psdData = psdData;
			this.layer = layer;
			if (layer == null) {
				this.params = psdData.getLayerParams();
			} else {
				this.params = psdData.getLayerParams(layer);
			}
			if (params != null) {
				for (LayerParam layerParam : params) {
					Object[] rowData = new Object[4];
					rowData[0] = layerParam.getLayerId();
					rowData[1] = layerParam.getParamId();
					rowData[2] = layerParam.getData();
					if (layer != null) {
						rowData[3] = layer.getName();
					} else if (layerParam.getLayerId() != null) {
						Layer iLayer = PSDUtil.getLayerById(psdData.getCache(), layerParam.getLayerId());
						if (iLayer != null) {
							rowData[3] = iLayer.getName();
						}
					}
					addRow(rowData);
				}
			}
			revalidate();
			repaint();
		}

		public List<LayerParam> getLayerParams() {
			return params;
		}

		public final void clean() {
			getDataVector().clear();
		}

	}
}
