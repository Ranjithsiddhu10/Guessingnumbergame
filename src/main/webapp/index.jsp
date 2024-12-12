<!DOCTYPE html>
<html>
<head>
    <title>Number Guessing Game</title>
</head>
<body>
    <h1>Welcome to the Guessing Game!</h1>

    <!-- Form to submit player's name to start the game -->
    <form action="game" method="POST">
        <input type="hidden" name="action" value="startGame">
        <label for="playerName">Enter your name:</label>
        <input type="text" name="playerName" id="playerName" required>
        <button type="submit">Start Game</button>
    </form>

    <!-- Display any message passed from the servlet -->
    <% 
        String message = (String) request.getAttribute("message");
        if (message != null) {
            out.println("<p>" + message + "</p>");
        }
    %>
</body>
</html>
