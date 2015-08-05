package ru.linachan.asgard;

import ru.linachan.util.Pair;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@SuppressWarnings({"unchecked", "unused"})
public class AsgardSQLQuery<T extends AsgardSelectable> implements Cloneable{

    private T model;
    private AsgardDBManager db;
    private Map<String, Pair<Boolean, Object>> filters = new HashMap<>();

    private int limit = 0;
    private Boolean limit_set = false;

    private int offset = 0;
    private Boolean offset_set = false;

    private String order_by = "";
    private Boolean order_by_asc = false;
    private Boolean order_by_set = false;

    private Boolean hasColumn(String column) {
        for (Field field : model.getClass().getFields()) {
            if (field.getName().equals(column)&&!Arrays.asList(model.__ignored).contains(column)) {
                return true;
            }
        }
        return false;
    }

    public AsgardSQLQuery(AsgardDBManager db, T model) {
        this.model = model;
        this.db = db;
    }

    public AsgardSQLQuery<T> eq(String field, Object value) throws CloneNotSupportedException {
        if (hasColumn(field)) {
            filters.put(field, new Pair(true, value));
        }
        return (AsgardSQLQuery<T>) this.clone();
    }

    public AsgardSQLQuery<T> like(String field, Object value) throws CloneNotSupportedException {
        if (hasColumn(field)) {
            filters.put(field, new Pair(false, value));
        }
        return (AsgardSQLQuery<T>) this.clone();
    }

    public AsgardSQLQuery<T> order_by(String field, Boolean asc) throws CloneNotSupportedException {
        if (hasColumn(field)) {
            this.order_by_set = true;
            this.order_by = field;
            this.order_by_asc = asc;
        }
        return (AsgardSQLQuery<T>) this.clone();
    }

    public AsgardSQLQuery<T> limit(int limit) throws CloneNotSupportedException {
        this.limit_set = true;
        this.limit = limit;
        return (AsgardSQLQuery<T>) this.clone();
    }

    public AsgardSQLQuery<T> limit(int limit, int offset) throws CloneNotSupportedException {
        this.limit_set = true;
        this.offset_set = true;
        this.limit = limit;
        this.offset = offset;
        return (AsgardSQLQuery<T>) this.clone();
    }

    private ResultSet query(String what) throws SQLException {
        String SQL_STATEMENT = "SELECT " + what + " FROM " + this.model.getClass().getSimpleName() + " WHERE ";
        for (Map.Entry<String, Pair<Boolean, Object>> entry : filters.entrySet()) {
            String column = entry.getKey();
            String value = String.valueOf(entry.getValue().value);
            Boolean eq = entry.getValue().key;
            if (eq) {
                SQL_STATEMENT += column + " = '" + value + "'";
            } else {
                SQL_STATEMENT += "(";
                SQL_STATEMENT += column + " LIKE '" + value + "'";
                SQL_STATEMENT += " OR " + column + " LIKE '%" + value + "'";
                SQL_STATEMENT += " OR " + column + " LIKE '" + value + "%'";
                SQL_STATEMENT += " OR " + column + " LIKE '%" + value + "%'";
                SQL_STATEMENT += ")";
            }
            SQL_STATEMENT += " AND ";
        }
        SQL_STATEMENT += "1";

        if (order_by_set) {
            SQL_STATEMENT += " ORDER BY " + order_by + ((order_by_asc) ? " ASC" : " DESC");
        }

        if (limit_set) {
            if (offset_set) {
                SQL_STATEMENT += " LIMIT " + String.valueOf(offset) + ", " + String.valueOf(limit);
            } else {
                SQL_STATEMENT += " LIMIT " + String.valueOf(limit);
            }
        }

        Statement statement = db.getConn().createStatement();
        return statement.executeQuery(SQL_STATEMENT);
    }

    public int count() throws SQLException {
        ResultSet res = query("COUNT(*)");
        if(res.next()) {
            return res.getInt(1);
        } else {
            return 0;
        }
    }

    public <T extends AsgardSelectable> List<T> get() throws SQLException, IllegalAccessException, InstantiationException {
        ResultSet res = query("*");

        List<T> res_queue = new LinkedList<T>();
        int internal_id = 0;

        while (res.next()) {
            T result = (T) model.getClass().newInstance();
            for (Field field : result.getClass().getFields()) {
                if(!Arrays.asList(model.__ignored).contains(field.getName())) {
                    String field_type = field.getType().getSimpleName();
                    switch (field_type) {
                        case "String":
                            field.set(result, res.getString(field.getName()));
                            break;
                        case "int":
                            field.setInt(result, res.getInt(field.getName()));
                            break;
                        case "long":
                            field.setLong(result, res.getLong(field.getName()));
                            break;
                        case "Boolean":
                            field.setBoolean(result, res.getBoolean(field.getName()));
                            break;
                    }
                }
            }
            result.queue_id(internal_id);
            res_queue.add(result);
            internal_id++;
        }

        return res_queue;
    }
}