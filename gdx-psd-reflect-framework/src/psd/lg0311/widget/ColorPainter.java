package psd.lg0311.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ColorPainter extends Actor {
	private ShapeRenderer renderer;
	private boolean drawSelf;
	private boolean fillStage;

	public ColorPainter(Color color) {
		this(color, false);
	}

	public ColorPainter(Color color, boolean drawSelf) {
		this.drawSelf = drawSelf;
		renderer = new ShapeRenderer();
		renderer.setColor(color);
	}

	public ColorPainter(int rgba) {
		this(new Color(rgba));
	}

	//
	public ColorPainter setFillStage(boolean fillStage) {
		this.fillStage = fillStage;
		return this;
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
		if (fillStage) {
			renderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		} else {
			if (drawSelf) {
				renderer.setProjectionMatrix(batch.getProjectionMatrix());
				renderer.setTransformMatrix(batch.getTransformMatrix());
				renderer.rect(getX(), getY(), getWidth(), getHeight());
			} else {
				renderer.rect(getX(), getY(), getWidth(), getHeight());
			}
		}
		//
		renderer.end();
		if (closeDrawing) {
			batch.begin();
		}
	}
}
