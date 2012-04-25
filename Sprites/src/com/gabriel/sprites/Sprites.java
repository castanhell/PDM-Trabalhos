package com.gabriel.sprites;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.opengl.GLES20;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga
 * 
 * @author Nicolas Gramlich
 * @since 00:06:23 - 11.07.2010
 */
public class Sprites extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private static final int DIALOG_ALLOWDIAGONAL_ID = 0;

	private Camera mCamera;

	private RepeatingSpriteBackground mGrassBackground;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mPlayerTextureRegion;

	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;

	private DigitalOnScreenControl mDigitalOnScreenControl;

	private float x, y;
	private boolean     permiteCima = true, 
					   permiteBaixo = true, 
					permiteEsquerda = true, 
					 permiteDireita = true;
	
	private PhysicsHandler physicsHandler;
	private AnimatedSprite player;

	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				this.mCamera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mOnScreenControlTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 288, 288, TextureOptions.BILINEAR);
		this.mGrassBackground = new RepeatingSpriteBackground(CAMERA_WIDTH, CAMERA_HEIGHT, this.getTextureManager(), AssetBitmapTextureAtlasSource.create(this.getAssets(), "gfx/background_grass.png"), this.getVertexBufferObjectManager());
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this, "thesmurfschr01.png",0, 0, 12, 8);
		this.mBitmapTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(this.mGrassBackground);

		final Line linhaCima = new Line(-1, -1, CAMERA_WIDTH+1, -1, this.getVertexBufferObjectManager());
		scene.attachChild(linhaCima);
		final Line linhaBaixo = new Line(-1, CAMERA_HEIGHT, CAMERA_WIDTH+1, CAMERA_HEIGHT, this.getVertexBufferObjectManager());
		scene.attachChild(linhaBaixo);
		final Line linhaEsquerda = new Line(-1, -1, -1, CAMERA_HEIGHT, this.getVertexBufferObjectManager());
		scene.attachChild(linhaEsquerda);
		final Line linhaDireita = new Line(CAMERA_WIDTH+1, -1, CAMERA_WIDTH+1, CAMERA_HEIGHT, this.getVertexBufferObjectManager());
		scene.attachChild(linhaDireita);

		final float centerX = (CAMERA_WIDTH - this.mPlayerTextureRegion
				.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.mPlayerTextureRegion
				.getHeight()) / 2;
		// final Sprite player = new Sprite(centerX, centerY,
		// this.mplayerTextureRegion, this.getVertexBufferObjectManager());
		/* Create the sprite and add it to the scene. */
		player = new AnimatedSprite(centerX, centerY, 48,
				64, this.mPlayerTextureRegion,
				this.getVertexBufferObjectManager());

		this.physicsHandler = new PhysicsHandler(player);
		player.registerUpdateHandler(this.physicsHandler);
		player.stopAnimation(28);

		scene.attachChild(player);

		this.mDigitalOnScreenControl = new DigitalOnScreenControl(0, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(), this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, this.getVertexBufferObjectManager(),
				new IOnScreenControlListener() {
					@Override
					public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {

						if (Sprites.this.x == pValueX && Sprites.this.y == pValueY)
							return;
						
						Sprites.this.x = pValueX;
						Sprites.this.y = pValueY;

						if (Sprites.this.x == -1.0 && Sprites.this.y == 0.0 && Sprites.this.permiteEsquerda) {
							Sprites.this.esquerda();
						} else if (Sprites.this.x == 0.0 && Sprites.this.y == 1.0 && Sprites.this.permiteBaixo) {
							Sprites.this.baixo();
						} else if (Sprites.this.x == 1.0 && Sprites.this.y == 0.0 && Sprites.this.permiteDireita) {
							Sprites.this.direita();
						} else if (Sprites.this.x == 0.0 && Sprites.this.y == -1.0 && Sprites.this.permiteCima) {
							Sprites.this.cima();
						} else {
							Sprites.this.para();
						}
					}
				});
		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
		this.mDigitalOnScreenControl.getControlBase().setScale(1.25f);
		this.mDigitalOnScreenControl.getControlKnob().setScale(1.25f);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();
		
		scene.setChildScene(this.mDigitalOnScreenControl);
		
		/* The actual collision-checking. */
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				if(player.collidesWith(linhaEsquerda)) {
					if (Sprites.this.permiteEsquerda)
						Sprites.this.para();
					Sprites.this.permiteEsquerda = false;
				} else {
					Sprites.this.permiteEsquerda = true;
				}
				
				if(player.collidesWith(linhaCima)) {
					if (Sprites.this.permiteCima)
						Sprites.this.para();
					Sprites.this.permiteCima = false;
				} else {
					Sprites.this.permiteCima = true;
				}
				
				if(player.collidesWith(linhaDireita)) {
					if (Sprites.this.permiteDireita)
						Sprites.this.para();
					Sprites.this.permiteDireita = false;
				} else {
					Sprites.this.permiteDireita = true;
				}
				
				if(player.collidesWith(linhaBaixo)) {
					if (Sprites.this.permiteBaixo)
						Sprites.this.para();
					Sprites.this.permiteBaixo = false;
				} else {
					Sprites.this.permiteBaixo = true;
				}
			}
		});

		return scene;
	}
	
	public void esquerda() {
		physicsHandler.setVelocity(-100, 0);
		player.animate(new long[] { 200, 200, 200 }, 39, 39+2, true);
	}
	
	public void baixo(){
		physicsHandler.setVelocity(0, 100);
		player.animate(new long[] { 200, 200, 200 }, 27, 27+2, true);
	}
	
	public void direita(){
		physicsHandler.setVelocity(100, 0);
		player.animate(new long[] { 200, 200, 200 }, 15, 15+2, true);
	}
	
	public void cima() {
		physicsHandler.setVelocity(0, -100);
		player.animate(new long[] { 200, 200, 200 }, 3, 3+2, true);
	}
	
	public void para(){
		physicsHandler.setVelocity(0, 0);
		player.stopAnimation();
	}
}
