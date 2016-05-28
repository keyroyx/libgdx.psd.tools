package psd.lg0311.widget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import psd.lg0311.actions.TimerAction;
import psd.reflect.PsdGroup;

public class FrameAnimationWidget extends Group {
	protected float duration = 1f;
	protected float delay;
	protected int currentIndex;
	protected int loop;
	protected int play;

	public FrameAnimationWidget(PsdGroup group) {
		setSize(group.getWidth(), group.getWidth());
		Array<Actor> copy = new Array<Actor>(group.getChildren());
		for (Actor actor : copy) {
			addActor(actor);
		}
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
		showIndex(currentIndex);
	}

	public void startAnimation() {
		clearActions();
		if (getChildren().size > 0) {
			TimerAction timerAction = new TimerAction(duration / getChildren().size) {
				@Override
				protected boolean onTimer() {
					showIndex(currentIndex);
					currentIndex++;
					if (currentIndex == getChildren().size) {
						currentIndex = 0;
						return true;
					}
					return false;
				}
			};
			//
			if (loop == -1) {
				addAction(Actions.forever(Actions.sequence(Actions.delay(delay), timerAction)));
			} else {
				addAction(Actions.repeat(loop, Actions.sequence(Actions.delay(delay), timerAction)));
			}

		}
	}

	public void stopAnimation() {
		clearActions();
	}

	private final void showIndex(int index) {
		Array<Actor> array = getChildren();
		for (int i = 0; i < array.size; i++) {
			array.get(i).setVisible(i == index);
		}
	}

	public static String getId() {
		return "fs";
	}
}
