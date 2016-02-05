package gdx.keyroy.psd.tools.widgets;

import gdx.keyroy.psd.tools.models.EditorConfig;
import gdx.keyroy.psd.tools.util.FileUtil;
import gdx.keyroy.psd.tools.util.L;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class FrameEditorConfig extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Create the frame.
	 */
	public FrameEditorConfig() {
		setTitle(L.get("dialog.frame_config"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(0, 0, 480, 152);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(4, 4, 4, 4));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel(L.get("text.pack_folder"));
		lblNewLabel.setBounds(10, 9, 60, 16);
		panel.add(lblNewLabel);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setText(EditorConfig.export_path);
		textField.setBounds(80, 7, 276, 22);
		panel.add(textField);
		textField.setColumns(10);

		JButton btn_select_folder = new JButton(L.get("text.select_folder"));
		btn_select_folder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File folder = FileUtil.selecDirectories(contentPane, EditorConfig.export_path);
				if (folder != null) {
					textField.setText(folder.getPath());
					EditorConfig.export_path = folder.getPath();
					EditorConfig.save();
				}
			}
		});
		btn_select_folder.setBounds(366, 5, 90, 24);
		panel.add(btn_select_folder);

		{
			final JCheckBox cb_ugc = new JCheckBox(L.get("text.used_libgdx_coordinate"));
			cb_ugc.setSelected(EditorConfig.used_libgdx_coordinate);
			cb_ugc.setBounds(10, 32, 446, 24);
			panel.add(cb_ugc);
			cb_ugc.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					EditorConfig.used_libgdx_coordinate = cb_ugc.isSelected();
					EditorConfig.save();
				}
			});
		}

		{
			final JCheckBox cb_utp = new JCheckBox(L.get("text.used_texture_packer"));
			cb_utp.setSelected(EditorConfig.used_texture_packer);
			cb_utp.setBounds(10, 58, 446, 23);
			panel.add(cb_utp);
			cb_utp.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					EditorConfig.used_texture_packer = cb_utp.isSelected();
					EditorConfig.save();
				}
			});
		}

		{
			final JCheckBox cb_uaan = new JCheckBox(L.get("text.used_android_assets_name"));
			cb_uaan.setEnabled(false);
			// cb_uaan.setSelected(EditorConfig.used_android_assets_name);
			cb_uaan.setBounds(10, 83, 446, 23);
			panel.add(cb_uaan);
			cb_uaan.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					EditorConfig.used_android_assets_name = cb_uaan.isSelected();
					EditorConfig.save();
				}
			});
		}

	}
}
