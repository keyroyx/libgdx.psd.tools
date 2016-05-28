package psd.lg0311;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("unchecked")
public class GroupProxy {
	protected static Vector2 _v2 = new Vector2();
	//
	protected Group group;

	public GroupProxy(Group group) {
		this.group = group;
	}

	public final <T extends Group> T getSource() {
		return (T) group;
	}

	public final void addActor(Actor actor) {
		group.addActor(actor);
	}

	public final void addAction(Action action) {
		group.addAction(action);
	}

	public final void remove() {
		getSource().remove();
	}

	public void getBorder(Rectangle rectangle) {
		rectangle.set(group.getX(), group.getY(), group.getWidth(), group.getHeight());
	}

	protected final Vector2 localToAscendantCoordinates(Actor actor, Vector2 v2) {
		return actor.localToAscendantCoordinates(getSource(), v2);
	}

	protected final Vector2 localToAscendantCoordinates(Actor actor) {
		_v2.set(0, 0);
		return localToAscendantCoordinates(group, _v2);
	}

	protected final void clearActions() {
		group.clearActions();
	}

	protected final void removeAction(Action action) {
		group.removeAction(action);
	}

	protected final void removeAction(Class<? extends Action> clazz) {
		Array<Action> array = getSource().getActions();
		for (int i = 0; i < array.size; i++) {
			Action action = array.get(i);
			if (action.getClass().equals(clazz)) {
				getSource().removeAction(action);
				break;
			}
		}
	}

	// 设置对象到下中
	protected final void bottom(Actor actor) {
		actor.setPosition(group.getWidth() / 2, 0, Align.bottom);
	}

	// 设置边界
	protected final void copyBorder(Actor from, Actor to) {
		to.setBounds(from.getX(), from.getY(), from.getWidth(), from.getHeight());
	}

	public final void print(String msg) {
		if (PsdAdapter.debug) {
			System.out.println(getMethodName() + msg);
		}
	}

	public final void print(Object... msg) {
		if (PsdAdapter.debug) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < msg.length; i++) {
				buffer.append(msg[i].toString());
				if (i != msg.length - 1) {
					buffer.append("    ");
				}
			}
			System.out.println(getMethodName() + buffer.toString());
		}
	}

	private final String getMethodName() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		return "[" + stacktrace[3].getLineNumber() + " " + getClass().getSimpleName() + "."
				+ stacktrace[3].getMethodName() + "]";
	}

}
