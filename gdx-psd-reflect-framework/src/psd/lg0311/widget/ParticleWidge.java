package psd.lg0311.widget;

import java.util.Enumeration;
import java.util.Hashtable;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import psd.loaders.FileManage;

public class ParticleWidge extends Actor {

	private static final Hashtable<String, ParticleEffectPool> pools = new Hashtable<String, ParticleEffectPool>();
	private ParticleEffect particleEffect;
	private boolean playOnce;
	private String path;
	private OnParticlePlayOnceListener listener;

	public ParticleWidge(String path) {
		this.path = path;
		ParticleEffectPool pool = pools.get(path);
		if (pool == null) {
			FileHandle particleFile = FileManage.file(path);
			particleEffect = new ParticleEffect();
			particleEffect.load(particleFile, FileManage.file(particleFile.parent().name()));
			pool = new ParticleEffectPool(particleEffect, 10, 50);
			pools.put(path, pool);
		} else {
			particleEffect = pool.obtain();
		}
		setSize(1, 1);
		stop();
	}

	public String getPath() {
		return path;
	}

	public final void play() {
		particleEffect.start();
	}

	public final void playOnce() {
		playOnce = true;
		particleEffect.start();
	}

	public final void playOnce(OnParticlePlayOnceListener listener) {
		this.listener = listener;
		playOnce = true;
		particleEffect.start();
	}

	public final void stop() {
		particleEffect.allowCompletion();
	}

	public final ParticleEffect getParticleEffect() {
		return particleEffect;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		particleEffect.update(delta);
		if (playOnce && particleEffect.isComplete()) {
			stop();
			playOnce = false;
			if (listener != null) {
				OnParticlePlayOnceListener playListener = listener;
				listener = null;
				playListener.onPlayOver(ParticleWidge.this);
			}
		}
	}

	@Override
	protected void positionChanged() {
		particleEffect.setPosition(getX(Align.center), getY(Align.bottom));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (particleEffect != null && isVisible()) {
			particleEffect.draw(batch);
		}
	}

	public static final void clean() {
		Enumeration<ParticleEffectPool> enumeration = pools.elements();
		while (enumeration.hasMoreElements()) {
			ParticleEffectPool particleEffectPool = (ParticleEffectPool) enumeration.nextElement();
			particleEffectPool.clear();
		}
		pools.clear();
	}

	public static interface OnParticlePlayOnceListener {
		public void onPlayOver(ParticleWidge particleWidge);
	}

	public static String getId() {
		return "ps";
	}
}
