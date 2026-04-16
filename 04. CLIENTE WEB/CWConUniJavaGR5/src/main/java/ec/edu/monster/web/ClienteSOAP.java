package ec.edu.monster.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClienteSOAP {
    
    // URL de tu Web Service
    private static final String URL_WS = "http://localhost:8080/04.%20SERVIDOR/WSConversorUnidades";

    public static String login(String usuario, String contrasenia) throws Exception {
        String xml = 
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">\n" +
            "   <soapenv:Header/>\n" +
            "   <soapenv:Body>\n" +
            "      <ws:login>\n" +
            "         <usuario>" + usuario + "</usuario>\n" +
            "         <contrasenia>" + contrasenia + "</contrasenia>\n" +
            "      </ws:login>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";
            
        return enviarPeticion(xml);
    }

    public static String convertir(String token, String valor, String origen, String destino) throws Exception {
        String xml = 
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.monster.edu.ec/\">\n" +
            "   <soapenv:Header>\n" +
            "      <ws:token>" + token + "</ws:token>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <ws:convertirLongitud>\n" +
            "         <valor>" + valor + "</valor>\n" +
            "         <unidadInicial>" + origen + "</unidadInicial>\n" +
            "         <unidadFinal>" + destino + "</unidadFinal>\n" +
            "      </ws:convertirLongitud>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";
            
        return enviarPeticion(xml);
    }

    private static String enviarPeticion(String xmlSOAP) throws Exception {
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create(URL_WS))
                .header("Content-Type", "text/xml; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(xmlSOAP))
                .build();

        HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

        if (respuesta.statusCode() == 200) {
            return extraerDato(respuesta.body(), "<return>", "</return>");
        } else {
            throw new Exception("Error del servidor: Código " + respuesta.statusCode());
        }
    }

    private static String extraerDato(String xml, String tagInicio, String tagFin) {
        int start = xml.indexOf(tagInicio);
        if (start != -1) {
            start += tagInicio.length();
            int end = xml.indexOf(tagFin, start);
            if (end != -1) return xml.substring(start, end);
        }
        return "Respuesta no válida del servidor";
    }
}