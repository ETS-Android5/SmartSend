package servlets.login;

import database.SmartSendDB;
import datafiles.Rider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;

@WebServlet("/LoginRider")
public class LoginRiderServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        String email = request.getParameter("email");
        String password = request.getParameter("email");
        PrintWriter out = response.getWriter();

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            response.setStatus(409);
        } else {
            synchronized (this) {
                Rider rider = SmartSendDB.getRider(email, password);
                LoginRiderResponse responseObj;
                Gson gson = new Gson();

                if (rider != null) {
                    response.setStatus(200);
                    responseObj = new LoginRiderResponse(rider, null);
                }
                else {
                    response.setStatus(400);
                    responseObj = new LoginRiderResponse(rider, "Error trying to log in rider.");
                }
                String json = gson.toJson(responseObj);
                out.println(json);
                out.close();
            }
        }
    }

    private class LoginRiderResponse {
        public Rider rider;
        public String error;

        public LoginRiderResponse(Rider rider, String error) {
            this.rider = rider;
            this.error = error;
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
