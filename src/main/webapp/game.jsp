<!DOCTYPE html>
<html>
<head>
    <title>Game - Guess the Number</title>
     <script>
        let startTime = Date.now();
        function startTimer() {
            setInterval(() => {
                let elapsed = Math.floor((Date.now() - startTime) / 1000);
                document.getElementById("timer").innerText = elapsed + " seconds";
            }, 1000);
        }
    </script>
</head>
<body onload="startTimer()">
    <h1>Welcome <%= session.getAttribute("playerName") %>!</h1>
    <h2>Guess the 4-digit number</h2>
    <p>Timer: <span id="timer">0 seconds</span></p>

    <!-- Form to submit a guess -->
    <form action="game" method="POST">
        <input type="hidden" name="action" value="submitGuess">
        <label for="guess">Enter your guess:</label>
        <input type="text" name="guess" id="guess" maxlength="4" required>
        <button type="submit">Submit Guess</button>
    </form>

    <!-- Display any message passed from the servlet -->
    <% 
        String message = (String) request.getAttribute("message");
        if (message != null) {
            out.println("<p>" + message + "</p>");
        }
    %>

    <!-- Button to restart the game (optional) -->
    <form action="game" method="POST">
        <input type="hidden" name="action" value="startGame">
        <button type="submit">Start New Game</button>
    </form>
     <p><strong>Feedback:</strong> ${feedback}</p>
      <p><strong>Your Score:</strong> ${yourScore}</p>
     <p><strong>Best Player:</strong> ${bestPlayer}</p>
     <p><strong>Best Score:</strong> ${bestScore}</p>
</body>
</html>
