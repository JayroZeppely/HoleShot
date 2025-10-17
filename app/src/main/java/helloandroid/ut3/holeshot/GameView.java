package helloandroid.ut3.holeshot;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler; // <-- Importer Handler
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random; // <-- Importer Random

public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private final GameThread thread;
    private final Handler gameHandler = new Handler(); // Handler pour le postDelayed
    private Point ballPosition;
    private Point holePosition;
    private final Random random = new Random(); // Pour générer des positions aléatoires

    private Paint ballPaint = new Paint();
    private Paint holePaint = new Paint();
    private Paint backgroundPaint = new Paint();

    private Paint obstaclePaint = new Paint();

    private SensorManager sm;
    private Sensor accel;
    private final float[] gravity = new float[3];
    private final float[] linear_acceleration = new float[3];

    private List<RectF> obstacles = new ArrayList<>();

    private int score;

    // Constructeur
    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        ballPaint.setColor(Color.rgb(120, 100, 140));
        holePaint.setColor(Color.BLACK);
        backgroundPaint.setColor(Color.WHITE);
        obstaclePaint.setColor(Color.RED);

        ballPosition = new Point();
        holePosition = new Point();

        sm = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ballPosition.x = getWidth() / 2;
        ballPosition.y = getHeight() / 2;

        generateNewHolePosition();

        thread.setRunning(true);
        thread.start();
        if (accel != null) {
            sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (sm != null) {
            sm.unregisterListener(this);
        }
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

    private void generateObstacles() {
        // Générer des obstacles aléatoires
        float left = random.nextInt(getWidth() - random.nextInt(150));
        float top = random.nextInt(getHeight() - random.nextInt(150));
        float right = left + random.nextInt(150);
        float bottom = top + random.nextInt(150);
        obstacles.add(new RectF(left, top, right, bottom));
    }

    // Boucle de logique du jeu
    public void update() {
        int lastBallX = ballPosition.x;
        int lastBallY = ballPosition.y;

        int speedFactor = 2;
        ballPosition.y += (int) (gravity[0] * speedFactor);
        ballPosition.x += (int) (gravity[1] * speedFactor);

        //Gestion collisions
        for (RectF obstacle : obstacles) {
            if (RectF.intersects(obstacle, new RectF(ballPosition.x - 30, ballPosition.y - 30, ballPosition.x + 30, ballPosition.y))) {
                ballPosition.x = lastBallX;
                ballPosition.y = lastBallY;
                break;
            }
        }

        // Bloquer la balle pour qu'elle ne sorte pas des bords de l'écran
        int ballRadius = 30;
        if (ballPosition.x < ballRadius) ballPosition.x = ballRadius;
        if (ballPosition.y < ballRadius) ballPosition.y = ballRadius;
        if (ballPosition.x > getWidth() - ballRadius) ballPosition.x = getWidth() - ballRadius;
        if (ballPosition.y > getHeight() - ballRadius) ballPosition.y = getHeight() - ballRadius;

        // --- Quand la balle entre dans le trou ---
        if (Math.abs(ballPosition.x - holePosition.x) < 30 && Math.abs(ballPosition.y - holePosition.y) < 30) {
            generateNewHolePosition();
            score += 1;
            if (score % 5 == 0) {
                generateObstacles();
            }
        }
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
            for (RectF obstacle : obstacles) {
                canvas.drawRect(obstacle, obstaclePaint);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event){
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = 0.8f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
}
