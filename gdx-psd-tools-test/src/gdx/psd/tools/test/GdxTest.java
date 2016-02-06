package gdx.psd.tools.test;

import gdx.keyroy.psd.tools.models.EditorConfig;
import psd.PsdFile;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.keyroy.util.json.Json;

public class GdxTest {
	public static void main(String[] args) {
		try {
			EditorConfig.load();
			Json json = new Json(GdxTest.class.getResourceAsStream("/xianshiguanka.json"));
			System.out.println(json);
			final PsdFile psdFile = json.toObject(PsdFile.class);
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.width = psdFile.width / 2;
			config.height = psdFile.height / 2;
			System.out.println("screen : " + config.width + "x" + config.height);

			new LwjglApplication(new ApplicationAdapter() {
				Stage stage;

				@Override
				public void create() {
					stage = toStage(psdFile);
				}

				@Override
				public void render() {
					if (stage != null) {
						stage.draw();
					}
				}

			}, config);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static final Stage toStage(PsdFile psdFile) {
		Stage stage = new Stage(new ScalingViewport(Scaling.stretch, psdFile.width, psdFile.height,
				new OrthographicCamera()), new SpriteBatch());
		AssetManager assetManager = new AssetManager();
		for (psd.Element psdActor : psdFile.childs) {
			Actor actor = toActor(psdFile, psdActor, assetManager);
			stage.addActor(actor);
		}
		return stage;
	}

	private static final Actor toActor(PsdFile psdFile, psd.Element actor, AssetManager assetManager) {
		Actor rt = null;
		if (actor instanceof psd.Folder) {
			psd.Folder group = (psd.Folder) actor;
			Group gdxGroup = new Group();
			for (psd.Element cActor : group.childs) {
				gdxGroup.addActor(toActor(psdFile, cActor, assetManager));
			}
			rt = gdxGroup;
		} else if (actor instanceof psd.Pic) {
			psd.Pic image = (psd.Pic) actor;
			TextureRegion texture = null;
			if (EditorConfig.used_texture_packer) {
				if (assetManager.isLoaded(psdFile.psdName, TextureAtlas.class)) {
				} else {
					assetManager.load(psdFile.psdName, TextureAtlas.class);
					assetManager.finishLoading();
				}
				TextureAtlas textureAtlas = assetManager.get(psdFile.psdName, TextureAtlas.class);

				Array<AtlasRegion> array = textureAtlas.getRegions();
				for (AtlasRegion atlasRegion : array) {
					if (atlasRegion.name.equals(image.textureName)) {
						atlasRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
						texture = atlasRegion;
						break;
					}
				}
			} else {
				Texture tx = new Texture(Gdx.files.internal(image.textureName));
				tx.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				texture = new TextureRegion(tx);
			}

			rt = new Image(texture);
		} else if (actor instanceof psd.Text) {
			psd.Text psdLabel = (psd.Text) actor;
			Label.LabelStyle style = new LabelStyle(new BitmapFont(), new Color(psdLabel.r, psdLabel.g,
					psdLabel.b, psdLabel.a));
			rt = new Label(psdLabel.text, style);
		}
		if (rt != null) {
			rt.setBounds(actor.x, actor.y, actor.width, actor.height);
		}

		return rt;
	}
}
