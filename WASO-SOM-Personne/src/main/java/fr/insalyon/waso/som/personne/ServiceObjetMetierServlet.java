package fr.insalyon.waso.som.personne;

import com.google.gson.JsonObject;
import fr.insalyon.waso.util.DBConnection;
import fr.insalyon.waso.util.JsonServletHelper;
import fr.insalyon.waso.util.exception.DBException;
import fr.insalyon.waso.util.exception.ServiceException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author WASO Team
 */
public class ServiceObjetMetierServlet extends HttpServlet {

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
        request.setCharacterEncoding(JsonServletHelper.ENCODING_UTF8);

        try {

            String som = request.getParameter("SOM");

            DBConnection connection = new DBConnection(
                    this.getInitParameter("JDBC-Personne-URL"),
                    this.getInitParameter("JDBC-Personne-User"),
                    this.getInitParameter("JDBC-Personne-Password"),
                    "PERSONNE"
            );

            JsonObject container = new JsonObject();

            ServiceObjetMetier service = new ServiceObjetMetier(connection, container);

            boolean serviceCalled = true;

            if ("getListePersonne".equals(som)) {
                
                service.getListePersonne();
                
            } else if ("rechercherPersonneParNom".equals(som)) {
                
                String nomPersonneParametre = request.getParameter("nom-personne");
                if (nomPersonneParametre == null) {
                    throw new ServiceException("Paramètres incomplets");
                }
                String nomPersonne = nomPersonneParametre;

                //service.rechercherPersonneParNom(nomPersonne);
                
            } else {
                
                serviceCalled = false;
            }

            if (serviceCalled) {

                JsonServletHelper.printJsonOutput(response, container);

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Service '" + som + "' inexistant");
                System.err.println("/!\\ Error method /!\\");
            }

        } catch (DBException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB Exception: " + ex.getMessage());
            ex.printStackTrace(System.err);
        } catch (ServiceException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Service Exception: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Service Objet Metier Servlet";
    }

}
