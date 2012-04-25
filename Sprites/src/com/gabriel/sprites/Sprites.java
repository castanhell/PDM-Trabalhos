package com.gabriel.sprites;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mplayerTextureRegion;

	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;

	private DigitalOnScreenControl mDigitalOnScreenControl;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interplayers
	// ===========================================================

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

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		this.mplayerTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(
						this.mBitmapTextureAtlas, this, "player.png", 0, 0, 3, 4);
		this.mBitmapTextureAtlas.load();

		this.mOnScreenControlTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		final float centerX = (CAMERA_WIDTH - this.mplayerTextureRegion
				.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.mplayerTextureRegion
				.getHeight()) / 2;
		// final Sprite player = new Sprite(centerX, centerY,
		// this.mplayerTextureRegion, this.getVertexBufferObjectManager());
		/* Create the sprite and add it to the scene. */
		final AnimatedSprite player = new AnimatedSprite(centerX, centerY, 48,
				64, this.mplayerTextureRegion,
				this.getVertexBufferObjectManager());

		final PhysicsHandler physicsHandler = new PhysicsHandler(player);
		player.registerUpdateHandler(physicsHandler);

		scene.attachChild(player);

		this.mDigitalOnScreenControl = new DigitalOnScreenControl(0,
				CAMERA_HEIGHT
						- this.mOnScreenControlBaseTextureRegion.getHeight(),
				this.mCamera, this.mOnScreenControlBaseTextureRegion,
				this.mOnScreenControlKnobTextureRegion, 0.1f,
				this.getVertexBufferObjectManager(),
				new IOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {
						 physicsHandler.setVelocity(
								 pValueX * 100, pValueY * 100);

						//System.out.println(pValueX + ")(" + pValueY);
						
						if(pValueX == -1.0 && pValueY == 0.0) {
							// esquerda
							player.animate(new long[] { 200, 200, 200 }, 9, 11, true);
						} else if(pValueX == 0.0 && pValueY == 1.0) {
							// baixo
							player.animate(new long[] { 200, 200, 200 }, 6, 8, true);
						} else if(pValueX == 1.0 && pValueY == 0.0) {
							// direita
							player.animate(new long[] { 200, 200, 200 }, 3, 5, true);
						} else if(pValueX == 0.0 && pValueY == -1.0) {
							// cima
							player.animate(new long[] { 200, 200, 200 }, 0, 2, true);
						} else if(pValueX == 0.0 && pValueY == 0.0) {
							// cima
							player.stopAnimation();
						}
						
						//player.animate(new long[] { 200, 200, 200 }, 6, 8, true);
						
						// switch (pWaypointIndex) {
						// case 0:
						// break;
						// case 1:
						// player.animate(new long[] { 200, 200, 200 }, 3, 5,
						// true);
						// break;
						// case 2:
						// player.animate(new long[] { 200, 200, 200 }, 0, 2,
						// true);
						// break;
						// case 3:
						// player.animate(new long[] { 200, 200, 200 }, 9, 11,
						// true);
						// break;
						// }
					}
				});
		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(
				GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
		this.mDigitalOnScreenControl.getControlBase().setScale(1.25f);
		this.mDigitalOnScreenControl.getControlKnob().setScale(1.25f);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();

		scene.setChildScene(this.mDigitalOnScreenControl);

		return scene;
	}

/*
	@Override
	public void onGameCreated() {
		this.showDialog(DIALOG_ALLOWDIAGONAL_ID);
	}
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(final int pID) {
		switch (pID) {
		case DIALOG_ALLOWDIAGONAL_ID:
			return new AlertDialog.Builder(this)
					.setTitle("Setup...")
					.setMessage(
							"Do you wish to allow diagonal directions on the OnScreenControl?")
					.setPositiveButton("Yes",
							new DialogInterplayer.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterplayer pDialog,
										final int pWhich) {
									Sprites.this.mDigitalOnScreenControl
											.setAllowDiagonal(true);
								}
							})
					.setNegativeButton("No",
							new DialogInterplayer.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterplayer pDialog,
										final int pWhich) {
									Sprites.this.mDigitalOnScreenControl
											.setAllowDiagonal(false);
								}
							}).create();
		}
		return super.onCreateDialog(pID);
	}*/

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

