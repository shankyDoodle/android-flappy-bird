package com.shankydoodle.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Shape;

import java.util.Random;

public class StartFlappyBird extends ApplicationAdapter {
    SpriteBatch batch;

    Texture background;

    Texture[] birds;
    int flapState = 0;
    float birdY = 0;
    float velocity = 0;

    int gameState = 0;
    float gravity = 2;

    Texture topTube;
    Texture bottomTube;
    float gap = 800;
    float maxTubeOffset;
    Random randomNumberGenerator;

    float tubeVelocity = 5;
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float distanceBetweenTubes;

    Circle birdCircle;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;


    Texture gameover;
    int score = 0;
    int scoringTubes = 0;
    BitmapFont font;

    Sound sound;

    @Override
    public void create() {
        batch = new SpriteBatch();

        background = new Texture("bg.jpg");
        birdCircle = new Circle();

        gameover = new Texture("gameover.png");

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(5, 5);

        birds = new Texture[2];
        birds[0] = new Texture("jet.png");
        birds[1] = new Texture("jet.png");

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 200;
        randomNumberGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() / 2 + 150;

        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

        sound = Gdx.audio.newSound(Gdx.files.internal("tap.mp3"));

        startGame();

    }

    public void startGame() {
        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
        for (int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (randomNumberGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 800);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + (i * distanceBetweenTubes);

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();


        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {


            //score calculation
            if (tubeX[scoringTubes] < Gdx.graphics.getWidth() / 2) {
                score++;

                Gdx.app.log("Score", String.valueOf(score));
                if (scoringTubes < numberOfTubes - 1) {
                    scoringTubes++;
                    if(score%5 == 0){
                        tubeVelocity += 2.5;
                    }
                } else {
                    scoringTubes = 0;
                }
            }

            // Decreasing the velocity in negative to bring it up
            if (Gdx.input.justTouched()) {
                //Gdx.app.log("Touched!", "Touched");
                velocity = -30;
                long id = sound.play(1);
//                sound.stop(id);
            }

            // Increasing the velocity each time the render method is invoked
            for (int i = 0; i < numberOfTubes; i++) {
                if (tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomNumberGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 800);
                } else {
                    tubeX[i] = tubeX[i] - tubeVelocity;
                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            }

            if (birdY > 0) {
                velocity = velocity + gravity;
                birdY -= velocity;
            }

        } else if (gameState == 0) {

            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);
            if (Gdx.input.justTouched()) {
                gameState = 1;
                startGame();
                score = 0;
                scoringTubes = 0;
                velocity = 0;
                tubeVelocity = 5;
            }
        }

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }


        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);

        font.draw(batch, String.valueOf(score), 100, Gdx.graphics.getHeight() - 200);

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

        for (int i = 0; i < numberOfTubes; i++) {
            if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
                gameState = 2;
                break;
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
