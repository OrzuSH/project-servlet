package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Getting the current session
        HttpSession currentSession = req.getSession();

        // Getting the game board object from the session
        Field field = extractField(currentSession);

        // getting the index of the cell where the click occurred
        int index = getSelectedIndex(req);
        Sign currentSign = field.getField().get(index);

        // Check that the cell that was clicked is empty.
        // Otherwise we do nothing and send the user to the same page without changes
        // parameters in the session
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        // we put a cross in the cell that the user clicked on
        field.getField().put(index, Sign.CROSS);

        // Check if the cross has won after adding the user's last click
        if (checkWin(resp, currentSession, field)) {
            return;
        }

        // We get an empty field cell
        int emptyFieldIndex = field.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            // Check if the zero won after adding the last zero
            if (checkWin(resp, currentSession, field)) {
                return;
            }
        }
        // If there is no empty cell and no one has won, it means it's a draw
        else {
            // Adding a flag to the session that indicates that a draw has occurred
            currentSession.setAttribute("draw", true);

            // Counting the list of icons
            List<Sign> data = field.getFieldData();

            // Updating this list in the session
            currentSession.setAttribute("data", data);

            // Helmet redirect
            resp.sendRedirect("/index.jsp");
            return;
        }

        // Counting the list of icons
        List<Sign> data = field.getFieldData();

        // Updating the field object and the list of icons in the session
        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }

    /**
     * The method checks if there are three tic-tac-toe marks in a row.
     * Returns true/false
     */
    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            // Adding a flag that shows that someone has won
            currentSession.setAttribute("winner", winner);

            // Counting the list of icons
            List<Sign> data = field.getFieldData();

            // Updating this list in the session
            currentSession.setAttribute("data", data);

            // Helmet redirect
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }
}