//
// import org.andengine.engine.camera.Camera;
// import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
// import org.andengine.engine.options.EngineOptions;
// import org.andengine.engine.options.ScreenOrientation;
// import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
// import org.andengine.entity.IEntity;
// import org.andengine.entity.modifier.LoopEntityModifier;
// import org.andengine.entity.modifier.PathModifier;
// import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
// import org.andengine.entity.modifier.PathModifier.Path;
// import org.andengine.entity.scene.Scene;
// import org.andengine.entity.scene.background.RepeatingSpriteBackground;
// import org.andengine.entity.sprite.AnimatedSprite;
// import org.andengine.entity.util.FPSLogger;
// import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
// import
// org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
// import
// org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
// import org.andengine.opengl.texture.region.ITextureRegion;
// import org.andengine.opengl.texture.region.TiledTextureRegion;
// import org.andengine.ui.activity.SimpleBaseGameActivity;
//
// /**
// * (c) 2010 Nicolas Gramlich
// * (c) 2011 Zynga
// *
// * @author Nicolas Gramlich
// * @since 13:58:48 - 19.07.2010
// */
// public class Sprites extends SimpleBaseGameActivity {
// // ===========================================================
// // Constants
// // ===========================================================
//
// private Camera mCamera;
//
// private static final int CAMERA_WIDTH = 720;
// private static final int CAMERA_HEIGHT = 480;
//
// // ===========================================================
// // Fields
// // ===========================================================
//
// private RepeatingSpriteBackground mGrassBackground;
//
// private BitmapTextureAtlas mBitmapTextureAtlas;
// private TiledTextureRegion mPlayerTextureRegion;
//
// private BitmapTextureAtlas mOnScreenControlTexture;
// private ITextureRegion mOnScreenControlBaseTextureRegion;
// private ITextureRegion mOnScreenControlKnobTextureRegion;
//
// private ITextureRegion mplayerTextureRegion;
// private DigitalOnScreenControl mDigitalOnScreenControl;
//
// // ===========================================================
// // Constructors
// // ===========================================================
//
// // ===========================================================
// // Getter & Setter
// // ===========================================================
//
// // ===========================================================
// // Methods for/from SuperClass/Interplayers
// // ===========================================================
//
// @Override
// public EngineOptions onCreateEngineOptions() {
// final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
//
// return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new
// RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
// }
//
// @Override
// public void onCreateResources() {
// BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
//
// this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),
// 128, 128);
// this.mPlayerTextureRegion =
// BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas,
// this, "player.png", 0, 0, 3, 4);
// this.mGrassBackground = new RepeatingSpriteBackground(CAMERA_WIDTH,
// CAMERA_HEIGHT, this.getTextureManager(),
// AssetBitmapTextureAtlasSource.create(this.getAssets(),
// "gfx/background_grass.png"), this.getVertexBufferObjectManager());
//
// //(pBitmapTextureAtlas, pContext, pAssetPath, pTextureX, pTextureY)
//
// this.mBitmapTextureAtlas.load();
// }
//
// @Override
// public Scene onCreateScene() {
// this.mEngine.registerUpdateHandler(new FPSLogger());
//
// final Scene scene = new Scene();
// scene.setBackground(this.mGrassBackground);
//
// /* Calculate the coordinates for the player, so its centered on the camera.
// */
// final float centerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getWidth()) /
// 2;
// final float centerY = (CAMERA_HEIGHT - this.mPlayerTextureRegion.getHeight())
// / 2;
//
// /* Create the sprite and add it to the scene. */
// final AnimatedSprite player = new AnimatedSprite(centerX, centerY, 48, 64,
// this.mPlayerTextureRegion, this.getVertexBufferObjectManager());
//
// final Path path = new Path(5).to(20, 10).to(10, CAMERA_HEIGHT -
// 74).to(CAMERA_WIDTH - 58, CAMERA_HEIGHT - 74).to(CAMERA_WIDTH - 58,
// 10).to(10, 10);
//
// player.registerEntityModifier(new LoopEntityModifier(new PathModifier(30,
// path, null, new IPathModifierListener() {
// @Override
// public void onPathStarted(final PathModifier pPathModifier, final IEntity
// pEntity) {
//
// }
//
// @Override
// public void onPathWaypointStarted(final PathModifier pPathModifier, final
// IEntity pEntity, final int pWaypointIndex) {
// switch(pWaypointIndex) {
// case 0:
// player.animate(new long[]{200, 200, 200}, 6, 8, true);
// break;
// case 1:
// player.animate(new long[]{200, 200, 200}, 3, 5, true);
// break;
// case 2:
// player.animate(new long[]{200, 200, 200}, 0, 2, true);
// break;
// case 3:
// player.animate(new long[]{200, 200, 200}, 9, 11, true);
// break;
// }
// }
//
// @Override
// public void onPathWaypointFinished(final PathModifier pPathModifier, final
// IEntity pEntity, final int pWaypointIndex) {
//
// }
//
// @Override
// public void onPathFinished(final PathModifier pPathModifier, final IEntity
// pEntity) {
//
// }
// })));
// scene.attachChild(player);
//
// return scene;
// }
//
// public ITextureRegion getmOnScreenControlBaseTextureRegion() {
// return mOnScreenControlBaseTextureRegion;
// }
//
// public void setmOnScreenControlBaseTextureRegion(
// ITextureRegion mOnScreenControlBaseTextureRegion) {
// this.mOnScreenControlBaseTextureRegion = mOnScreenControlBaseTextureRegion;
// }
//
// // ===========================================================
// // Methods
// // ===========================================================
//
// // ===========================================================
// // Inner and Anonymous Classes
// // ===========================================================
// }
