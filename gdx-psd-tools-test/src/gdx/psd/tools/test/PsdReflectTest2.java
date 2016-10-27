package gdx.psd.tools.test;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.keyroy.util.json.Json;

import psd.PsdFile;
import psd.framework.PsdReflectAdapter;
import psd.framework.PsdReflectApplicationAdapter;
import psd.reflect.PsdAn;
import psd.reflect.PsdGroup;
import psd.reflect.PsdImage;

public class PsdReflectTest2 {
	public static void main(String[] args) {
		try {
			Json json = new Json(PsdReflectTest2.class.getResourceAsStream("/xianshiguanka.json"));
			System.out.println(json);
			PsdFile psdFile = json.toObject(PsdFile.class);
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.width = psdFile.width / 2;
			config.height = psdFile.height / 2;
			System.out.println("screen : " + config.width + "x" + config.height);

			PsdReflectApplicationAdapter applicationAdapter = new PsdReflectApplicationAdapter() {
				@Override
				protected void onCreate() {
					set(new GroupReflect());
				}
			};

			new LwjglApplication(applicationAdapter, config);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PsdAn("xianshiguanka.json")
	public static final class GroupReflect extends PsdReflectAdapter {

		@PsdAn
		protected PsdImage buttonPlay;

		@Override
		public void onCreate(PsdGroup psdGroup) {
			buttonPlay.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println("click onReflectSuccess ");
					System.out.println();
				}
			});

			// Image image = (Image) (psdGroup.getPsdFolder().get(new
			// ElementFilter() {
			// @Override
			// public boolean accept(Element element) {
			// return "buttonPlay".equals(element.layerName);
			// }
			// }).getActor());
			initButtonStyle(psdGroup.findActor("buttonPlay"));
		}

		@PsdAn
		protected void buttonPlay() {
			System.out.println("click buttonPlay method");
		}

		@PsdAn
		protected void buttonPlay(PsdImage psdImage) {
			System.out.println("click buttonPlay method1 " + "    " + buttonPlay.equals(psdImage));
		}

		public static final void initButtonStyle(final Actor image) {
			image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
			image.addListener(new ClickListener() {
				private float pressedScale = 0.85f;
				private float duration = 0.1f;

				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
					play(image, pressedScale);
					return true;
				}

				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int b) {
					play(image, 1);
				}

				public void play(final Actor image, float scale) {
					if (image != null) {
						image.clearActions();
						ScaleToAction scaleToAction = new ScaleToAction();
						scaleToAction.setDuration(duration);
						scaleToAction.setScale(scale);
						image.addAction(scaleToAction);
					}
				}
			});
		}

	}
}
