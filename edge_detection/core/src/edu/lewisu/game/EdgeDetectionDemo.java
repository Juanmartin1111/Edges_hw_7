package edu.lewisu.game;

//import javax.swing.text.Position;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
//import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class EdgeDetectionDemo extends ApplicationAdapter {
    SpriteBatch batch;
	Texture img;
	Texture background1, background2;
    float imgX, imgY;
	float imgWidth, imgHeight;
	float player;
	int WIDTH, HEIGHT;
	float WORLDMAXx, WORLDMAXy, WORLDMINx, WORLDMINy;
	OrthographicCamera cam;
	float WORLDWIDTH, WORLDHEIGHT;
	LabelStyle labelStyle;
	Label label;
	CameraShake shaker;
	private float backgroundSpeed = 5;
	private float backgroundX = 0;

	public void setupLabelStyle() {
		labelStyle = new LabelStyle();
		labelStyle.font = new BitmapFont(Gdx.files.internal("fonts/scaryfont.fnt"));
	}
    @Override
    public void create () {
        batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		background1 = new Texture("revelations.png");
		background2 = new Texture("revelations.png");
        WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();	//Viewport or screen		
		WORLDWIDTH = WORLDMAXx + WORLDMINx;
		WORLDHEIGHT = WORLDMAXy + WORLDMINy;	// view of the world 
		WORLDMAXx = 2*WIDTH;
		WORLDMINx = -2*WIDTH;
		WORLDMAXy = 2*HEIGHT;
		WORLDMINy = -2*HEIGHT;
		imgX = 175;
        imgY = 175;
        imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		player = imgWidth + imgHeight;
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH/2,HEIGHT/2);
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		System.out.println(cam.position.x + " " + cam.position.y);
		setupLabelStyle();
		// now create the label
		label = new Label("Welcome!", labelStyle);
		label.setPosition(20, 400); //world coordinate == screen coordinate at the beginning
		shaker = new CameraShake(cam, 100, batch, null, 10, 2);
		
	}

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Keys.A)) {
            imgX-=5;
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            imgX+=5;
        }
        if (Gdx.input.isKeyPressed(Keys.W)) {
            imgY+=5;
        }
        if (Gdx.input.isKeyPressed(Keys.S)) {
            imgY-=5; 
		}
		if (Gdx.input.isKeyPressed(Keys.J)) {
            locky();
		}
		if (Gdx.input.isKeyPressed(Keys.U)) {
            lockCoordinates(imgX,imgY);
		}
	}
	public Vector2 getViewPortOrigin() {
		return new Vector2(cam.position.x - WIDTH/2, cam.position.y - HEIGHT/2);
	}
	public Vector2 getScreenCoordinates() {
		Vector2 viewPortOrigin = getViewPortOrigin();
		return new Vector2(imgX - viewPortOrigin.x, imgY - viewPortOrigin.y);
	}
    public void panCoordinates(float border) {
		Vector2 screenPos = getScreenCoordinates();
		if (screenPos.x > WIDTH - imgWidth - border) { // about to go off viewport horizontally
			if (imgX + imgWidth > WORLDMAXx - border) {	// abput to go off the world
				lockCoordinates(WORLDWIDTH, WORLDHEIGHT);
			} else {	// just pan the camera
				cam.position.x = cam.position.x + screenPos.x - WIDTH + imgWidth + border;
				cam.update();
				System.out.println(cam.position.x);
				batch.setProjectionMatrix(cam.combined);
			}
		}
		if (screenPos.x < border) {	//about to leave the viewport left
			// move the camera left - subtrct the amount we are over the border from
			// the current camera position
			if (imgX + imgWidth < WORLDMINx - border) {
				lockCoordinates(WORLDWIDTH,WORLDHEIGHT);
			}
			cam.position.x = cam.position.x - (border - screenPos.x);
			System.out.println(cam.position.x);
			cam.update();
			batch.setProjectionMatrix(cam.combined);
		}
		if (screenPos.y > HEIGHT - imgHeight - border) {	// about to go off the viewport vertically
			if (imgY + imgHeight > WORLDMAXy - border) {	// out of real estate in y direction
				lastlyCoor();
			} else {	// keep paning we have more room
				cam.position.y = cam.position.y + screenPos.y - HEIGHT + imgHeight + border;
				System.out.println(cam.position.y);
				cam.update();
				batch.setProjectionMatrix(cam.combined);
			}
		}
		if (screenPos.y < border) {
			if (imgY + imgHeight < WORLDMINy - border) {
				lastlyCoor();
			}
			cam.position.y = cam.position.y - (border - screenPos.y);
			System.out.println(cam.position.y);
			cam.update();
			batch.setProjectionMatrix(cam.combined);
		}
    }
    public void wrapCoordinates(float targetWidth, float targetHeight) {
		if (imgX > targetWidth) { //WIDTH = TargetWidth
			imgX = -imgWidth;
		} else if (imgX < -imgWidth) {
			imgX=targetWidth;
		}
		/*if (imgX >= WORLDWIDTH) {
			imgX = -imgWidth;
		} else if (imgX < -imgWidth) {
			imgX=-WORLDWIDTH;
		}*/
		if (imgY > targetHeight) {  //HEIGHT = TargetHeight
			imgY = -imgHeight;
		} else if (imgY < -imgHeight) {
			imgY = targetHeight;
		}
	}
	public void wrapCoordinates() {
		wrapCoordinates(WIDTH, HEIGHT);
	}
	public void locky() {
		if (imgX > player*2) {
			imgX = player*2;
		} else if (imgX < 0) {
			imgX = 0;
		}
		if (imgY > player*2) {
			imgY = player*2;
		} else if (imgX < 0) {
			imgY = 0;
		}
	}
	public void lastlyCoor() {
		if (imgX > WIDTH - imgWidth) {
			imgX = WIDTH - imgWidth;
		} else if (imgX < 0) {
			imgX = 0;
		}
		if (imgY > HEIGHT - imgHeight) {
			imgY =HEIGHT - imgHeight;
		} else if (imgY < 0) {
			imgY = 0;
		}
	}
    public void lockCoordinates(float targetWidth, float targetHeight) {
		if (imgX > targetWidth - imgWidth) {
			imgX = targetWidth - imgWidth;
		} else if (imgX < 0) {
			imgX = 0;
		}
		if (imgY > targetHeight - imgHeight) {
			imgY = targetHeight - imgHeight;
		} else if (imgY < 0) {
			imgY = 0;
		}
	}
	public void lockCoordinates() {
		lockCoordinates(WORLDWIDTH, WORLDHEIGHT);
	}
	abstract class CameraEffect {
		protected OrthographicCamera cam;
		protected int duration, progress;
		protected ShapeRenderer renderer;
		protected SpriteBatch batch;
		public CameraEffect(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer) {
			this.cam = cam;
			this.duration = duration;
			this.batch = batch;
			this.renderer = renderer;
			progress = duration;
		}
		public boolean isActive() {
			return (progress<duration);
		}
		public abstract void play();
		public void updateCamera() {
			cam.update();
			if (renderer != null) {
				renderer.setProjectionMatrix(cam.combined);
			}
			if (batch != null) {
				batch.setProjectionMatrix(cam.combined);
			}
		}
		public void start() {
			progress = 0;
		}
	}
	
	class CameraShake extends CameraEffect {
		private int intensity;
		private int speed;
		public int getIntensity() {
			return intensity;
		}
		public void setIntensity(int intensity) {
			if (intensity < 0) {
				this.intensity = 0;
			} else {
				this.intensity = intensity;
			}
		}
		public int getSpeed() {
			return speed;
		}
		public void setSpeed(int speed) {
			speed = (100 - speed)/10;
			if (speed <= 0) {
				this.speed = 1;
			} else {
				if (speed > duration) {
					this.speed = duration / 2;
				} else {
					this.speed = speed;
				}
			}
		}
		@Override
	public boolean isActive() {
		return super.isActive() && speed > 0;
	}
	public CameraShake(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer, int intensity, int speed) {
		super(cam,duration,batch,renderer);
		setIntensity(intensity);
		setSpeed(speed);
	}
	@Override
	public void play() {
		if (isActive()) {
			if (progress % speed == 0) {
				intensity = -intensity;
				cam.translate(2*intensity,0);
			}
			progress++;
			if (isActive()) {
				cam.translate(-intensity,0);
			}
			updateCamera();
		}
	}
	@Override
	public void start() {
		super.start();
		cam.translate(intensity,0);
		updateCamera();
	}
}



    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			shaker.start();
		}
		shaker.play();
		handleInput();
		panCoordinates(20);
		label.setText("X = " + imgX + ", Y = " + imgY);
		// update the label position to ensure that it stays at the same place on
		// the screen as the camera moves.
		label.setPosition(20+cam.position.x-WIDTH/2,400+cam.position.y-HEIGHT/2);
		batch.begin();
		batch.draw(background1, backgroundX, 0, WIDTH*2,HEIGHT*3);
		batch.draw(background2, backgroundX + WIDTH*2, 0, WIDTH*2,HEIGHT*3);
		batch.draw(img, imgX, imgY);
		label.draw(batch,1);
		batch.end();
		
		backgroundX -= backgroundSpeed;
		if (backgroundX + WIDTH == 0) {
			backgroundX = 0;
		}
    }
    @Override
    public void dispose () {
        batch.dispose();
        img.dispose();
    }
}
