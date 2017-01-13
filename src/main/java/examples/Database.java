package examples;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jarndt on 1/12/17.
 */
public class Database {
//    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        System.out.println(query("select * from users where id = 3"));
//    }


    public static final String  DRIVER      = "org.postgresql.Driver",
                                CONNECTION  = "jdbc:postgresql://localhost:5432/jarndt",
                                USERNAME    = "jarndt",
                                PASSWORD    = "postgres";

    private Connection connection;
    private static Database instance;

    private Database() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        connection = DriverManager.getConnection(CONNECTION,USERNAME,PASSWORD);
    }

    private static Database getInstance() throws SQLException, ClassNotFoundException {
        if(instance == null)
            instance = new Database();
        return instance;
    }

    public static List<HashMap<String,Object>> query(String query) throws SQLException, ClassNotFoundException {
        Database d = getInstance();
        List<HashMap<String,Object>> result = new ArrayList<>();
        Statement stmt = d.connection.createStatement();
        if(containsString(query.toLowerCase(),new String[]{"insert","update","delete","create"})) {
            stmt.executeUpdate(query);
            return null;
        }
        ResultSet rs = stmt.executeQuery( query );
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        while ( rs.next() ) {
            HashMap h = new HashMap();
            for(int i = 1; i<columnCount; i++)
                h.put(metadata.getColumnName(i),rs.getObject(i));
            result.add(h);
        }
        rs.close();
        stmt.close();
        stmt.close();
        return result;
    }

    private static boolean containsString(String query, String[] s) {
        for(String ss : s)
            if(query.contains(ss))
                return true;
        return false;
    }
}
