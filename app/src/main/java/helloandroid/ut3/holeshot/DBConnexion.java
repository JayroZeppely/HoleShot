package helloandroid.ut3.holeshot;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DBConnexion {
    private static final String URL = "https://rpqjhttwofqbvfigiyok.supabase.co";
    private static final String KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJwcWpodHR3b2ZxYnZmaWdpeW9rIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI1ODcwNTksImV4cCI6MjA3ODE2MzA1OX0.SetWC3l2CEHhuBUq2-cwJ-PQi7k5fTYvLNlBAjWA4HY";

    //Insertion des scores
    public static void insertScore(String pseudo, int score) throws Exception {
        HttpURLConnection connexion = (HttpURLConnection) new URL(URL + "/rest/v1/Score").openConnection();
        connexion.setRequestMethod("POST");
        connexion.setRequestProperty("apikey", KEY);
        connexion.setRequestProperty("Authorization", "Bearer " + KEY);
        connexion.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject();
        json.put("pseudo", pseudo);
        json.put("score", score);

        try(OutputStream os = connexion.getOutputStream()) {
            os.write(json.toString().getBytes());
        }

        if(connexion.getResponseCode() != 201) {
            throw new Exception("Erreur lors de l'insertion du score");
        }

        connexion.disconnect();
    }

    //Récupération du top 10
}
