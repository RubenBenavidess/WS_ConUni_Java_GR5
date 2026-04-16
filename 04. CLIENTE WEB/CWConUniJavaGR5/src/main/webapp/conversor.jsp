<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ec.edu.monster.web.ClienteSOAP"%>
<%
    // 1. Verificamos la seguridad (¿Hay token guardado?)
    String tokenActivo = (String) session.getAttribute("tokenGlobal");
    if (tokenActivo == null || tokenActivo.isEmpty()) {
        response.sendRedirect("index.jsp"); // Expulsado al login
        return;
    }

    // 2. Procesar conversión si se hizo clic en el botón
    String resultado = "";
    if ("POST".equalsIgnoreCase(request.getMethod())) {
        String val = request.getParameter("valor");
        String ori = request.getParameter("origen");
        String des = request.getParameter("destino");
        try {
            // Enviamos la petición inyectando el token guardado
            resultado = ClienteSOAP.convertir(tokenActivo, val, ori, des);
        } catch (Exception e) {
            resultado = "Error: " + e.getMessage();
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Conversor Monster Web</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f9; padding: 50px; }
        .container { background: white; max-width: 500px; margin: auto; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        select, input[type="number"], input[type="text"] { width: 100%; padding: 10px; margin-top: 5px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;}
        button { background-color: #007bff; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; font-size: 16px; width: 100%;}
        button:hover { background-color: #0056b3; }
        .resultado { margin-top: 20px; padding: 15px; background-color: #e9ecef; border-radius: 4px; font-weight: bold; text-align: center; font-size: 20px;}
        .token-info { font-size: 12px; color: gray; text-align: right; margin-bottom: 20px;}
    </style>
</head>
<body>
    <div class="container">
        <div class="token-info">Token activo: <%= tokenActivo.substring(0, 8) %>...</div>
        <h2>Conversor de Longitud</h2>
        
        <form method="POST">
            <label>Valor a convertir:</label>
            <input type="number" step="any" name="valor" required>
            
            <label>Unidad de Origen:</label>
            <select name="origen">
                <option value="METRO">Metro</option>
                <option value="KILOMETRO">Kilómetro</option>
                <option value="CENTIMETRO">Centímetro</option>
                <option value="MILIMETRO">Milímetro</option>
            </select>
            
            <label>Unidad de Destino:</label>
            <select name="destino">
                <option value="KILOMETRO">Kilómetro</option>
                <option value="METRO">Metro</option>
                <option value="CENTIMETRO">Centímetro</option>
                <option value="MILIMETRO">Milímetro</option>
            </select>
            
            <button type="submit">Convertir</button>
        </form>

        <% if (!resultado.isEmpty()) { %>
            <div class="resultado">Resultado: <%= resultado %></div>
        <% } %>
    </div>
</body>
</html>