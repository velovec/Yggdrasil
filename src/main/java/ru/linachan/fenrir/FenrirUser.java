package ru.linachan.fenrir;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
public class FenrirUser {

    private String userName = null;
    private String displayName = null;
    private Set<String> groups = new HashSet<String>();

    public FenrirUser(SearchResult result) throws NamingException {
        Attributes attr = result.getAttributes();

        NamingEnumeration<String> groups = (NamingEnumeration<String>) attr.get("memberOf").getAll();
        while (groups.hasMore()) {
            this.groups.add(groups.nextElement());
        }

        this.userName = (String) attr.get("sAMAccountName").get();
        this.displayName = (String) attr.get("displayName").get();
    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public boolean inGroup(String group) {
        try {
            if (group != null) {
                String groupName = new String(group.getBytes("ISO-8859-1"), "UTF-8");
                return this.groups.contains(groupName);
            } else {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }
}
