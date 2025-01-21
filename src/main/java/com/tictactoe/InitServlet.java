package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Creating a new session
        HttpSession currentSession = req.getSession(true);

        // Creating a playing field
        Field field = new Field();
        Map<Integer, Sign> fieldData = field.getField();

        // Getting a list of field values
        List<Sign> data = field.getFieldData();

        // Adding field parameters from the session (it will be necessary to store the state between requests)
        currentSession.setAttribute("field", field);
        // and field values sorted by index (needed for drawing tic-tac-toe)
        currentSession.setAttribute("data", data);

        // Redirecting the request to the index page.jsp via the server
        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}