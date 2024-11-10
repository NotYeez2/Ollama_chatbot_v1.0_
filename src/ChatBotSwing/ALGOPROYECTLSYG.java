package ChatBotSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ALGOPROYECTLSYG extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton newChatButton;
    private DefaultListModel<String> historialModel;
    private JList<String> historialList;
    private Map<String, String> chatsGuardados; // Almacena el contenido de los chats con su título
    private String currentChatTitle; // Título del chat actual

    public ALGOPROYECTLSYG() {
        setTitle("ChatBot Simple");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Área de chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField(25);
        sendButton = new JButton("Enviar");
        newChatButton = new JButton("Nuevo Chat");

        // Panel para la entrada de usuario y botones
        JPanel inputPanel = new JPanel();
        inputPanel.add(inputField);
        inputPanel.add(sendButton);
        inputPanel.add(newChatButton);

        // Modelo de lista y lista de historial
        historialModel = new DefaultListModel<>();
        historialList = new JList<>(historialModel);
        JScrollPane historialScrollPane = new JScrollPane(historialList);
        historialScrollPane.setPreferredSize(new Dimension(200, 600));

        // Mapa para guardar chats
        chatsGuardados = new HashMap<>();
        currentChatTitle = generarTituloChat(); // Inicializar con un título aleatorio

        // Acción para guardar el chat actual antes de iniciar uno nuevo
        newChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Guardar el chat actual antes de crear uno nuevo
                if (!chatArea.getText().isEmpty()) {
                    chatsGuardados.put(currentChatTitle, chatArea.getText());
                    if (!historialModel.contains(currentChatTitle)) {
                        historialModel.addElement(currentChatTitle);
                    }
                }

                // Crear un nuevo chat y actualizar el título
                currentChatTitle = generarTituloChat();
                chatArea.setText("");
                inputField.setText("");
            }
        });

        // Acción al seleccionar un chat del historial
        historialList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedChatTitle = historialList.getSelectedValue();
                if (selectedChatTitle != null && chatsGuardados.containsKey(selectedChatTitle)) {
                    // Guardar el chat actual antes de cambiar
                    if (!chatArea.getText().isEmpty()) {
                        chatsGuardados.put(currentChatTitle, chatArea.getText());
                        if (!historialModel.contains(currentChatTitle)) {
                            historialModel.addElement(currentChatTitle);
                        }
                    }

                    // Cargar el chat seleccionado
                    currentChatTitle = selectedChatTitle;
                    chatArea.setText(chatsGuardados.get(selectedChatTitle));
                }
            }
        });

        // Acción al enviar un mensaje
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = inputField.getText().trim();
                if (!userInput.isEmpty()) {
                    chatArea.append("\nUsuario: " + userInput + "\n\n");

                    String respuesta = SimpleChatBotOllama.enviarPreguntaOllama(userInput);
                    String respuestaLimpia = obtenerCampo(respuesta, "response");
                    if (respuestaLimpia != null) {
                        respuestaLimpia = respuestaLimpia.replace("\\n", "\n").trim();
                        chatArea.append("ChatBot: " + respuestaLimpia + "\n\n");
                    } else {
                        chatArea.append("ChatBot: Error al obtener respuesta.\n\n");
                    }

                    inputField.setText("");
                }
            }
        });

        // Añadir componentes a la ventana
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(historialScrollPane, BorderLayout.EAST);

        setVisible(true);
    }

    // Método para generar un título aleatorio para el chat
    private String generarTituloChat() {
        String[] titulos = {"Chat de Conversación", "Historia Curiosa", "Consulta General", "Interacción AI", "Diálogo Informal"};
        return titulos[new Random().nextInt(titulos.length)];
    }

    // Método para extraer campos del JSON
    public static String obtenerCampo(String json, String campo) {
        String clave = "\"" + campo + "\":";
        int inicio = json.indexOf(clave);

        if (inicio == -1) return null;

        inicio += clave.length();
        int fin = inicio;
        boolean dentroDeComillas = false;

        while (fin < json.length()) {
            char actual = json.charAt(fin);
            if (actual == '\"') dentroDeComillas = !dentroDeComillas;
            if (!dentroDeComillas && (actual == ',' || actual == '}')) break;
            fin++;
        }

        String valor = json.substring(inicio, fin).trim();
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            valor = valor.substring(1, valor.length() - 1);
        }
        return valor;
    }

    public static void main(String[] args) {
        new ALGOPROYECTLSYG();
    }
}

