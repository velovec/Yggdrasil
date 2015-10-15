package ru.linachan.fenrir;

import ru.linachan.yggdrasil.component.YggdrasilComponent;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class FenrirCore extends YggdrasilComponent {

    private String ldapServer;
    private String ldapBaseDN;
    private String ldapBaseDomain;
    private String ldapAdminGroup;
    private String ldapUserGroup;

    private Map<String, FenrirSession> sessions = new HashMap<String, FenrirSession>();

    @Override
    protected void onInit() {
        ldapServer     = core.getConfig("FenrirLDAPServer", "127.0.0.1");
        ldapBaseDN     = core.getConfig("FenrirLDAPBaseDN", "DC=EXAMPLE,DC=COM");
        ldapBaseDomain = core.getConfig("FenrirLDAPBaseDomain", "EXAMPLE");
        ldapAdminGroup = core.getConfig("FenrirLDAPAdminGroup", "CN=Administrators,DC=EXAMPLE,DC=COM");
        ldapUserGroup  = core.getConfig("FenrirLDAPUserGroup", "CN=User,DC=EXAMPLE,DC=COM");
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean executeTests() {
        return true;
    }

    public boolean checkAuth(String token) {
        return sessions.containsKey(token);
    }

    private SearchResult authViaLDAP(String username, String password) {
        Hashtable<String, Object> env = new Hashtable<String, Object>(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapServer);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapBaseDomain + "\\" + username);
        env.put(Context.SECURITY_CREDENTIALS, password);

        DirContext context = null;

        try {
            context = new InitialDirContext(env);
        } catch (NamingException e) {
            core.logWarning("LDAPAuthException: " + e.getMessage());
        }

        SearchResult result = null;

        if (context != null) {
            try {
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                String searchFilter = "(&(objectClass=user)(sAMAccountName=" + username + "))";

                NamingEnumeration<SearchResult> results = context.search(ldapBaseDN, searchFilter, searchControls);

                if(results.hasMoreElements()) {
                    result = results.nextElement();

                    if(results.hasMoreElements()) {
                        core.logWarning("LDAPAuthWarning:: More than one record found.");
                    }
                }

                context.close();
            } catch (NamingException e) {
                core.logWarning("LDAPAuthException: " + e.getExplanation());
            }
        }
        return result;
    }

    public FenrirUser auth(String username, String password) throws NamingException {
        FenrirUser user = null;

        SearchResult result = authViaLDAP(username, password);

        if (result != null) {
            user = new FenrirUser(result);
            if (user.inGroup(ldapUserGroup)) {
                return user;
            }
        }

        return null;
    }

    public String newSession(FenrirUser user, String clientIP) {
        FenrirSession session = new FenrirSession(user, clientIP, generateToken());

        String token = session.getToken();

        sessions.put(token, session);

        return token;
    }

    public FenrirSession getSessionInfo(String token) {
        if (sessions.containsKey(token))
            return sessions.get(token);
        return null;
    }

    public boolean closeSession(String token) {
        if(sessions.containsKey(token)) {
            sessions.remove(token);
            return true;
        } else {
            return false;
        }
    }

    private String getRandomHexString(){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while(sb.length() < 8){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 8);
    }

    public String generateToken() {
        String token;
        while (true) {
            token = getRandomHexString();
            if (!sessions.containsKey(token)) {
                break;
            }
        }
        return token;
    }

    public boolean isAdmin(String token) {
        FenrirUser user = getSessionInfo(token).getUser();
        return user != null && user.inGroup(ldapAdminGroup);
    }
}
