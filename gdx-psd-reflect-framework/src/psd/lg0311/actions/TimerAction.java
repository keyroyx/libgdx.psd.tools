package psd.lg0311.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

public class TimerAction extends Action {
	private float duration, time;

	public TimerAction() {
		this(1f);
	}

	public TimerAction(float duration) {
		this.duration = duration;
	}

	@Override
	public boolean act(float delta) {
		boolean rt = false;
		time += delta;
		if (time > duration) {
			rt = onTimer();
			time = time - duration;
		}
		return rt;
	}

	protected boolean onTimer() {
		return false;
	}

	/** Causes the delay to be complete. */
	public void finish() {
		time = duration;
	}

	public void restart() {
		time = 0;
	}

	/** Gets the time spent waiting for the delay. */
	public float getTime() {
		return time;
	}

	/** Sets the time spent waiting for the delay. */
	public void setTime(float time) {
		this.time = time;
	}

	public float getDuration() {
		return duration;
	}

	/** Sets the length of the delay in seconds. */
	public void setDuration(float duration) {
		this.duration = duration;
	}

}
