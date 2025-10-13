package helloandroid.ut3.holeshot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import android.os.Handler; // <-- Importer Handler
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import java.util.Random; // <-- Importer Random

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private final GameThread thread;
    private final Handler gameHandler = new Handler(); // Handler pour le postDelayed
    private Point ballPosition;
    private Point holePosition;
    private final Random random = new Random(); // Pour générer des positions aléatoires

    private Paint ballPaint = new Paint();
    private Paint holePaint = new Paint();
    private Paint backgroundPaint = new Paint();

    // Constructeur
    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        ballPaint.setColor(Color.rgb(120, 100, 110));
        holePaint.setColor(Color.BLACK);
        backgroundPaint.setColor(Color.WHITE);

        ballPosition = new Point();
        holePosition = new Point();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ballPosition.x = getWidth() / 2;
        ballPosition.y = getHeight() / 2;

        generateNewHolePosition();

        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
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

    private void generateNewHolePosition() {
        if (getWidth() > 0 && getHeight() > 0) {
            holePosition.x = random.nextInt(getWidth() - 50);
            holePosition.y = random.nextInt(getHeight() - 50);
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
            canvas.drawPaint(backgroundPaint);
            canvas.drawCircle(ballPosition.x, ballPosition.y, 30, ballPaint);
            canvas.drawCircle(holePosition.x, holePosition.y, 50, holePaint);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
}
