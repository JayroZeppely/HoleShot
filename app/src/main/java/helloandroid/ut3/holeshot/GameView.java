package helloandroid.ut3.holeshot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect; // <-- Importer Rect
import android.os.Handler; // <-- Importer Handler
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import java.util.ArrayList; // <-- Importer ArrayList
import java.util.List; // <-- Importer List
import java.util.Random; // <-- Importer Random

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private final GameThread thread;
    private final Handler gameHandler = new Handler(); // Handler pour le postDelayed

    private Point ballPosition;

    private Point holePosition;
    private final Random random = new Random(); // Pour générer des positions aléatoires

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
    }

    // Le Runnable qui sera appelé toutes les secondes
    private final Runnable setTimer = new Runnable() {
        @Override
        public void run() {
            gameHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();

        // Démarrer le timer pour ajouter des blocs
        gameHandler.post(setTimer);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Arrêter le timer
        gameHandler.removeCallbacks(setTimer);

        // Arrêter le thread de rendu
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
    }

    /**
     * Dessine tous les éléments du jeu.
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
}
