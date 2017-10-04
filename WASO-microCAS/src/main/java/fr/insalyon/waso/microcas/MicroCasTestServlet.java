package fr.insalyon.waso.microcas;

import fr.insalyon.waso.util.MicroCasFilter;
import com.google.gson.JsonObject;
import fr.insalyon.waso.util.JsonServletHelper;
import java.io.IOException;
import java.util.Date;
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
public class MicroCasTestServlet extends HttpServlet {

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

        HttpSession session = request.getSession(true);

        request.setCharacterEncoding(JsonServletHelper.ENCODING_UTF8);

        String todo = request.getParameter(MicroCasServlet.REQUEST_ACTION);

        if (MicroCasServlet.REQUEST_ACTION_TEST.equals(todo)) {

            Date validated = (Date) session.getAttribute(MicroCasFilter.SESSION_MICROCAS_CLIENT_VALID_DATE);
            String login = (String) session.getAttribute(MicroCasFilter.SESSION_MICROCAS_CLIENT_VALID_LOGIN);
            Map<String, String> user = (Map<String, String>) session.getAttribute(MicroCasFilter.SESSION_MICROCAS_CLIENT_VALID_USER);

            JsonObject jsonItem = new JsonObject();

            if (validated == null) {
                jsonItem.addProperty("nosession", "...");
            } else {
                jsonItem.addProperty("login", login);

                JsonObject jsonItemDetail = new JsonObject();
                for (Map.Entry<String, String> attribute : user.entrySet()) {
                    jsonItemDetail.addProperty(attribute.getKey(), attribute.getValue());
                }
                jsonItem.add("description", jsonItemDetail);
            }

            JsonObject container = new JsonObject();
            container.add("session", jsonItem);

            JsonServletHelper.printJsonOutput(response, container);
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
        return "μCAS Test Servlet";
    }

}
