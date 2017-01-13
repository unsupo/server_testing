package examples;

import java.sql.SQLException;

/**
 * Created by jarndt on 1/12/17.
 */
public class DataBaseInit {
//    public static void main(String[] args) {
//        String accessToken = "EAAI10tXkMXwBAMMyFwgmFFUyuMv43WR1V5IQieRZC6R8dUDYck9qC3K9HfEHG8tg0Ba8ZBV3WZBSqqECCAkLG5J36KMKv7CMMXKvpm3TJ3P0Exue49vojh9VHJzuIpjUwnwRIea50ZCNQm4hwPfa6Mhe2hYMpbBkrIjpOrohPwZDZD";
//        FacebookClient fbClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_8);
//        User me = fbClient.fetchObject("me", User.class, Parameter.with("fields", "name,email,birthday,verified,languages,location,sports"));
//
//        System.out.println(me);
//    }

    public static void createTable(String tableName) throws SQLException, ClassNotFoundException {
        Database.query("CREATE TABLE "+tableName+"(" +
                "   ID serial primary key," +
                "   key text primary key," +
                "   email varchar(250) primary key," +
                "   emailverified CHAR(1)," +
                "   firstname varchar(250)," +
                "   lastname varchar(250)," +
                "   username varchar(250)," +
                "   password varchar(250)," +
                "   birthday," +
                "   pictureUrl text," +
                "   locale varchar(100)," +
                "   familyName  text," +
                "   givenName   text," +
                "   userID  text," +
                "   application text," +
                "   applicationID text," +
                "   gender char(1)" +
                ");");
    }
}