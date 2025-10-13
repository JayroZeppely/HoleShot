package helloandroid.ut3.holeshot;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private final SurfaceHolder surfaceHolder;
    private final GameView gameView;
    private boolean running;

    // ----- AJOUTS POUR LE CONTRÔLE DES FPS -----
    // Cible de FPS (images par seconde) pour le jeu.
    private static final int TARGET_FPS = 60;
    // Durée cible pour chaque image en millisecondes (1000ms / 60fps).
    private static final long TARGET_TIME = 1000 / TARGET_FPS;
    // ---------------------------------------------

    public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
        this.running = false;
    }

    public void setRunning(boolean isRunning) {
        this.running = isRunning;
    }

    @Override
    public void run() {
        // ----- VARIABLES POUR LA GESTION DU TEMPS -----
        long startTime;
        long timeMillis;
        long waitTime;
        // ---------------------------------------------

        while (running) {
            // Enregistre l'heure de début du cycle
            startTime = System.nanoTime();
            Canvas canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    if (canvas != null) {
                        this.gameView.update();      // Mettre à jour la logique du jeu
                        this.gameView.draw(canvas);  // Dessiner l'état actuel
                    }
                }
            } catch (Exception e) {
                Log.e("GameThread", "Erreur dans la boucle de jeu", e);
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        Log.e("GameThread", "Erreur lors du déverrouillage du canvas", e);
                    }
                }
            }

            // ----- CALCUL DU TEMPS DE PAUSE -----
            // Calcule combien de temps le cycle a pris, en millisecondes.
            timeMillis = (System.nanoTime() - startTime) / 1000000;
            // Calcule le temps qu'il reste à attendre pour atteindre notre objectif de 60 FPS.
            waitTime = TARGET_TIME - timeMillis;

            try {
                // Si la boucle a été plus rapide que notre cible, on fait une pause.
                if (waitTime > 0) {
                    Thread.sleep(waitTime);
                }
            } catch (InterruptedException e) {
                Log.e("GameThread", "Thread de jeu interrompu", e);
                // Bonne pratique : restaurer le statut d'interruption du thread
                Thread.currentThread().interrupt();
            }
            // ------------------------------------
        }
    }
}
