package com.tanay.thunderbird.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter
{
	SpriteBatch batch;
	Texture background;										//texture = image
	Texture topTube;
	Texture bottomTube;
	Texture gameover;
	int score = 0;
	int scoringTube = 0;
	BitmapFont font, font1;
	//ShapeRenderer shapeRenderer;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	Circle birdCircle;
	float velocity = 0;
	float gravity = 1;
	int gameState = 0;
	float gap = 400;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float tubeX[] = new float[numberOfTubes];
	float tubeOffset[] = new float[numberOfTubes];
	float distanceBetweenTubes;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	String s = "RESTART";

	@Override
	public void create()                                    //when app is launched
	{
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameover = new Texture("gameover.png");
		birdCircle = new Circle();
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		font.getData().setScale(5);   				//for text size

		font1 = new BitmapFont();
		font1.setColor(Color.BLACK);
		font1.getData().setScale(5);

		//shapeRenderer = new ShapeRenderer();      //enables us to draw shapes

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 350;
		randomGenerator = new Random();
		distanceBetweenTubes = (Gdx.graphics.getWidth()*3)/4;

		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		startGame();
	}

	public void startGame()
	{
		birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;                   //birds[0] because both birds have the same height

		for(int i=0; i<numberOfTubes; i++)
		{
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f)*(Gdx.graphics.getHeight() - gap - 700);      //randomGenerator.nextFloat() produces a random number between 0 and 1, we're using it as
			tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() + i*distanceBetweenTubes;
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render()                                   //goes on continuously with the app
	{
		//tells render() that we're beginning to display sprites now->
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1)
		{
			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2)
			{
				score++;
				Gdx.app.log("Score", String.valueOf(score));

				if(scoringTube < numberOfTubes-1)
				{
					scoringTube++;
				}

				else
				{
					scoringTube = 0;
				}
			}

			if(Gdx.input.justTouched())						  //to start the flapping of the bird only when the screen is tapped
			{
				velocity = -20;
			}

			for(int i=0; i<numberOfTubes; i++)
			{
				if(tubeX[i] < -topTube.getWidth())
				{
					tubeX[i] += (numberOfTubes)*(distanceBetweenTubes);
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f)*(Gdx.graphics.getHeight() - gap - 700);
				}

				else
				{
					tubeX[i]-=tubeVelocity;
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			if(birdY>0)
			{
				velocity += gravity;
				birdY -= velocity;
			}

			else
			{
				gameState = 2;
			}
		}

		else if(gameState == 0)
		{
			font1.draw(batch, "TAP TO PLAY", Gdx.graphics.getWidth()/2 - 230, Gdx.graphics.getHeight()/2 + 200);

			if(Gdx.input.justTouched())
			{
				gameState = 1;
				font1.dispose();
			}
		}

		else if(gameState == 2)
		{
			batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, Gdx.graphics.getHeight()/2 - gameover.getHeight()/2 + 200);
			birdY = 0;
			//for restarting
			if(Gdx.input.justTouched())
			{
				gameState = 1;
				startGame();
				scoringTube = 0;
				score = 0;
				velocity = 0;
			}
		}

		if(flapState == 0)
		{
			flapState = 1;
		}

		else
		{
			flapState=0;
		}

		batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2, birdY);
		font.draw(batch, "Score: " + String.valueOf(score), 50, 150);

		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth()/2);         //for radius, we can use height as well, but width of the bird image is slightly larger than its height, so!

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for(int i=0; i<numberOfTubes; i++)
		{
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			//to check collision
			if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i]))
			{
				gameState = 2;
			}
		}

		//shapeRenderer.end();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
