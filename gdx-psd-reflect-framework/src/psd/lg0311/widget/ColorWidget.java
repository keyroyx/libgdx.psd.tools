package psd.lg0311.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ColorWidget extends Actor {
	private ShapeRenderer renderer;
	private boolean fillScreen;

	public ColorWidget(Color color) {
		renderer = new ShapeRenderer();
		renderer.setColor(color);
	}

	public ColorWidget(Color color, boolean fillScreen) {
		this(color);
		setFillScreen(fillScreen);
	}

	public ColorWidget(int rgba) {
		this(new Color(rgba));
	}

	public void setFillScreen(boolean fillScreen) {
		this.fillScreen = fillScreen;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		boolean closeDrawing = batch.isDrawing();
		if (closeDrawing) {
			batch.end();
		}
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		renderer.begin(ShapeType.Filled);
		if (fillScreen) {
			renderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		} else {
			renderer.setProjectionMatrix(batch.getProjectionMatrix());
			renderer.setTransformMatrix(batch.getTransformMatrix());
			renderer.rect(getX(), getY(), getWidth(), getHeight());
		}
		//
		renderer.end();
		if (closeDrawing) {
			batch.begin();
		}
	}

	public static String getId() {
		return "color";
	}

}
