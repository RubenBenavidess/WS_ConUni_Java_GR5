package ec.edu.monster.ws.manejadores;

import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import jakarta.xml.ws.handler.MessageContext;

import javax.xml.namespace.QName;
import org.w3c.dom.NodeList;
import java.util.Set;

public class ManejadorAutenticacion implements SOAPHandler<SOAPMessageContext> {

    private static final String USUARIO_VALIDO = "Monster";
    private static final String CONTRASENIA_VALIDA = "Monster9";

    @Override
    public boolean handleMessage(SOAPMessageContext contexto) {

        Boolean esSalida = (Boolean) contexto.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        // Solo validar peticiones entrantes
        if (!esSalida) {
            try {
                var mensajeSOAP = contexto.getMessage();
                var encabezado = mensajeSOAP.getSOAPHeader();

                if (encabezado == null) {
                    throw new RuntimeException("No se envió encabezado SOAP");
                }

                NodeList nodosUsuario = encabezado.getElementsByTagNameNS("*", "usuario");
                NodeList nodosContrasenia = encabezado.getElementsByTagNameNS("*", "contrasenia");

                if (nodosUsuario.getLength() == 0 || nodosContrasenia.getLength() == 0) {
                    throw new RuntimeException("Faltan credenciales");
                }

                String usuario = nodosUsuario.item(0).getTextContent();
                String contrasenia = nodosContrasenia.item(0).getTextContent();

                if (!USUARIO_VALIDO.equals(usuario) || !CONTRASENIA_VALIDA.equals(contrasenia)) {
                    throw new RuntimeException("Credenciales inválidas");
                }

            } catch (Exception e) {
                throw new RuntimeException("Error de autenticación: " + e.getMessage());
            }
        }

        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleFault(SOAPMessageContext contexto) {
        return true;
    }

    @Override
    public void close(MessageContext contexto) {}
}