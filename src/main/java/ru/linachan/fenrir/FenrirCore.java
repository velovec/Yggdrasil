package ru.linachan.fenrir;

import ru.linachan.yggdrasil.YggdrasilCore;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class FenrirCore {

    private YggdrasilCore core;

    private String ldapServer;
    private String ldapBaseDN;
    private String ldapBaseDomain;
    private String ldapAdminGroup;
    private String ldapUserGroup;

    private Map<String, FenrirSession> sessions = new HashMap<String, FenrirSession>();

    public FenrirCore(YggdrasilCore core) {
        this.core = core;

        this.ldapServer     = this.core.getConfig("FenrirLDAPServer", "127.0.0.1");
        this.ldapBaseDN     = this.core.getConfig("FenrirLDAPBaseDN", "DC=EXAMPLE,DC=COM");
        this.ldapBaseDomain = this.core.getConfig("FenrirLDAPBaseDomain", "EXAMPLE");
        this.ldapAdminGroup = this.core.getConfig("FenrirLDAPAdminGroup", "CN=Administrators,DC=EXAMPLE,DC=COM");
        this.ldapUserGroup  = this.core.getConfig("FenrirLDAPUserGroup", "CN=User,DC=EXAMPLE,DC=COM");

        this.core.logInfo("Initializing Fenrir Auth System...");
    }

    public boolean checkAuth(String token) {
        return this.sessions.containsKey(token);
    }

    private SearchResult authViaLDAP(String username, String password) {
        Hashtable<String, Object> env = new Hashtable<String, Object>(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, this.ldapServer);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, this.ldapBaseDomain + "\\" + username);
        env.put(Context.SECURITY_CREDENTIALS, password);

        DirContext context = null;

        try {
            context = new InitialDirContext(env);
        } catch (NamingException e) {
            this.core.logWarning("LDAPAuthException: " + e.getMessage());
        }

        SearchResult result = null;

        if (context != null) {
            try {
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                String searchFilter = "(&(objectClass=user)(sAMAccountName=" + username + "))";

                NamingEnumeration<SearchResult> results = context.search(this.ldapBaseDN, searchFilter, searchControls);

                if(results.hasMoreElements()) {
                    result = results.nextElement();

                    if(results.hasMoreElements()) {
                        this.core.logWarning("LDAPAuthWarning:: More than one record found.");
                    }
                }

                context.close();
            } catch (NamingException e) {
                this.core.logWarning("LDAPAuthException: " + e.getExplanation());
            }
        }
        return result;
    }

    public FenrirUser auth(String username, String password) throws NamingException {
        FenrirUser user = null;

        SearchResult result = this.authViaLDAP(username, password);

        if (result != null) {
            user = new FenrirUser(result);
            if (user.inGroup(this.ldapUserGroup)) {
                return user;
            }
        }

        return null;
    }

    public String newSession(FenrirUser user, String clientIP) {
        FenrirSession session = new FenrirSession(user, clientIP, generateToken());

        String token = session.getToken();

        this.sessions.put(token, session);

        return token;
    }

    public FenrirSession getSessionInfo(String token) {
        if (this.sessions.containsKey(token))
            return this.sessions.get(token);
        return null;
    }

    public boolean closeSession(String token) {
        if(this.sessions.containsKey(token)) {
            this.sessions.remove(token);
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
            if (!this.sessions.containsKey(token)) {
                break;
            }
        }
        return token;
    }

    public boolean isAdmin(String token) {
        FenrirUser user = this.getSessionInfo(token).getUser();
        return user != null && user.inGroup(this.ldapAdminGroup);
    }

    public boolean executeTests() {
        return true;
    }
}
