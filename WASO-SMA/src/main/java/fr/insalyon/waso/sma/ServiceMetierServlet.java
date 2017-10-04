package fr.insalyon.waso.sma;

import com.google.gson.JsonObject;
import fr.insalyon.waso.util.JsonServletHelper;
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
public class ServiceMetierServlet extends HttpServlet {

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

            String sma = request.getParameter("SMA");

            JsonObject container = new JsonObject();

            ServiceMetier service = new ServiceMetier(this.getInitParameter("URL-SOM-Client"), this.getInitParameter("URL-SOM-Personne"), container);

            boolean serviceCalled = true;

            if ("getListeClient".equals(sma)) {

                service.getListeClient();

            } else if ("rechercherClientParNumero".equals(sma)) {

                String numeroParametre = request.getParameter("numero");
                if (numeroParametre == null) {
                    throw new ServiceException("Paramètres incomplets");
                }
                Integer numero = Integer.parseInt(numeroParametre);
                
                //service.rechercherClientParNumero(numero);

            } else if ("rechercherClientParDenomination".equals(sma)) {


                // service.rechercherClientParDenomination(denomination,ville);

            } else if ("rechercherClientParNomPersonne".equals(sma)) {

                
                // service.rechercherClientParNomPersonne(nomPersonne,ville);

            } else {

                serviceCalled = false;
            }

            service.release();

            
            if (serviceCalled) {

                JsonServletHelper.printJsonOutput(response, container);

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Service '" + sma + "' inexistant");
                System.err.println("/!\\ Error method /!\\");
            }

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
        return "Service Metier Servlet";
    }

}
