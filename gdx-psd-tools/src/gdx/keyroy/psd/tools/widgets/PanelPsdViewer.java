package gdx.keyroy.psd.tools.widgets;

import gdx.keyroy.psd.tools.models.PSDData;
import gdx.keyroy.psd.tools.util.CenterLayout;
import gdx.keyroy.psd.tools.util.Message;
import gdx.keyroy.psd.tools.util.MessageKey;
import gdx.keyroy.psd.tools.util.MessageListener;
import gdx.keyroy.psd.tools.util.PSDUtil;
import gdx.keyroy.psd.tools.util.PSDUtil.LayerBoundary;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import library.psd.Layer;
import library.psd.LayersContainer;
import library.psd.Psd;
import library.psd.util.LayerFilter;

@SuppressWarnings("serial")
public class PanelPsdViewer extends JPanel {
	private PsdView psdView;

	public PanelPsdViewer() {
		setLayout(new BorderLayout(0, 0));
		psdView = new PsdView();
		final JPanel panel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				if (psdView.psd != null) {
					Rectangle rectangle = PSDUtil.getMaxSize(psdView.psd);
					return new Dimension((int) rectangle.getWidth() + 10, (int) rectangle.getHeight() + 10);
				}
				return super.getPreferredSize();
			}
		};
		panel.setLayout(new CenterLayout());
		panel.add(psdView);
		JScrollPane scrollPane = new JScrollPane(panel);
		add(scrollPane);

		// 点击到 PSD 文件
		Message.register(PSDData.class, new MessageListener<PSDData>() {
			@Override
			public void onMessage(PSDData t, Object[] params) {
				// if (is(MessageKey.SELECTED, params)) {
				Psd psd = t.getCache();
				psdView.setPsd(psd);
				panel.revalidate();
				// }
			}
		});

		// 点击到图层
		Message.register(Layer.class, new MessageListener<Layer>() {
			@Override
			public void onMessage(Layer t, Object[] params) {
				psdView.setLayer(t);
			}
		});

		// 清除
		Message.register(MessageKey.class, new MessageListener<MessageKey>() {

			@Override
			public void onMessage(MessageKey t, Object[] params) {
				if (t == MessageKey.CLEAN) {
					psdView.clean();
				}
			}
		});
	}

	private class PsdView extends JComponent {
		private static final long serialVersionUID = 1L;
		private static final int LINE_WIDTH = 3;
		private boolean isDirty;
		private Psd psd;
		private Rectangle maxPsdSize;
		private Layer layer;
		private BufferedImage bufferedImage;
		private Color color = new Color(0xff990099);
		private Stroke stroke = new BasicStroke(LINE_WIDTH);

		public PsdView() {
			setPreferredSize(new Dimension(400, 400));
		}

		public void setPsd(Psd psd) {
			this.psd = psd;
			this.layer = null;
			this.maxPsdSize = PSDUtil.getMaxSize(psd);
			Rectangle rectangle = PSDUtil.getMaxSize(psd);
			Dimension dimension = new Dimension(rectangle.width + LINE_WIDTH, rectangle.height + LINE_WIDTH);
			setPreferredSize(dimension);
			bufferedImage = new BufferedImage(dimension.width, dimension.height,
					BufferedImage.TYPE_4BYTE_ABGR);
			isDirty = true;
			repaint();
			revalidate();
		}

		public void clean() {
			this.psd = null;
			this.layer = null;
			this.bufferedImage = null;
			this.maxPsdSize = null;
			isDirty = false;
			setPreferredSize(new Dimension());
			repaint();
			revalidate();
		}

		public void setLayer(final Layer layer) {
			if (psd != null && layer != null) {
				this.layer = psd.getLayer(new LayerFilter() {
					@Override
					public boolean accept(Layer lay) {
						return layer.equals(lay);
					}
				});
			}
			repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(stroke);
			g2d.setColor(color);
			if (isDirty) {
				Graphics2D graphics = bufferedImage.createGraphics();
				paintLayersContainer(graphics, psd, 1.0f);
				graphics.dispose();
				isDirty = false;
			}

			if (bufferedImage != null) {
				g2d.drawImage(bufferedImage, 0, 0, null);
			} else if (psd != null) {
				paintLayersContainer(g2d, psd, 1.0f);
			}

			if (layer != null) {
				paintLayersBoundary(g2d, layer);
			} else if (maxPsdSize != null) {
				g.drawRect(0, 0, maxPsdSize.width, maxPsdSize.height);
			}
		}

		private final void paintLayersContainer(Graphics2D g, LayersContainer container, float alpha) {
			for (int i = 0; i < container.getLayersCount(); i++) {
				Layer layer = container.getLayer(i);
				if (!layer.isVisible()) {
					continue;
				}

				Composite composite = g.getComposite();
				float layerAlpha = alpha * layer.getAlpha() / 255.0f;
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, layerAlpha));

				if (layer.getImage() != null) {
					g.drawImage(layer.getImage(), layer.getX(), layer.getY(), null);
				}
				g.setComposite(composite);
				paintLayersContainer(g, layer, layerAlpha);
			}
		}

		private final void paintLayersBoundary(Graphics2D g, Layer layer) {
			//
			Composite composite = g.getComposite();
			float layerAlpha = layer.getAlpha() / 255.0f;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, layerAlpha));
			if (layer.getImage() != null) {
				g.drawImage(layer.getImage(), layer.getX(), layer.getY(), null);
			}
			g.setComposite(composite);
			if (layer.isFolder()) {
				LayerBoundary boundary = LayerBoundary.getLayerBoundary(layer);
				g.drawString(layer.getName(), boundary.getX(), boundary.getY());
				g.drawRect(boundary.getX(), boundary.getY(), boundary.getWidth(), boundary.getHeight());
			} else {
				g.drawString(layer.getName(), layer.getX(), layer.getY());
				g.drawRect(layer.getX(), layer.getY(), layer.getWidth(), layer.getHeight());
			}
		}

	}
}
