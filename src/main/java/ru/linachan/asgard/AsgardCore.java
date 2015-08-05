package ru.linachan.asgard;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.sql.SQLException;

public class AsgardCore {

    private YggdrasilCore core;
    private AsgardDBManager db;

    public AsgardCore(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;

        String db_driver = this.core.getConfig("AsgardDBDriver", "com.mysql.jdbc.Driver");
        String db_url = this.core.getConfig("AsgardDBUrl", "jdbc:mysql://localhost/asgard");
        String db_user = this.core.getConfig("AsgardDBUser", "asgard");
        String db_password = this.core.getConfig("AsgardDBPassword", "");

        this.core.logInfo("Initializing Asgard Data Storage...");
        try {
            this.db = new AsgardDBManager(this.core, db_driver, db_url, db_user, db_password);
        } catch (SQLException e) {
            this.core.logWarning("SQL Error: " + e.getSQLState() + " : " + e.getMessage());
            this.core.shutdownYggdrasil();
        } catch (ClassNotFoundException e) {
            this.core.logWarning("SQL Error: " + e.getMessage());
            this.core.shutdownYggdrasil();
        }
    }

    public <T extends AsgardSelectable> AsgardSQLQuery<T> query(T model) {
        return this.db.query(model);
    }

    public boolean executeTests() {
        return true;
    }
}
