package com.game;

import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.concurrent.TimeUnit;

public class GameServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/guessing_game";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private String generatedNumber = null;
    private long startTime;
    private int guessCount = 0;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String playerName = request.getParameter("playerName");
       
        
        String action = request.getParameter("action");
        System.out.println("playerName====>"+ playerName);
        System.out.println("action====>"+ action);
        if ("submitGuess".equals(action)) {
        	 String userGuess = request.getParameter("guess");
        	 playerName = (String) request.getSession().getAttribute("playerName");
        	 if (generatedNumber == null) {
                 generatedNumber = GameUtils.generateUniqueNumber();
                 startTime = System.currentTimeMillis();
             }

             guessCount++;
             String feedback = getFeedback(generatedNumber, userGuess);
             boolean isCorrect = generatedNumber.equals(userGuess);

             if (isCorrect) {
                 long endTime = System.currentTimeMillis();
                 float timeTaken = (endTime - startTime) / 1000F;

                 // Calculate score: (Lower time and guesses yield better score)
                 float score = 10000 / (guessCount * timeTaken);
                 
                 savePlayerScore(playerName, guessCount, timeTaken, score);
                 updateBestScore(playerName, score);

                 request.setAttribute("message", "Congratulations! You guessed it right.");
                 request.setAttribute("time", timeTaken);
                 request.setAttribute("score", score);
                 request.setAttribute("bestPlayer", getBestPlayer());
                 request.setAttribute("bestScore", getBestScore());
                 request.setAttribute("yourScore", score);
                 

                 // Reset game state
                 generatedNumber = null;
                 guessCount = 0;
             } else {
                 request.setAttribute("feedback", feedback);
             }

             request.getRequestDispatcher("game.jsp").forward(request, response);
        	
        } else {
            System.out.println("playerName ==>"+ playerName);
            // Check if player name is valid
            if (playerName != null && !playerName.isEmpty()) {
                // Store player's name in the session
                request.getSession().setAttribute("playerName", playerName);

                // Generate a random 4-digit number (target number)
                int targetNumber = (int) (Math.random() * 9000) + 1000;

                // Store the target number in the session
                request.getSession().setAttribute("targetNumber", targetNumber);

                // Initialize guesses count in session
                request.getSession().setAttribute("guesses", 0);

                // Set a message to be displayed on game.jsp
                request.setAttribute("message", "Game started! Try guessing the 4-digit number.");

                // Forward to game.jsp
                request.getRequestDispatcher("game.jsp").forward(request, response);
            } else {
                // If player name is invalid, return to index.jsp with an error message
                request.setAttribute("message", "Please enter a valid name.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }
       
    }

    private String getFeedback(String target, String guess) {
    	System.out.println("Guess parameter retrieved: " + guess + " Targeted: "+ target);
        StringBuilder feedback = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                feedback.append("+");
            } else if (target.contains(String.valueOf(guess.charAt(i)))) {
                feedback.append("-");
            }
        }
        return feedback.toString();
    }

    private void savePlayerScore(String playerName, int guesses, float time, float score) {
    	 try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO number_game (player_name, guesses, time_taken, score) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, playerName);
            ps.setInt(2, guesses);
            ps.setFloat(3, time);
            ps.setFloat(4, score);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBestScore(String playerName, float score) {
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("UPDATE number_game SET player_name = ?, score = ? WHERE id = 1 AND score > ?")) {
            ps.setString(1, playerName);
            ps.setFloat(2, score);
            ps.setFloat(3, score);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String getBestScore() {
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT score FROM  number_game WHERE score = (Select max(score) from number_game)")) {
            if (rs.next()) {
                return rs.getString("score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    private String getBestPlayer() {
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT player_name FROM  number_game WHERE score = (Select max(score) from number_game)")) {
            if (rs.next()) {
                return rs.getString("player_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }
}
