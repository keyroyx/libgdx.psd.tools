package psd.lg0311.widget;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import psd.loaders.FileManage;

public class FntWidget extends Actor {
	public static boolean drawBox = true;
	private final BitmapFont bitmapFont;
	private final float fntHeight;
	private String text;
	private float scale = 1;
	private float textSize;
	private boolean wrapped;
	private int align = Align.left | Align.center;

	public FntWidget(String fntPath) {
		this.bitmapFont = createBitmapFont(fntPath);
		this.bitmapFont.getData().setScale(1);
		this.fntHeight = bitmapFont.getLineHeight();
		this.textSize = fntHeight;
	}

	public final FntWidget setAlign(int align) {
		this.align = align;
		return this;
	}

	public final FntWidget setText(String text) {
		this.text = text;
		if (this.text != null) {
			wrapped = this.text.indexOf('\n') != -1;
		}
		return this;
	}

	public final FntWidget setTextSize(float size) {
		if (size > 0) {
			this.textSize = size;
			scale = textSize / fntHeight;
			bitmapFont.getData().setScale(scale);
		}
		return this;
	}

	public final FntWidget setTextColor(String color) {
		try {
			if (color != null) {
				setColor(Color.valueOf(color));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (text != null) {
			bitmapFont.getData().setScale(scale);
			bitmapFont.setColor(getColor());
			if (wrapped) {
				drawWrappedText(batch, bitmapFont, text, this, 0, align);
			} else {
				drawText(batch, bitmapFont, text, this, align);
			}
			drawRect(batch, this, Color.YELLOW);
		}
	}

	//
	protected final void drawText(Batch batch, BitmapFont bitmapFont, String text, Actor actor, int align) {
		if (bitmapFont != null) {
			float x = actor.getX();
			float y = actor.getY() + textSize + (actor.getHeight() - textSize - bitmapFont.getAscent()) / 2;

			boolean isDrawing = batch.isDrawing();
			if (isDrawing == false) {
				batch.begin();
			}
			bitmapFont.draw(batch, text, x, y, actor.getWidth(), align, false);
			if (isDrawing == false) {
				batch.end();
			}
		}
	}

	protected final void drawWrappedText(Batch batch, BitmapFont bitmapFont, String text, Actor actor,
			int padding, int align) {
		if (bitmapFont != null) {
			float x = actor.getX();
			float y = actor.getY() + actor.getHeight();
			boolean isDrawing = batch.isDrawing();
			if (isDrawing == false) {
				batch.begin();
			}
			if (padding != 0) {
				bitmapFont.draw(batch, text, x + padding, y - padding, actor.getWidth() - padding * 2, align,
						true);
			} else {
				bitmapFont.draw(batch, text, x, y, actor.getWidth(), align, true);
			}

			if (isDrawing == false) {
				batch.end();
			}
		}
	}

	protected static final boolean hasAlign(int align, int target) {
		return (align & target) == target;
	}

	//
	private static GlyphLayout glyphLayout = new GlyphLayout();

	public static final Rectangle getTextBounds(String text, BitmapFont bitmapFont) {
		glyphLayout.setText(bitmapFont, text);
		Rectangle rectangle = new Rectangle();
		rectangle.setSize(glyphLayout.width, glyphLayout.height);
		return rectangle;
	}

	private static final HashMap<String, BitmapFont> MAP = new HashMap<String, BitmapFont>();

	private static final BitmapFont createBitmapFont(String fntPath) {
		BitmapFont bitmapFont = MAP.get(fntPath);
		if (bitmapFont == null) {
			BitmapFontData fontData = new BitmapFontData(FileManage.file(fntPath), false);
			Array<TextureRegion> regions = new Array<TextureRegion>(fontData.imagePaths.length);
			for (int i = 0; i < fontData.imagePaths.length; i++) {
				String imagePath = fontData.imagePaths[i];
				Texture texture = FileManage.get(imagePath, Texture.class);
				regions.add(new TextureRegion(texture));
			}
			fontData.getImagePaths();
			bitmapFont = new BitmapFont(fontData, regions, false);
			MAP.put(fntPath, bitmapFont);
		}
		return bitmapFont;
	}

	protected static ShapeRenderer shapeRenderer = new ShapeRenderer();

	protected static final void drawRect(Batch batch, float x, float y, float w, float h, Color color) {
		boolean closeDrawing = batch.isDrawing();
		if (closeDrawing) {
			batch.end();
		}
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(color);
		shapeRenderer.rect(x, y, w, h);
		shapeRenderer.end();
		if (closeDrawing) {
			batch.begin();
		}
	}

	protected static final void drawRect(Batch batch, Actor actor, Color color) {
		if (drawBox) {
			drawRect(batch, actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight(), color);
		}
	}

	public static String getId() {
		return "fnt";
	}

}
