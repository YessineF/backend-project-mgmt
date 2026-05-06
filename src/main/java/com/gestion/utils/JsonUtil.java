package com.gestion.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utilitaire centralisé pour toute la sérialisation JSON.
 * Gère LocalDate, envoie les réponses HTTP et lit le body des requêtes.
 */
public class JsonUtil {

    // ── Gson configuré avec adaptateur pour LocalDate ──────────────
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {

                @Override
                public void write(JsonWriter out, LocalDate date) throws IOException {
                    if (date == null) { out.nullValue(); return; }
                    out.value(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
                }

                @Override
                public LocalDate read(JsonReader in) throws IOException {
                    if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                        in.nextNull(); return null;
                    }
                    return LocalDate.parse(in.nextString(),
                            DateTimeFormatter.ISO_LOCAL_DATE);
                }
            })
            .serializeNulls()          // inclut les champs null dans la réponse
            .setPrettyPrinting()       // JSON lisible (désactiver en prod)
            .create();

    private JsonUtil() {}

    // ── Sérialiser un objet Java → String JSON ─────────────────────
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    // ── Désérialiser un String JSON → objet Java ───────────────────
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    // ── Désérialiser avec Type générique (ex: List<Employe>) ───────
    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    /**
     * Lit le body JSON de la requête HTTP entrante.
     * Exemple : String body = JsonUtil.readBody(request);
     */
    public static String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (var reader = request.getReader()) {
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    /**
     * Envoie une réponse JSON avec un statut HTTP précis.
     * Exemple : JsonUtil.sendResponse(response, 200, employe);
     */
    public static void sendResponse(HttpServletResponse response,
                                    int status, Object data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(GSON.toJson(data));
        out.flush();
    }

    /**
     * Envoie une réponse d'erreur JSON standardisée.
     * Exemple : JsonUtil.sendError(response, 404, "Employé non trouvé");
     */
    public static void sendError(HttpServletResponse response,
                                 int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Format standard : { "error": "message" }
        JsonObject error = new JsonObject();
        error.addProperty("error", message);

        PrintWriter out = response.getWriter();
        out.print(GSON.toJson(error));
        out.flush();
    }
}