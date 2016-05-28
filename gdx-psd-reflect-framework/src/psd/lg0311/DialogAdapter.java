package psd.lg0311;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import psd.lg0311.widget.ColorPainter;
import psd.reflect.PsdGroup;

public abstract class DialogAdapter extends PsdAdapter {
	// 是否自动关闭
	private boolean isAutoClose = true;
	//
	private PsdAdapter parent;

	//
	@Override
	protected final void onCreate(PsdGroup psdGroup) {
		// 设置背景色
		setBackgroundColor(0x00000077);
		// 回调
		doCreate(psdGroup);
	}

	// 显示 DIALOG
	protected final float show(PsdGroup parent) {
		PsdGroup group = getSource();
		parent.addActor(group);
		return onShow(group);
	}

	// 显示
	protected float onShow(PsdGroup group) {
		// 动画播放时间
		float duration = 0.25f;
		group.setColor(1, 1, 1, 0);
		group.setOrigin(group.getWidth() / 2, group.getHeight() / 2);
		group.addAction(Actions.alpha(1, duration));
		group.setScale(0);
		group.addAction(Actions.scaleTo(1, 1, duration));
		return duration;
	}

	// 关闭 DIALOG
	protected final void close() {
		PsdGroup group = getSource();
		onClose(group);
		parent.hideDialog(this);
	}

	protected void onClose(PsdGroup group) {
		// 动画播放时间
		float duration = 0.25f;
		group.addAction(Actions.alpha(0, duration, Interpolation.sineOut));
		group.addAction(Actions.scaleTo(0, 0, duration, Interpolation.sineOut));
		group.addAction(Actions.delay(duration, new Action() {
			@Override
			public boolean act(float delta) {
				getSource().remove();
				return true;
			}
		}));
	}

	protected void setParent(PsdAdapter parent) {
		this.parent = parent;
	}

	// 设置关闭按钮
	protected final void setCloseButton(String layerName) {
		Actor actor = getSource().findActor(layerName);
		initButtonStyle(actor);
		actor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				close();
			}
		});
	}

	// 设置背景色
	protected final void setBackgroundColor(int rgba) {
		ColorPainter colorPainter = new ColorPainter(rgba);
		colorPainter.setSize(getSource().getWidth(), getSource().getHeight());
		colorPainter.setFillStage(true);
		getSource().addActorAt(0, colorPainter);
		//
		colorPainter.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// 是否自动关闭
				if (isAutoClose) {
					close();
				}
			}
		});
	}

	// 延迟显示按钮
	protected final void showDelayPop(Array<Actor> array) {
		array.reverse();
		showDelayPop(array, 0.6f, 0.4f);
	}

	// 延迟显示按钮
	protected final void showDelayPop(Array<Actor> array, float delay, float duration) {
		for (int i = 0; i < array.size; i++) {
			Actor actor = array.get(i);
			actor.setOrigin(actor.getWidth() / 2, actor.getHeight() / 2);
			actor.setColor(1, 1, 1, 0);
			actor.setScale(0);
			Action action = Actions.delay(delay * (i + 1), Actions.parallel(Actions.alpha(1, duration),
					Actions.scaleTo(1, 1, duration, Interpolation.swingOut)));
			actor.addAction(action);
		}
	}

	public void setAutoClose(boolean isAutoClose) {
		this.isAutoClose = isAutoClose;
	}

	// 构造函数
	protected abstract void doCreate(PsdGroup psdGroup);
}
