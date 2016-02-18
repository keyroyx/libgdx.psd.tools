package gdx.keyroy.data.tools.widgets;

import gdx.keyroy.data.tools.DataManage;
import gdx.keyroy.data.tools.models.ClassElement;
import gdx.keyroy.data.tools.models.ClassPath;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.L;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.Messager;
import gdx.keyroy.psd.tools.util.SwingUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ClassElementComponent extends JPanel {

	public ClassElementComponent(final ClassElement classElement) {
		setPreferredSize(new Dimension(128, 60));
		setLayout(new BorderLayout(0, 0));

		final JLabel label = new JLabel(classElement.getObjId());
		label.setHorizontalAlignment(SwingConstants.CENTER);
		add(label);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Messager.send(classElement, MessageKey.SELECTED);
			}
		});

		JPopupMenu popup = new JPopupMenu();
		{ // 修改ID
			JMenuItem reset = new JMenuItem(L.get("menu.reset_element"));
			popup.add(reset);
			reset.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String id = (String) JOptionPane.showInputDialog(ClassElementComponent.this,
							L.get("text.input_element_id") + " ：\n", L.get("dialog.reset_element"),
							JOptionPane.PLAIN_MESSAGE, Icons.CLASS_FILE, null, "" + (classElement.getObjId()));
					if (id != null) {
						ClassPath classPath = classElement.getParent();
						if (classPath.has(id)) {
							JOptionPane.showMessageDialog(null, L.get("text.duplicate_element_id"),
									L.get("error.duplicate_element_id"), JOptionPane.ERROR_MESSAGE);
						} else {
							DataManage.reset(classElement, id);
							label.setText(id);
						}
					}
				}
			});
		}

		{ // 删除这个信息
			JMenuItem del = new JMenuItem(L.get("menu.del_element"));
			popup.add(del);
			del.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int n = JOptionPane.showConfirmDialog(ClassElementComponent.this, "label.notice", "dialog.del_class_element",
							JOptionPane.YES_NO_OPTION);// i=0/1
					if (n == JOptionPane.YES_OPTION) {
						DataManage.delete(classElement);
						//
						Messager.send(classElement.getParent(), MessageKey.UPDATE);
					}
				}
			});
		}

		SwingUtil.addPopup(this, popup);
	}
}
