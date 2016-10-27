package gdx.psd.tools.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.keyroy.util.json.Json;

import psd.PsdFile;
import psd.framework.PsdReflectAdapter;
import psd.reflect.PsdAn;
import psd.reflect.PsdGroup;
import psd.reflect.PsdImage;
import psd.reflect.PsdStage;

public class PsdReflectTest {
	public static void main(String[] args) {
		try {
			Json json = new Json(PsdReflectTest.class.getResourceAsStream("/xianshiguanka.json"));
			System.out.println(json);
			PsdFile psdFile = json.toObject(PsdFile.class);
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.width = psdFile.width / 2;
			config.height = psdFile.height / 2;
			System.out.println("screen : " + config.width + "x" + config.height);
			new LwjglApplication(new ApplicationAdapter() {
				GroupReflect groupReflect = new GroupReflect();
				PsdStage stage;

				@Override
				public void create() {
					stage = PsdStage.reflect(groupReflect);
					Gdx.input.setInputProcessor(stage);
				}

				@Override
				public void render() {
					Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
					if (stage != null) {
						stage.act();
						stage.draw();
					} else {
						System.out.println("stage =  null");
					}
				}

			}, config);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PsdAn("xianshiguanka.json")
	public static final class GroupReflect extends PsdReflectAdapter {

		@PsdAn("buttonPlay")
		protected PsdImage buttonPlayPic;

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

		}

		@PsdAn
		protected void buttonPlay() {
			System.out.println("click buttonPlay method");
		}

		@PsdAn
		protected void buttonPlay(PsdImage psdImage) {
			System.out.println("click buttonPlay method1 " + "    " + buttonPlay.equals(psdImage));
		}

		@PsdAn
		protected void buttonPlay(psd.Pic pic) {
			System.out.println("click buttonPlay method2 " + "    " + this.buttonPlayPic.equals(pic));
		}

		@PsdAn
		protected void buttonPlay(PsdImage psdImage, psd.Pic pic) {
			System.out.println("click buttonPlay method3 " + "    " + buttonPlay.equals(psdImage) + "    "
					+ buttonPlayPic.equals(pic));
		}

		@PsdAn
		protected void buttonPlay(psd.Pic pic, PsdImage psdImage) {
			System.out.println("click buttonPlay method4 " + "    " + buttonPlay.equals(psdImage) + "    "
					+ buttonPlayPic.equals(pic));
		}

		@PsdAn("buttonPlay")
		protected void buttonPlay5(psd.Pic pic, PsdImage psdImage) {
			System.out.println("click buttonPlay method5 " + "    " + buttonPlay.equals(psdImage) + "    "
					+ buttonPlayPic.equals(pic));
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
