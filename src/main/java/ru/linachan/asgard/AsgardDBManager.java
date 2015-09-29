package ru.linachan.asgard;

import ru.linachan.yggdrasil.YggdrasilCore;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AsgardDBManager {

    private YggdrasilCore core;

    private Connection c = null;

    public AsgardDBManager(YggdrasilCore core, String db_driver, String db_url, String db_user, String db_password) {
        this.core = core;

        try {
            Class.forName(db_driver);
            c = DriverManager.getConnection(db_url, db_user, db_password);
        } catch (ClassNotFoundException | SQLException e) {
            core.logException(e);
        }

        if (this.checkConnection()) {
            this.core.logInfo("Asgard Data Storage on-line...");
        }
    }

    public boolean checkConnection() {
        return c != null;
    }

    public DSLContext getContext() {
        return DSL.using(c, SQLDialect.MYSQL);
    }
}
