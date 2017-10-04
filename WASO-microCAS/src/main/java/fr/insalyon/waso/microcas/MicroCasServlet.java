package fr.insalyon.waso.microcas;

import com.google.gson.JsonObject;
import fr.insalyon.waso.util.JsonServletHelper;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author WASO Team
 */
public class MicroCasServlet extends HttpServlet {

    public static final String REQUEST_ACTION = "action";
    public static final String REQUEST_ACTION_LOGIN = "login";
    public static final String REQUEST_ACTION_CHECK = "check";
    public static final String REQUEST_ACTION_TEST = "test";
    public static final String REQUEST_ACTION_LOGOUT = "logout";
    public static final String REQUEST_SERVICE = "service";
    public static final String REQUEST_LOGIN = "login";
    public static final String REQUEST_PASSWORD = "password";
    public static final String REQUEST_TICKET = "ticket";
    public static final String SESSION_MICROCAS_SERVER_LOGIN = "MicroCAS:Server:Login";
    public static final String SESSION_MICROCAS_SERVER_USER = "MicroCAS:Server:User";
    public static final String SESSION_MICROCAS_SERVER_SERVICE = "MicroCAS:Server:Service";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ldapServerUrl = this.getInitParameter("LDAP-Server-URL");
        MicroCas microCas = new MicroCas(ldapServerUrl);

        HttpSession session = request.getSession(true);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0);

        request.setCharacterEncoding(JsonServletHelper.ENCODING_UTF8);

        String action = request.getParameter(REQUEST_ACTION);

        if (action == null) {

            String service = request.getParameter(REQUEST_SERVICE);
            if (service == null || service.length() == 0) {

                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                System.err.println("/!\\ Error method /!\\");
            } else {

                String login = (String) session.getAttribute(SESSION_MICROCAS_SERVER_LOGIN);
                Map<String, String> user = (Map<String, String>) session.getAttribute(SESSION_MICROCAS_SERVER_USER);

                if (login == null) {
                    session.setAttribute(SESSION_MICROCAS_SERVER_SERVICE, service);
                    this.getServletContext().getRequestDispatcher("/login.html").forward(request, response);
                } else {

                    String ticket = microCas.createTicket(service, user);
                    response.sendRedirect(service + "?" + REQUEST_TICKET + "=" + ticket);
                }
            }

        } else if (REQUEST_ACTION_LOGIN.equals(action)) {

            String login = request.getParameter(REQUEST_LOGIN);
            String password = request.getParameter(REQUEST_PASSWORD);

            Map<String, String> user = microCas.checkLogin(login, password);

            if (user != null) {

                session.setAttribute(SESSION_MICROCAS_SERVER_LOGIN, login);
                session.setAttribute(SESSION_MICROCAS_SERVER_USER, user);

                String service = (String) session.getAttribute(SESSION_MICROCAS_SERVER_SERVICE);
                String ticket = microCas.createTicket(service, user);
                response.sendRedirect(service + "?" + REQUEST_TICKET + "=" + ticket);

            } else {
                this.getServletContext().getRequestDispatcher("/login.html").forward(request, response);
            }

            return;
        } else if (REQUEST_ACTION_CHECK.equals(action)) {

            String service = request.getParameter(REQUEST_SERVICE);
            String ticket = request.getParameter(REQUEST_TICKET);
            Map<String, String> user = microCas.checkTicket(service, ticket);

            JsonObject jsonItem = new JsonObject();

            jsonItem.addProperty("service", service);
            jsonItem.addProperty("ticket", ticket);

            if (user == null) {
                jsonItem.addProperty("error", "Ticket not valid");
            } else {

                jsonItem.addProperty("login", user.get("login"));
                JsonObject jsonItemDetails = new JsonObject();
                for (Map.Entry<String, String> attribute : user.entrySet()) {
                    jsonItemDetails.addProperty(attribute.getKey(), attribute.getValue());
                }
                jsonItem.add("details", jsonItemDetails);
            }

            JsonObject container = new JsonObject();
            container.add("authentication", jsonItem);

            JsonServletHelper.printJsonOutput(response, container);

        } else if (REQUEST_ACTION_LOGOUT.equals(action)) {

            session.removeAttribute(SESSION_MICROCAS_SERVER_LOGIN);
            session.removeAttribute(SESSION_MICROCAS_SERVER_USER);
            session.removeAttribute(SESSION_MICROCAS_SERVER_SERVICE);
            //session.invalidate();

            String service = request.getParameter(REQUEST_SERVICE);

            if (service == null) {
                this.getServletContext().getRequestDispatcher("/login.html").forward(request, response);
            } else {
                response.sendRedirect(service);
            }

        } else {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            System.err.println("/!\\ Error method /!\\");
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "μCAS Servlet";
    }

}
