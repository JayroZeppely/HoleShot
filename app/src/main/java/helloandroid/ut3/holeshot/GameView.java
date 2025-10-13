package helloandroid.ut3.holeshot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private final List<Rect> blocks = new ArrayList<>(); // Liste pour stocker les blocs
    private final Paint blockPaint = new Paint();
    private final Random random = new Random(); // Pour générer des positions aléatoires

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        // Initialiser le "pinceau" pour les blocs
        blockPaint.setColor(Color.rgb(250, 0, 0));
    }

    // Méthode pour ajouter un nouveau bloc à l'écran
    private void addBlock() {
        if (getWidth() == 0) return; // Ne rien faire si la vue n'a pas encore de taille

        int blockWidth = 100;
        int blockHeight = 50;

        // Position x aléatoire
        int x = random.nextInt(getWidth() - blockWidth);
        // Position y en haut de l'écran
        int y = 0;

        // Créer le rectangle et l'ajouter à la liste
        Rect newBlock = new Rect(x, y, x + blockWidth, y + blockHeight);

        // Important: la modification de la liste doit être synchronisée
        // pour éviter les conflits avec le GameThread qui la lit en même temps.
        synchronized (blocks) {
            blocks.add(newBlock);
        }
    }

    // Le Runnable qui sera appelé toutes les 100ms
    private final Runnable addBlockRunnable = new Runnable() {
        @Override
        public void run() {
            addBlock();
            // Planifier la prochaine exécution dans 100 millisecondes (1/10s)
            gameHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();

        // Démarrer le timer pour ajouter des blocs
        gameHandler.post(addBlockRunnable);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Arrêter le timer
        gameHandler.removeCallbacks(addBlockRunnable);

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

    /**
     * Met à jour la logique du jeu (par exemple, faire tomber les blocs).
     */
    public void update() {
        // 'synchronized' pour éviter de modifier la liste pendant que le
        // Runnable `addBlockRunnable` y ajoute un élément.
        synchronized (blocks) {
            for (Rect block : blocks) {
                // Fait descendre chaque bloc de 5 pixels à chaque mise à jour
                block.offset(0, 5);
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
            canvas.drawColor(Color.WHITE);

            // 'synchronized' pour éviter les erreurs si la liste est modifiée
            // pendant qu'on la parcourt pour le dessin.
            synchronized (blocks) {
                for (Rect block : blocks) {
                    canvas.drawRect(block, blockPaint);
                }
            }
        }
    }

    // Les autres méthodes (constructeurs, surfaceChanged) restent les mêmes...

    public GameView(Context context) { this(context, null); }
    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { this(context, attrs); }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
}
