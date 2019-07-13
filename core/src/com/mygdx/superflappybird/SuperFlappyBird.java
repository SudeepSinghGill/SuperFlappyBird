package com.mygdx.superflappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;


public class SuperFlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
	Texture background;
	Texture[] birds;
	Texture GameOver;
	Texture Restart;
	ShapeRenderer shapeRenderer;
	BitmapFont font;
    Music music;
    Sound birdFlap;
    Sound birdPoint;
    Sound birdHit;
	Texture pipeUp;
	Texture pipeDown;
	int flapState = 0;
	int flag = 0;
	float birdY = 0;
	float velocity = 0;
	float gravity = 1.5f;
	int gameState = 0;
	float maxPipeOffset;
	float[] pipeOffset = new float[4];
	Random rand;
	float pipeVelocity = 8;
	float[] pipeX = new float[4];
	float DistanceBetweenPipes;
	Circle birdCircle;
	Rectangle[] pipeRectangleUp;
	Rectangle[] pipeRectangleDown;
	int Gap = 400;
	int Score = 0;
	int ScoringTube = 0;
    int count = 0;
    float time = 0;

	@Override
	public void create () {
	    batch = new SpriteBatch();
		background = new Texture("background.png");
        Restart = new Texture("refresh.png");
		birds = new Texture[4];
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(8);
        shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        pipeRectangleUp = new Rectangle[4];
        pipeRectangleDown = new Rectangle[4];
		birds[0] = new Texture(Gdx.files.internal("frame1.png"));
        birds[1] = new Texture(Gdx.files.internal("frame2.png"));
        birds[2] = new Texture(Gdx.files.internal("frame3.png"));
        birds[3] = new Texture(Gdx.files.internal("frame4.png"));
        GameOver = new Texture("gameover.png");
        pipeUp = new Texture("pipe_up.png");
        pipeDown = new Texture("pipe_down.png");
        maxPipeOffset = Gdx.graphics.getHeight()-Gap-150;
        DistanceBetweenPipes = Gdx.graphics.getWidth()/2.5f;
        rand = new Random();
        music = Gdx.audio.newMusic(Gdx.files.internal("musiceffect.mp3"));
        birdFlap = Gdx.audio.newSound(Gdx.files.internal("wing.mp3"));
        birdPoint = Gdx.audio.newSound(Gdx.files.internal("flappypoint.mp3"));
        birdHit = Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
        startGame();

	}

	public void startGame()
    {
        birdY = Gdx.graphics.getHeight()/2-150/2;
        for(int i = 0;i<4;i++)
        {
            pipeX[i] = Gdx.graphics.getWidth()*3/4-pipeUp.getWidth()/2 + Gdx.graphics.getWidth()/2+ i*DistanceBetweenPipes;
            pipeOffset[i] = (rand.nextFloat()-0.5f)*(maxPipeOffset);
            pipeRectangleDown[i] = new Rectangle();
            pipeRectangleUp[i] = new Rectangle();

        }

    }

    public void hitSound()
    {
        birdHit.play();
    }

	@Override
	public void render () {
        batch.begin();
        batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        if(gameState ==1 )
        {
            music.setVolume(0.1f);
            music.play();
            music.setLooping(true);
            if(pipeX[ScoringTube] < Gdx.graphics.getWidth()/2)
            {
                Score = Score+5 ;
                birdPoint.play(0.2f);
                if(ScoringTube <3)
                {
                    ScoringTube++;
                }
                else
                {
                    ScoringTube=0;
                }

            }
            if(Gdx.input.justTouched())
            {
                velocity = -20;
                birdFlap.play(0.2f);
            }

            for(int i =0;i<4;i++)
            {
                if(pipeX[i]< - pipeUp.getWidth())
                {
                    pipeOffset[i] = (rand.nextFloat()-0.5f)*(maxPipeOffset);
                    pipeX[i] = pipeX[i] + 4*DistanceBetweenPipes;
                }
                pipeX[i] = pipeX[i] - pipeVelocity;
                batch.draw(pipeUp,pipeX[i],Gdx.graphics.getHeight()/2-pipeDown.getHeight()-Gap/2-pipeOffset[i],pipeUp.getWidth(),pipeUp.getHeight());
                batch.draw(pipeDown,pipeX[i],Gdx.graphics.getHeight()/2 + Gap/2-pipeOffset[i],pipeDown.getWidth(),pipeDown.getHeight());

                pipeRectangleUp[i].set(pipeX[i],Gdx.graphics.getHeight()/2-pipeDown.getHeight()-Gap/2-pipeOffset[i],pipeUp.getWidth(),pipeUp.getHeight());
                pipeRectangleDown[i].set(pipeX[i],Gdx.graphics.getHeight()/2 + Gap/2-pipeOffset[i],pipeDown.getWidth(),pipeDown.getHeight());
            }


            if(birdY>0)
            {
                velocity = velocity + gravity;
                birdY = birdY - velocity;
            }
            else
            {
                gameState = 2;
            }

        }
        else if(gameState == 0)
        {
            if(Gdx.input.justTouched())
            {
                gameState = 1;
            }

        }
        else if(gameState == 2)
        {

            if(time<2) {
                time += Gdx.graphics.getDeltaTime();
                batch.draw(GameOver,Gdx.graphics.getWidth()/2-900/2,Gdx.graphics.getHeight()/2-500/2,900,500);
            }
            else
            {
                batch.draw(Restart,Gdx.graphics.getWidth()-250,80,200,200);
            }

            if(count==0)
            {
                hitSound();
                count = 1;
            }
            if(Gdx.input.justTouched() && time>2)
            {
                gameState=0;
                startGame();
                Score = 0;
                ScoringTube = 0;
                velocity = 0;
                count = 0;
                time = 0;
            }

        }

		batch.draw(birds[flapState],Gdx.graphics.getWidth()/2-150/2,birdY,150,150);
        font.draw(batch,String.valueOf(Score),0,Gdx.graphics.getHeight());

		batch.end();
        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.RED);
        birdCircle.set(Gdx.graphics.getWidth()/2,birdY+150/2,150/2);
        //shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

		for(int i=0;i<4;i++)
        {
           // shapeRenderer.rect(pipeRectangleDown[i].x,pipeRectangleDown[i].y,pipeRectangleDown[i].width,pipeRectangleDown[i].height);
           // shapeRenderer.rect(pipeRectangleUp[i].x,pipeRectangleUp[i].y,pipeRectangleUp[i].width,pipeRectangleUp[i].height);

            if(Intersector.overlaps(birdCircle,pipeRectangleUp[i]) || Intersector.overlaps(birdCircle,pipeRectangleDown[i]))
            {
                gameState = 2;
            }

        }
        //shapeRenderer.end();


		if(birdY >= 0 && gameState!=0)
		{
            if(flag == 0)
            {
                flapState++;
            }
            if(flag == 1)
            {
                flapState--;
            }
            if(flapState == 3)
            {
                flag = 1;
            }
            if(flapState == 0)
            {
                flag = 0;
            }

        }
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		music.dispose();
        birdFlap.dispose();
        birdPoint.dispose();
	}
}
