package gdx.keyroy.data.tools.widgets;

import gdx.keyroy.data.tools.models.ImagePath;
import gdx.keyroy.psd.tools.util.MessageListener;
import gdx.keyroy.psd.tools.util.Messager;
import gdx.keyroy.psd.tools.util.ScrollPane;
import gdx.keyroy.psd.tools.util.TextureUnpacker;
import gdx.keyroy.psd.tools.util.TextureUnpacker.Page;
import gdx.keyroy.psd.tools.util.TextureUnpacker.Region;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TabImageDrawerPanel extends JPanel implements ContainerListener {
	private ImagePath imagePath;
	private ImageView imageView;

	/**
	 * Create the panel.
	 */
	public TabImageDrawerPanel(ImagePath imagePath) {
		this.imagePath = imagePath;
		this.imageView = new ImageView();
		try {
			File file = new File(imagePath.getFilePath());
			if (imagePath.isAtlas()) {
				imageView.setPage(imagePath);
			} else {
				BufferedImage bufferedImage = ImageIO.read(file);
				imageView.setBufferedImage(bufferedImage);
			}
		} catch (Exception e) {
		}
		//
		setLayout(new BorderLayout(0, 0));
		add(new ScrollPane(imageView), BorderLayout.CENTER);
	}

	@Override
	public void componentAdded(ContainerEvent e) {
		Messager.register(Region.class, regionListener);
	}

	@Override
	public void componentRemoved(ContainerEvent e) {
		Messager.unregister(Region.class, regionListener);
	}

	public final ImagePath getImagePath() {
		return imagePath;
	}

	private class ImageView extends JComponent {
		private BufferedImage bufferedImage;
		private Color color = new Color(0xff990099);
		private Stroke stroke = new BasicStroke(3);
		private ImagePath imagePath;
		private Region region;

		public void setBufferedImage(BufferedImage bufferedImage) {
			if (bufferedImage != null) {
				setPreferredSize(new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight()));
			} else {
				setPreferredSize(new Dimension(0, 0));
			}
			this.bufferedImage = bufferedImage;
			this.imagePath = null;

		}

		public void setPage(ImagePath imagePath) {
			TextureUnpacker unpacker = imagePath.getUnpacker();
			Page page = unpacker.getPages().get(0);
			setBufferedImage(page.texture);
			this.imagePath = imagePath;
		}

		public void setRegion(Region region, ImagePath imagePath) {
			if (imagePath != null && imagePath.equals(this.imagePath)) {
				this.region = region;
			} else {
				this.region = null;
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(stroke);
			g2d.setColor(color);
			if (bufferedImage != null) {
				g2d.drawImage(bufferedImage, 0, 0, null);
			}

			if (region != null) {
				g2d.drawRect(region.left, region.top, region.width, region.height);
				g2d.drawString(region.name, region.left, region.top);
			}
		}
	}

	private MessageListener<Region> regionListener = new MessageListener<TextureUnpacker.Region>() {
		@Override
		public void onMessage(Region t, Object[] params) {
			imageView.setRegion(t, get(ImagePath.class, params));
			repaint();
		}
	};

}
