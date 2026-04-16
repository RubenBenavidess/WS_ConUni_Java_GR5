package ec.edu.monster;

import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import static com.codename1.ui.CN.*;
import com.codename1.system.Lifecycle;
import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.io.*;
import java.io.IOException;
import java.io.OutputStream;

public class CMConUniJavaGR5 extends Lifecycle {
    
    // Variable global para guardar el token que nos devuelva el servidor
    private String tokenGlobal = "";
    // URL de tu Web Service
    private final String urlWS = "http://192.168.18.9:8080/04.%20SERVIDOR/WSConversorUnidades";

    @Override
    public void runApp() {
        // La aplicación arranca mostrando el Login
        mostrarPantallaLogin();
    }

    // =========================================================
    // 1. PANTALLA DE LOGIN
    // =========================================================
    private void mostrarPantallaLogin() {
        Form formLogin = new Form("Iniciar Sesión", BoxLayout.y());
        
        TextField txtUsuario = new TextField("", "Usuario", 20, TextField.ANY);
        TextField txtClave = new TextField("", "Contraseña", 20, TextField.PASSWORD);
        Button btnIngresar = new Button("Ingresar");

        formLogin.addAll(
            new Label("Usuario:"), txtUsuario, 
            new Label("Contraseña:"), txtClave, 
            btnIngresar
        );

        btnIngresar.addActionListener(e -> {
            String user = txtUsuario.getText();
            String pass = txtClave.getText();

            if(user.isEmpty() || pass.isEmpty()){
                ToastBar.showErrorMessage("Llene todos los campos");
                return;
            }

            CN.scheduleBackgroundTask(() -> {
                String respuesta = enviarPeticionLogin(user, pass);
                
                CN.callSerially(() -> {
                    if (respuesta.contains("Error") || respuesta.contains("No se pudo")) {
                        Dialog.show("Error", "Credenciales incorrectas o falla de conexión", "OK", null);
                    } else {
                        // Si es exitoso, guardamos el token devuelto
                        tokenGlobal = respuesta; 
                        // Cambiamos a la pantalla del conversor
                        mostrarPantallaConversor(); 
                    }
                });
            });
        });

        formLogin.show();
    }

    // =========================================================
    // 2. PANTALLA DEL CONVERSOR
    // =========================================================
    private void mostrarPantallaConversor() {
        Form formConversor = new Form("Conversor Monster", BoxLayout.y());

        TextField txtValor = new TextField("", "Valor a convertir", 20, TextArea.DECIMAL);
        String[] unidades = {"METRO", "KILOMETRO", "CENTIMETRO", "MILIMETRO"};
        ComboBox<String> cbOrigen = new ComboBox<>(unidades);
        ComboBox<String> cbDestino = new ComboBox<>(unidades);
        
        Button btnConvertir = new Button("Convertir Ahora");
        Label lblResultado = new Label("Resultado aparecerá aquí");

        formConversor.addAll(
            new SpanLabel("Token Activo: " + tokenGlobal.substring(0, 8) + "..."), // Solo para que veas que funciona
            new Label("Valor a convertir:"), txtValor,
            new Label("De:"), cbOrigen,
            new Label("A:"), cbDestino,
            btnConvertir,
            lblResultado
        );

        // Agregamos un botón para volver atrás
        formConversor.getToolbar().addCommandToLeftBar("Salir", null, ev -> mostrarPantallaLogin());

        btnConvertir.addActionListener(e -> {
            String valor = txtValor.getText();
            String origen = cbOrigen.getSelectedItem();
            String destino = cbDestino.getSelectedItem();

            CN.scheduleBackgroundTask(() -> {
                String resultado = enviarPeticionConversor(valor, origen, destino);
                CN.callSerially(() -> {
                    lblResultado.setText("Resultado: " + resultado);
                    formConversor.revalidate(); 
                });
            });
        });

        formConversor.show();
    }

    // =========================================================
    // 3. MÉTODOS DE CONEXIÓN AL SERVIDOR
    // =========================================================
    
    // Petición para loguearse (NO ENVÍA TOKEN EN EL HEADER)
    private String enviarPeticionLogin(String usuario, String contrasenia) {
        String soapRequest = 
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">" +
            "   <soapenv:Header/>" +
            "   <soapenv:Body>" +
            "      <ws:login>" +
            "         <usuario>" + usuario + "</usuario>" +
            "         <contrasenia>" + contrasenia + "</contrasenia>" +
            "      </ws:login>" +
            "   </soapenv:Body>" +
            "</soapenv:Envelope>";
            
        return ejecutarRequestSOAP(soapRequest);
    }

    // Petición para convertir (SÍ ENVÍA EL TOKEN EN EL HEADER)
    private String enviarPeticionConversor(String valor, String origen, String destino) {
        String soapRequest = 
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">" +
            "   <soapenv:Header>" +
            "      <ws:token>" + tokenGlobal + "</ws:token>" + // INYECCIÓN DEL TOKEN
            "   </soapenv:Header>" +
            "   <soapenv:Body>" +
            "      <ws:convertirLongitud>" +
            "         <valor>" + valor + "</valor>" +
            "         <unidadInicial>" + origen + "</unidadInicial>" +
            "         <unidadFinal>" + destino + "</unidadFinal>" +
            "      </ws:convertirLongitud>" +
            "   </soapenv:Body>" +
            "</soapenv:Envelope>";

        return ejecutarRequestSOAP(soapRequest);
    }

    // Método centralizado para enviar el texto por red
    private String ejecutarRequestSOAP(String xmlSOAP) {
        ConnectionRequest req = new ConnectionRequest() {
            @Override
            protected void buildRequestBody(OutputStream os) throws IOException {
                os.write(xmlSOAP.getBytes("UTF-8"));
            }
        };

        req.setUrl(urlWS);
        req.setPost(true);
        req.setContentType("text/xml; charset=utf-8");

        NetworkManager.getInstance().addToQueueAndWait(req);

        if (req.getResponseCode() == 200) {
            String response = new String(req.getResponseData());
            return extraerDato(response, "<return>", "</return>");
        } else {
            return "Error: " + req.getResponseCode();
        }
    }

    private String extraerDato(String xml, String tagInicio, String tagFin) {
        int start = xml.indexOf(tagInicio);
        if (start != -1) {
            start += tagInicio.length();
            int end = xml.indexOf(tagFin, start);
            if (end != -1) {
                return xml.substring(start, end);
            }
        }
        return "No se pudo procesar la respuesta";
    }
}