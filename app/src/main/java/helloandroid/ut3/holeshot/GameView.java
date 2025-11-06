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
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
    private final GameThread thread;
    private GameEventListener gameEventListener;
    private final Handler gameHandler = new Handler();
    private Point ballPosition;
    private Point holePosition;
    private final Random random = new Random(); // Pour générer des positions aléatoires

    private int holeRadius = 30;
    private Paint ballPaint = new Paint();
    private Paint holePaint = new Paint();
    private Paint backgroundPaint = new Paint();

    private Paint obstaclePaint = new Paint();

    private SensorManager sm;
    private Sensor accel;
    private final float[] gravity = new float[3];
    private final float[] linear_acceleration = new float[3];
    private Sensor light;
    private float currentLight;

    private final float SEUIL_LIGHT = 40.0f;
    private boolean isDarkMode = false;

    private List<RectF> obstacles = new ArrayList<>();

    private int score;
    private int lives = 3;
    private boolean invincibility = true;
    private int coolDown = 1500; //Frames d'invincibilité pour ne pas perdre toutes les vies en restant collé a un obstacle

    private int endlessPoint = 100;

    private Bitmap holeSprite;
    private Bitmap scaledHoleSprite;   // Pour l'affichage

    private int multiplicateur = 0;

    private String mode;

    public interface GameEventListener {
        void onGameOver();
        void onScoreChanged(int score);
        void resetTimer();
        void onLivesChanged(int lives);
        void onLightChanged(boolean isLight);
    }

    // Constructeur
    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        ballPaint.setColor(Color.rgb(120, 100, 140));
        holePaint.setColor(Color.BLACK);
        backgroundPaint.setColor(Color.WHITE);
        obstaclePaint.setColor(Color.RED);

        holeSprite = BitmapFactory.decodeResource(getResources(), R.drawable.hole);
        // On redimensionne le sprite pour qu'il corresponde à notre rayon
        // Redimensionnez toujours à partir de l'original !
        scaledHoleSprite = Bitmap.createScaledBitmap(holeSprite, holeRadius * 2, holeRadius * 2, false);

        ballPosition = new Point();
        holePosition = new Point();

        sm = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

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
        if (light != null) {
            sm.registerListener(this, light, SensorManager.SENSOR_DELAY_GAME);
        }

        if(gameEventListener != null){
            gameEventListener.onLivesChanged(lives);
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

    void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    private void generateNewHolePosition() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        boolean positionIsInvalid;
        int maxAttempts = 50; // Pour éviter une boucle infinie si l'écran est plein
        int attempts = 0;

        do {
            positionIsInvalid = false;
            int testX = random.nextInt(getWidth() - (holeRadius * 2)) + holeRadius;
            int testY = random.nextInt(getHeight() - (holeRadius * 2)) + holeRadius;

            RectF holeBounds = new RectF(testX - holeRadius, testY - holeRadius, testX + holeRadius, testY + holeRadius);

            // Vérifier la collision avec chaque obstacle existant
            for (RectF obstacle : obstacles) {
                if (RectF.intersects(holeBounds, obstacle)) {
                    positionIsInvalid = true; // Le trou est sur un obstacle
                    break;
                }
            }
            // Si la position est valide, on l'accepte et on sort de la boucle
            if (!positionIsInvalid) {
                holePosition.x = testX;
                holePosition.y = testY;
            }
            attempts++;

        } while (positionIsInvalid && attempts < maxAttempts);

        // Si après 50 tentatives on n'a pas trouvé de place, on le place quand même;
        if (attempts >= maxAttempts) {
            holePosition.x = random.nextInt(getWidth() - (holeRadius * 2)) + holeRadius;
            holePosition.y = random.nextInt(getHeight() - (holeRadius * 2)) + holeRadius;
        }

        // Redimensionner le sprite du trou
        scaledHoleSprite = Bitmap.createScaledBitmap(holeSprite, holeRadius * 2, holeRadius * 2, false);
    }


    private void generateObstacles() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        boolean positionIsInvalid;
        int maxAttempts = 50; // Sécurité pour éviter les boucles infinies
        int attempts = 0;
        RectF newObstacle = null;

        do {
            positionIsInvalid = false;
            // Générer un nouvel obstacle potentiel
            float left = random.nextInt(getWidth() - 150);
            float top = random.nextInt(getHeight() - 150);
            float right = left + 50 + random.nextInt(100);
            float bottom = top + 50 + random.nextInt(100);
            newObstacle = new RectF(left, top, right, bottom);

            for (RectF existingObstacle : obstacles) {
                if (RectF.intersects(newObstacle, existingObstacle)) {
                    positionIsInvalid = true;
                    break;
                }
            }

            // Si toujours valide, on passe à la vérification suivante
            if (!positionIsInvalid) {
                // 2. Vérifier qu'il ne chevauche pas le trou actuel
                RectF holeBounds = new RectF(holePosition.x - holeRadius, holePosition.y - holeRadius, holePosition.x + holeRadius, holePosition.y + holeRadius);
                if (RectF.intersects(newObstacle, holeBounds)) {
                    positionIsInvalid = true;
                }
            }

            attempts++;

        } while (positionIsInvalid && attempts < maxAttempts);

        // Si une position valide a été trouvée, on ajoute l'obstacle
        if (newObstacle != null && !positionIsInvalid) {
            obstacles.add(newObstacle);
        }
    }

    public void update() {
        int lastBallX = ballPosition.x;
        int lastBallY = ballPosition.y;

        int speedFactor = 2;
        ballPosition.y += (int) (gravity[0] * speedFactor);
        ballPosition.x += (int) (gravity[1] * speedFactor);

        //Gestion collisions
        RectF ballRect = new RectF(
                ballPosition.x - 30,
                ballPosition.y - 30,
                ballPosition.x + 30,
                ballPosition.y + 30
        );

        for (RectF obstacle : obstacles) {
            if (RectF.intersects(obstacle, ballRect)) {
                ballPosition.x = lastBallX;
                ballPosition.y = lastBallY;
                onObstacleHit();
                break;
            }
        }

        // Bloquer la balle pour qu'elle ne sorte pas des bords de l'écran
        int ballRadius = 30;
        if (ballPosition.x < ballRadius) ballPosition.x = ballRadius;
        if (ballPosition.y < ballRadius) ballPosition.y = ballRadius;
        if (ballPosition.x > getWidth() - ballRadius) ballPosition.x = getWidth() - ballRadius;
        if (ballPosition.y > getHeight() - ballRadius) ballPosition.y = getHeight() - ballRadius;

        // Quand la balle touche le trou
        if (Math.abs(ballPosition.x - holePosition.x) < holeRadius && Math.abs(ballPosition.y - holePosition.y) < holeRadius) {
            int baseRadius = 40;
            int variation = 30;
            holeRadius = baseRadius + random.nextInt(variation);
            if (gameEventListener != null) {
                multiplicateur += 1;
                switch (mode) {
                    case "30s":
                        score = 500 * multiplicateur;
                        break;
                    case "45s":
                        score = 250 * multiplicateur;
                        break;
                    case "60s":
                        score = 100 * multiplicateur;
                        break;
                    case "endless":
                        score += endlessPoint;
                        if (multiplicateur % 10 == 0) {
                            endlessPoint += 100;
                        }
                        gameEventListener.resetTimer();
                        break;
                }
                gameEventListener.onScoreChanged(score);
            }
            if (multiplicateur % 5 == 0) {
                generateObstacles();
            }
            generateNewHolePosition();
        }
    }

    /**
     * Dessine tous les éléments du jeu.
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            // Définir les couleurs en fonction du mode
            Paint currentBackgroundPaint =  currentLight < SEUIL_LIGHT ? holePaint : backgroundPaint;
            Paint currentObstaclePaint =  currentLight < SEUIL_LIGHT ? new Paint() {{ setColor(Color.BLUE); }} : obstaclePaint;
            Paint currentBallPaint = ballPaint;

            canvas.drawPaint(currentBackgroundPaint);

            // Gestion DarkMode
            if ( currentLight < SEUIL_LIGHT) {
                Paint lightHolePaint = new Paint();
                lightHolePaint.setColor(Color.WHITE);
                canvas.drawCircle(holePosition.x, holePosition.y, holeRadius, lightHolePaint);
            } else {
                canvas.drawBitmap(scaledHoleSprite, holePosition.x - holeRadius, holePosition.y - holeRadius, null);
            }

            canvas.drawCircle(ballPosition.x, ballPosition.y, 30, currentBallPaint);

            for (RectF obstacle : obstacles) {
                canvas.drawRect(obstacle, currentObstaclePaint);
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onObstacleHit(){
        if(!invincibility){
            return;
        }

        invincibility = false;

        gameHandler.postDelayed(() -> invincibility = true, coolDown); // reactiver les frames d'invincibilité

        if(lives > 0) {
            lives -= 1;
            if (gameEventListener != null) {
                gameEventListener.onLivesChanged(lives);
            }
        }if(lives == 0 && gameEventListener != null){
            //gameEventListener.onLivesChanged(lives);
            gameEventListener.onGameOver();
        }
    }

    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            currentLight = event.values[0];
            boolean isDark = currentLight < SEUIL_LIGHT;
            if (isDarkMode != isDark) {
                isDarkMode = isDark;
                gameEventListener.onLightChanged(isDark);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    public void setMode(String mode) {
        this.mode = mode;
    }
}
