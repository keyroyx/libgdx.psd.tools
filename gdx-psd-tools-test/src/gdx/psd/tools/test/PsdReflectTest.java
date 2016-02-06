package gdx.psd.tools.test;

import psd.Element;
import psd.ElementFilter;
import psd.PsdFile;
import psd.reflect.PsdAn;
import psd.reflect.PsdGroup;
import psd.reflect.PsdImage;
import psd.reflect.PsdReflectListener;
import psd.reflect.PsdStage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.keyroy.util.json.Json;

public class PsdReflectTest {
	public static void main(String[] args) {
		try {
			Json json = new Json(GdxTest.class.getResourceAsStream("/xianshiguanka.json"));
			System.out.println(json);
			final PsdFile psdFile = json.toObject(PsdFile.class);
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
	public static final class GroupReflect implements PsdReflectListener {

		@PsdAn("buttonPlay")
		protected psd.Pic buttonPlayPic;

		@PsdAn
		protected PsdImage buttonPlay;

		@Override
		public void onReflectSuccess(PsdGroup psdGroup) {
			buttonPlay.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println("click onReflectSuccess "
							+ buttonPlay.equals(buttonPlayPic.getUserObject()));
					System.out.println();
				}
			});

			Image image = (Image) (psdGroup.getPsdFolder().get(new ElementFilter() {
				@Override
				public boolean accept(Element element) {
					return "buttonPlay".equals(element.layerName);
				}
			}).getUserObject());
			initButtonStyle(image);
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

		public static final void initButtonStyle(final Image image) {
			image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
			image.addListener(new ClickListener() {
				private float pressedScale = 0.8f;
				private float duration = 0.1f;

				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
					// play(image, duration, pressedScale,
					// Interpolation.swingOut);
					play(image, duration, pressedScale, Interpolation.circleIn);
					return true;
				}

				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int b) {
					play(image, duration, 1, Interpolation.circleOut);
				}

				public void play(final Image image, float duration, float scale, Interpolation interpolation) {
					if (image != null) {
						image.clearActions();
						ScaleToAction scaleToAction = new ScaleToAction() {
							protected void update(float percent) {
								super.update(percent);
								image.setScale(image.getScaleX(), image.getScaleY());
							};
						};
						scaleToAction.setDuration(duration);
						scaleToAction.setScale(scale);
						scaleToAction.setInterpolation(interpolation);
						image.addAction(scaleToAction);
					}
				}
			});
		}
	}
}
