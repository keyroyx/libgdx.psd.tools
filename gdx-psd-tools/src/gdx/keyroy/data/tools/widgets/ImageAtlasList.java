package gdx.keyroy.data.tools.widgets;

import gdx.keyroy.data.tools.models.ImagePath;
import gdx.keyroy.psd.tools.util.Icons;
import gdx.keyroy.psd.tools.util.Messager;
import gdx.keyroy.psd.tools.util.TextureUnpacker;
import gdx.keyroy.psd.tools.util.TextureUnpacker.Region;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class ImageAtlasList extends JPanel {

	@SuppressWarnings("unchecked")
	public ImageAtlasList(final ImagePath imagePath) {
		setLayout(new BorderLayout(0, 0));

		JList<Region> list = new JList<Region>();
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				JList<Region> list = (JList<Region>) e.getSource();
				Region region = list.getSelectedValue();
				if (region != null && e.getValueIsAdjusting()) {
					Messager.send(region, imagePath);
				}
			}
		});
		RegionListModel regionListModel = new RegionListModel(imagePath.getUnpacker());
		list.setCellRenderer(regionListModel);
		list.setModel(regionListModel);
		add(list);
	}

	@SuppressWarnings("rawtypes")
	class RegionListModel extends DefaultListModel implements ListCellRenderer<Region> {
		private EmptyBorder border = new EmptyBorder(4, 4, 4, 4);
		private Color TRANSLUCENT = new Color(Color.TRANSLUCENT, true);

		@SuppressWarnings("unchecked")
		public RegionListModel(TextureUnpacker textureUnpacker) {
			for (Region region : textureUnpacker.getRegions()) {
				addElement(region);
			}
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Region> list, Region value, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (value == null) {
				return null;
			}
			//
			JLabel label = new JLabel(value.name + "[" + value.toString() + "]");
			label.setOpaque(true);
			label.setIcon(Icons.IMAGE_FILE);
			label.setBorder(border);
			if (isSelected) {
				label.setBackground(Color.LIGHT_GRAY);
			} else {
				label.setBackground(TRANSLUCENT);
			}
			return label;
		}
	}
}
