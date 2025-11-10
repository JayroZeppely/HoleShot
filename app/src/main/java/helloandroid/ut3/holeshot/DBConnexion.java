package helloandroid.ut3.holeshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    public static List<String> getTop10() throws Exception {
        HttpURLConnection connexion = (HttpURLConnection) new URL(URL + "/rest/v1/Score?select=*&order=score.desc&limit=10").openConnection();
        connexion.setRequestMethod("GET");
        connexion.setRequestProperty("apikey", KEY);
        connexion.setRequestProperty("Authorization", "Bearer " + KEY);

        if (connexion.getResponseCode() != 200) {
            throw new Exception("Erreur lors de la récupération des scores");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        List<String> scores = new ArrayList<>();
        JSONArray array = new JSONArray(builder.toString()); //On recupere les scores en String

        //On va créer la liste avec les pseudos et les scores
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            String pseudo = object.getString("pseudo");
            int score = object.getInt("score");

            scores.add(pseudo + "   " + score);
        }

        reader.close();
        connexion.disconnect();
        return scores;
    }

}
