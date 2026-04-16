<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ec.edu.monster.web.ClienteSOAP"%>
<%
    String mensajeError = "";
    // Si el formulario fue enviado
    if ("POST".equalsIgnoreCase(request.getMethod())) {
        String usr = request.getParameter("usuario");
        String pwd = request.getParameter("contrasenia");
        try {
            // Llamamos a nuestra clase Java
            String tokenGenerado = ClienteSOAP.login(usr, pwd);
            
            if (tokenGenerado != null && !tokenGenerado.contains("Error") && !tokenGenerado.contains("Respuesta no válida")) {
                // Login exitoso: Guardamos el token en la sesión web
                session.setAttribute("tokenGlobal", tokenGenerado);
                response.sendRedirect("conversor.jsp"); // Saltamos a la otra página
                return;
            } else {
                mensajeError = "Credenciales incorrectas.";
            }
        } catch (Exception e) {
            mensajeError = "Falla de conexión: " + e.getMessage();
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Monster Web</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .login-box { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); text-align: center; }
        input[type="text"], input[type="password"] { width: 90%; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 4px; }
        button { background-color: #28a745; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; font-size: 16px;}
        button:hover { background-color: #218838; }
        .error { color: red; font-size: 14px; }
    </style>
</head>
<body>
    <div class="login-box">
        <h2>Bienvenido</h2>
        <p class="error"><%= mensajeError %></p>
        <form method="POST">
            <input type="text" name="usuario" placeholder="Usuario" required><br>
            <input type="password" name="contrasenia" placeholder="Contraseña" required><br>
            <button type="submit">Iniciar Sesión</button>
        </form>
    </div>
</body>
</html>