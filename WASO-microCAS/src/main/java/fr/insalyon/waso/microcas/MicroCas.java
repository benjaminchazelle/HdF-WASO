package fr.insalyon.waso.microcas;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 *
 * @author WASO Team
 */
public class MicroCas {

    protected static long TICKET_COUNTER = Long.MAX_VALUE / 8 + Math.round((Long.MAX_VALUE / 16) * Math.random());
    protected static Map<String, Map<String, String>> TICKETS = new HashMap<String, Map<String, String>>();

    protected final String ldapServerUrl;

    public MicroCas(String ldapServerUrl) {
        this.ldapServerUrl = ldapServerUrl;
    }

    public Map<String, String> checkLogin(String login, String password) {

        Map<String, String> result = null;

        if (login != null && password != null) {

            if ("test".equals(this.ldapServerUrl)) {
                if (login.equals(password)) {
                    result = new HashMap<String, String>();
                    result.put("login", login);
                    result.put("description", "Name of <" + login + ">");
                }
            } else {

                try {
                    Properties props = new Properties();
                    props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                    props.put(Context.PROVIDER_URL, this.ldapServerUrl);

                    String principalName = "cn=" + login + ",dc=mars";
                    props.put(Context.SECURITY_AUTHENTICATION, "simple");
                    props.put(Context.SECURITY_PRINCIPAL, principalName);
                    props.put(Context.SECURITY_CREDENTIALS, password);

                    InitialDirContext context = new InitialDirContext(props);

                    SearchControls ctrls = new SearchControls();
                    ctrls.setReturningAttributes(new String[]{"cn", "uid", "description", "mail"});
                    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                    NamingEnumeration<SearchResult> answers
                            = context.search("dc=mars", "(cn=" + login + ")", ctrls);

                    if (answers.hasMore()) {

                        Map<String, String> user = new HashMap<String, String>();
                        user.put("login", login);

                        Attributes attributes = answers.next().getAttributes();
                        //String name = attributes.get("displayname").get().toString();
                        String name = (attributes.get("description") != null ? attributes.get("description").get().toString() : "<description>");
                        System.err.println("LDAP @ " + login + " => " + name);
                        NamingEnumeration<? extends Attribute> allAttributes = attributes.getAll();
                        while (allAttributes.hasMore()) {
                            Attribute attribute = allAttributes.next();

                            user.put(attribute.getID(), attribute.get().toString());

                            NamingEnumeration<?> allValues = attribute.getAll();
                            while (allValues.hasMore()) {
                                Object value = allValues.next();
                                System.err.println(" - " + attribute.getID() + ": " + value.toString());
                            }
                        }

                        result = user;
                    }

                } catch (NamingException e) {
                    result = null;
                    e.printStackTrace(System.err);
                }
            }
        }

        return result;
    }

    public String createTicket(String service, Map<String, String> user) {

        String ticket = String.format("%016X", ++TICKET_COUNTER);

        TICKETS.put(ticket, new HashMap<String, String>(user));

        return ticket;
    }

    public Map<String, String> checkTicket(String service, String ticket) {

        Map<String, String> user = TICKETS.remove(ticket);

        return user;
    }

}
