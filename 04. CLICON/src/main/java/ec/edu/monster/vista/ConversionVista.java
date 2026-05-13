package ec.edu.monster.vista;

import ec.edu.monster.controlador.ConversionControlador;
import ec.edu.monster.modelo.ConversionModelo;
import ec.edu.monster.modelo.MensajesModelo;
import ec.edu.monster.modelo.TipoConversion;
import ec.edu.monster.util.EntradaUtil;
import jakarta.xml.ws.soap.SOAPFaultException;

public class ConversionVista {

    private static final String[] UNIDADES_LONGITUD = {
        "MILIMETRO", "CENTIMETRO", "METRO", "KILOMETRO", "YARDA"
    };

    private static final String[] UNIDADES_MASA = {
        "MILIGRAMO", "GRAMO", "KILOGRAMO", "TONELADA", "ONZA"
    };

    private static final String[] UNIDADES_TEMPERATURA = {
        "CELSIUS", "FAHRENHEIT", "KELVIN", "RANKINE"
    };

    private final MensajesModelo mensajes = new MensajesModelo();
    private final ConversionControlador conversionControlador;

    public ConversionVista(ConversionControlador conversionControlador) {
        this.conversionControlador = conversionControlador;
    }

    public void ejecutarMenu() {
        int opcion;

        do {
            mensajes.mostrarTitulo("MENU PRINCIPAL");
            mensajes.mostrarMensaje("1. Convertir Longitud");
            mensajes.mostrarMensaje("2. Convertir Masa");
            mensajes.mostrarMensaje("3. Convertir Temperatura");
            mensajes.mostrarMensaje("4. Cambiar Contraseña");
            mensajes.mostrarMensaje("0. Salir");

            opcion = EntradaUtil.leerEntero("Seleccione una opcion: ");

            switch (opcion) {
                case 1 -> procesarConversion(TipoConversion.LONGITUD, UNIDADES_LONGITUD);
                case 2 -> procesarConversion(TipoConversion.MASA, UNIDADES_MASA);
                case 3 -> procesarConversion(TipoConversion.TEMPERATURA, UNIDADES_TEMPERATURA);
                case 4 -> procesarCambioContrasenia(); // 🚨 LLAMADA AL NUEVO MÉTODO
                case 0 -> mensajes.mostrarMensaje("Saliendo del menu de conversion.");
                default -> mensajes.mostrarError("Opcion no valida. Intente nuevamente.");
            }
        } while (opcion != 0);
    }

    private void procesarCambioContrasenia() {
        mensajes.mostrarTitulo("CAMBIO DE CONTRASEÑA");

        // Nota: Asumo que en tu EntradaUtil tienes un método leerTexto o leerCadena. 
        // Si se llama distinto, solo ajusta el nombre.
        String actual = EntradaUtil.leerTextoNoVacio("Ingrese su contraseña actual: ");
        String nueva = EntradaUtil.leerTextoNoVacio("Ingrese su nueva contraseña: ");
        String confirmacion = EntradaUtil.leerTextoNoVacio("Confirme su nueva contraseña: ");

        // Doble validación local
        if (nueva.isBlank()) {
            mensajes.mostrarError("La nueva contraseña no puede estar vacía.");
            return;
        }

        if (!nueva.equals(confirmacion)) {
            mensajes.mostrarError("Las contraseñas no coinciden. Operación cancelada.");
            return;
        }

        if (actual.equals(nueva)) {
            mensajes.mostrarMensaje("Advertencia: La nueva contraseña debe ser diferente a la actual.");
            return;
        }

        // Procesamiento con el controlador y captura de errores SOAP
        try {
            mensajes.mostrarMensaje("Procesando solicitud en el servidor...");
            
           String respuestaServidor = conversionControlador.cambiarContrasenia(actual, nueva);
            
            mensajes.mostrarMensaje("¡Éxito! " + respuestaServidor);
        } catch (SOAPFaultException ex) {
            // Atrapa si el servidor dice "Contraseña actual incorrecta"
            mensajes.mostrarError("Error del Servidor: " + ex.getFault().getFaultString());
        } catch (Exception ex) {
            mensajes.mostrarError("Error inesperado: " + ex.getMessage());
        }
    }

    private void procesarConversion(TipoConversion tipo, String[] unidadesPermitidas) {
        // ... (Tu código original se mantiene intacto)
        mensajes.mostrarTitulo("CONVERSION DE " + tipo.getNombreVisible().toUpperCase());

        double valor = EntradaUtil.leerDecimal("Ingrese el valor a convertir: ");
        String unidadInicial = solicitarUnidad("inicial", unidadesPermitidas);
        String unidadFinal = solicitarUnidad("final", unidadesPermitidas);

        ConversionModelo modelo = new ConversionModelo(tipo, valor, unidadInicial, unidadFinal);

        try {
            double resultado = conversionControlador.convertir(modelo);
            String mensaje = String.format("Resultado: %.6f %s = %.6f %s", valor, unidadInicial, resultado, unidadFinal);
            mensajes.mostrarMensaje(mensaje);
        } catch (SOAPFaultException ex) {
            mensajes.mostrarError(ex.getFault().getFaultString());
        } catch (Exception ex) {
            mensajes.mostrarError(ex.getMessage());
        }
    }

    private String solicitarUnidad(String tipoUnidad, String[] unidadesPermitidas) {
        // ... (Tu código original se mantiene intacto)
        mensajes.mostrarMensaje("Seleccione la unidad " + tipoUnidad + ":");
        for (int indice = 0; indice < unidadesPermitidas.length; indice++) {
            mensajes.mostrarMensaje((indice + 1) + ". " + unidadesPermitidas[indice]);
        }

        while (true) {
            int opcion = EntradaUtil.leerEntero("Opcion de unidad: ");
            if (opcion >= 1 && opcion <= unidadesPermitidas.length) {
                return unidadesPermitidas[opcion - 1];
            }
            mensajes.mostrarError("Unidad no valida. Elija una opcion de la lista.");
        }
    }
}