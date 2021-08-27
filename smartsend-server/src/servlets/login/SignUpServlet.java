//package servlets.login;
//
//import datafiles.user.User;
//import exceptions.name.NameException;
//import logicinterface.RizpaInterface;
//import utils.ServletUtils;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//import static constants.Constants.USERNAME;
//
//public class SignUpServlet extends HttpServlet {
//
//
//
//     /**
//     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
//     * methods.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        final String CONTEXT_PATH = request.getContextPath();
//        final String HOME_URL = CONTEXT_PATH + "/pages/dashboard/dashboard.html";
//        final String SIGN_UP_URL = CONTEXT_PATH + "/pages/signup/signup.html";
//
//        response.setContentType("text/html;charset=UTF-8");
//        String usernameFromParameter = request.getParameter(USERNAME);
//
//        if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
//            response.sendRedirect(SIGN_UP_URL);
//        } else {
//            usernameFromParameter = usernameFromParameter.trim();
//
//            synchronized (this) {
//                RizpaInterface engine = ServletUtils.getEngine(getServletContext());
//
//                if (engine.isUserExists(usernameFromParameter)) {
//                    String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
//
//                    if (usernameFromParameter.equalsIgnoreCase("admin")) {
//                        errorMessage = "Invalid username entered, please try different name.";
//                    }
//                    PrintWriter out = response.getWriter();
//
//                    response.setStatus(400);
//                    out.println(errorMessage);
//                    out.flush();
//                }
//                else {
//                    try {
//                        User newUser = new User(usernameFromParameter);
//                        engine.addUser(usernameFromParameter, newUser);
//                        engine.addOnlineUser(usernameFromParameter, newUser);
//                    } catch (NameException ignored) {
//                    }
//                    request.getSession(true).setAttribute(USERNAME, usernameFromParameter);
//                    response.getOutputStream().println(HOME_URL);
//                }
//            }
//        }
//    }
//
//// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
//    /**
//     * Handles the HTTP <code>GET</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    /**
//     * Handles the HTTP <code>POST</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    /**
//     * Returns a short description of the servlet.
//     *
//     * @return a String containing servlet description
//     */
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }// </editor-fold>
//}
