package psd.lg0311.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import psd.reflect.PsdImage;

public class ProgressBarWidget extends WidgetGroup {
	private final Rectangle widgetAreaBounds = new Rectangle();
	private final Rectangle scissorBounds = new Rectangle();
	//
	private boolean isVertical;
	private Texture texture;
	private float length;
	private boolean reverse;
	private float duration;
	private PercentAction percentAction;

	public ProgressBarWidget(PsdImage image) {
		TextureRegionDrawable drawable = (TextureRegionDrawable) image.getDrawable();
		texture = drawable.getRegion().getTexture();
		setSize(image.getWidth(), image.getHeight());
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	//
	public final void setPercent(float percent) {
		setPercent(percent, true);
	}

	//
	public final void setPercent(float percent, boolean action) {
		if (percent > 1) {
			percent = 1;
		} else if (percent < 0) {
			percent = 0;
		}
		float goal = 0;
		if (isVertical) {
			goal = texture.getHeight() * percent;
		} else {
			goal = texture.getWidth() * percent;
		}
		action(goal - length, action);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {

		// Setup transform for this group.
		applyTransform(batch, computeTransform());

		// Caculate the scissor bounds based on the batch transform, the
		// available widget area and the camera transform. We need to
		// project those to screen coordinates for OpenGL ES to consume.

		if (isVertical) {
			if (reverse) {
				widgetAreaBounds.setSize(getWidth(), getHeight() - length);
			} else {
				widgetAreaBounds.setSize(getWidth(), length);
			}
		} else {
			widgetAreaBounds.setSize(length, getHeight());
		}

		getStage().calculateScissors(widgetAreaBounds, scissorBounds);
		//
		float scale = Gdx.graphics.getDensity();
		if (scale < 1) {
			scale = 1;
		}

		// Enable scissors for widget area and draw the widget.
		if (ScissorStack.pushScissors(scissorBounds)) {
			batch.draw(texture, 0, 0, 0, 0, texture.getWidth(), texture.getHeight());
			batch.flush();
			ScissorStack.popScissors();
		}
		resetTransform(batch);

	} //

	public void setVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	private final void action(float offset, boolean action) {
		if (action && duration > 0) {
			if (percentAction == null) {
				percentAction = new PercentAction();
				percentAction.setInterpolation(Interpolation.swing);
			}
			percentAction.setDuration(duration);
			percentAction.setOffset(offset);
			percentAction.reset();
			removeAction(percentAction);
			addAction(percentAction);
		} else {
			length += offset;
		}
	}

	private class PercentAction extends TemporalAction {
		private float from;
		private float offset;

		public void setOffset(float offset) {
			this.offset = offset;
		}

		@Override
		protected void begin() {
			from = length;
		}

		@Override
		protected void update(float percent) {
			length = from + offset * percent;
		}
	}

	public static String getId() {
		return "pb";
	}

}
