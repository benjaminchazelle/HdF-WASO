package fr.insalyon.waso.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author WASO Team
 */
public class JsonServletHelper {

    public static final String ENCODING_UTF8 = "UTF-8";
    public static final String CONTENTTYPE_JSON = "application/json";
    
    public static void printJsonOutput(HttpServletResponse response, JsonObject container) throws IOException {

        response.setContentType(CONTENTTYPE_JSON);
        response.setCharacterEncoding(ENCODING_UTF8);

        
        Gson gson = new GsonBuilder().create();
        JsonWriter jsonWriter = new JsonWriter(response.getWriter());
        jsonWriter.setIndent("  ");
        gson.toJson(container, jsonWriter);
        jsonWriter.close();

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        PrintWriter out = response.getWriter();
//        out.println(gson.toJson(container));
//        out.close();
    }
}
