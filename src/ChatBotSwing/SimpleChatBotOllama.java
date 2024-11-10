package ChatBotSwing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleChatBotOllama {
    // Nombre del modelo que deseas usar
    private static final String MODEL_NAME = "llama3.2"; // Cambia esto por el nombre correcto de tu modelo

    // Método para enviar preguntas a Ollama
    public static String enviarPreguntaOllama(String pregunta) {
        try {
            URL url = new URL("http://localhost:11434/api/generate"); // Endpoint de la API
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Cuerpo de la solicitud en el formato esperado, incluyendo parámetros adicionales
            String jsonInputString = String.format(
                "{\"model\":\"%s\", \"prompt\":\"%s\", \"temperature\":0.7, \"max_tokens\":150, \"stream\":false}",
                MODEL_NAME, pregunta
            );

            // Enviar la solicitud
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Leer la respuesta
            int responseCode = con.getResponseCode();
            StringBuilder response = new StringBuilder();

            // Manejo de respuesta
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
            } else {
                // Leer el mensaje de error
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                return "Error: " + responseCode + " - " + response.toString(); // Mostrar el error detallado
            }

            // Retornar la respuesta obtenida
            return response.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
