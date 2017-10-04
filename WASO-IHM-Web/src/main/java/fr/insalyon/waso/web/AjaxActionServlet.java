package fr.insalyon.waso.web;

import com.google.gson.JsonObject;
import fr.insalyon.waso.util.JsonServletHelper;
import fr.insalyon.waso.util.MicroCasFilter;
import fr.insalyon.waso.util.exception.ServiceException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author WASO Team
 */
public class AjaxActionServlet extends HttpServlet {

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
        
        try {

            String action = request.getParameter("action");

            JsonObject container = new JsonObject();

            AjaxAction ajaxAction = new AjaxAction(this.getInitParameter("URL-SMA"), container);

            boolean actionCalled = true;

            if ("getListeClient".equals(action)) {

                // 1. Lecture des paramètres de la Recherche Client
                
                String numeroParametre = request.getParameter("numero-client");
                String denominationParametre = request.getParameter("denomination-client");
                String nomPersonneParametre = request.getParameter("nom-personne");
                String villeParametre = request.getParameter("ville-client");

                
                // 1. Choix de l'action en fonction des paramètres rempli
                
                if (numeroParametre != null && numeroParametre.length() > 0) {
                    Integer numero = Integer.parseInt(numeroParametre);
                    ajaxAction.rechercherClientParNumero(numero);
                }
                else if (denominationParametre != null && denominationParametre.length() > 0) {
                    String denomination = denominationParametre;
                    String ville = villeParametre;
                    ajaxAction.rechercherClientParDenomination(denomination,ville);
                }
                else if (nomPersonneParametre != null && nomPersonneParametre.length() > 0) {
                    String nomPersonne = nomPersonneParametre;
                    String ville = villeParametre;
                    ajaxAction.rechercherClientParNomPersonne(nomPersonne,ville);
                }
                else {
                    ajaxAction.getListeClient();
                }

            } else {

                actionCalled = false;
            }

            ajaxAction.release();
            
            if (actionCalled) {

                container.addProperty("IHM_microCAS_User", (String) session.getAttribute(MicroCasFilter.SESSION_MICROCAS_CLIENT_VALID_LOGIN));
                JsonServletHelper.printJsonOutput(response, container);

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action '" + action + "' inexistante");
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
        return "AJAX Action Servlet";
    }

}
