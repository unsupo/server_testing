package examples;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jarndt on 1/3/17.
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private final String CLIENT_ID = "628531512920-arim9k90e5j7bf4aomg8rb56dr0cp9vt.apps.googleusercontent.com";
    private final String ACCESS_TOKEN = "622129601327484|SR8Fs6oT4wV5TayZopEQ9IP4tWY"; //facebook

    private static final String DEFAULT = "default", GOOGLE = "google", FACEBOOK = "facebook";

    @CrossOrigin//(origins = "http://localhost:8080")
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="type", defaultValue = "default") String type,
                             @RequestParam(value="name") String name) throws SQLException, ClassNotFoundException, GeneralSecurityException, IOException {
//        System.out.println(name+"\t"+type);
        String result = checkDatabase(type,name);
        try {
            if(type.equalsIgnoreCase(GOOGLE) && googleVerifier(name))
                return new Greeting(counter.incrementAndGet(), "SUCCESS!",result);
            if(type.equalsIgnoreCase(FACEBOOK) && facebookVerifier(name))
                return new Greeting(counter.incrementAndGet(), "SUCCESS!",result);
            if(type.equalsIgnoreCase(DEFAULT) && verifier(name))
                return new Greeting(counter.incrementAndGet(), "SUCCESS!",result);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Greeting(counter.incrementAndGet(), "FAILED",result);
    }

    private String checkDatabase(String type, String name) throws SQLException, ClassNotFoundException, GeneralSecurityException, IOException {
        String tableName = "testing";
        List<HashMap<String,Object>> result = null;
        Object data = name;
        try {
            String query = "select * from "+ tableName+" where ";
            if(type.equalsIgnoreCase(GOOGLE))
                result = Database.query(query+"email = '"+((Payload)(data = getGooglePayload(name))).getEmail()+"'");
            else if(type.equalsIgnoreCase(FACEBOOK))
                result = Database.query(query+"email = '"+((User)(data = getFacebookUserData(name))).getEmail()+"'");
            else
                result = Database.query(query+"email = '"+name.split("\\|")[0]+" and password = '"+name.split("\\|")[1]);
        } catch (Exception e) {
            if(e.getMessage().contains("relation \""+tableName+"\" does not exist")) {
                DataBaseInit.createTable(tableName); //table doesn't exist, so create table and try again
                return checkDatabase(type,name);
            }
            throw e;
        }

        if(result == null || result.size() == 0){
            //Insert query because record doesn't exist
            if(type.equalsIgnoreCase(GOOGLE)) {
                Payload payload = (Payload) data;
                String id = payload.getUserId();
                String email = payload.getEmail();
                Boolean verified = payload.getEmailVerified();
                String pictureUrl = (String) payload.get("picture");
                String locale = (String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");
                Database.query(String.format("INSERT INTO " + tableName + "" +
                        "(key,email,emailvarified,firstname,lastname,pictureurl,locale,userid)" +
                        "values(%s,%s,%s,%s,%s,%s,%s,%s)",
                        name,email,verified,givenName,familyName,pictureUrl,locale,id));
            }else if(type.equalsIgnoreCase(FACEBOOK)){
                User user = (User) data;
                String email = user.getEmail();
//                user.getAbout();
//                user.getAgeRange();
//                user.getBio();
//                user.getBirthday();
//                user.getBirthdayAsDate();
//                user.getCover();
//                user.getCurrency();
                String firstname = user.getFirstName();
                String lastname = user.getLastName();
//                user.getGender();
//                user.getLocale();
//                user.getPicture().getUrl();
                Boolean verified = user.getVerified();
                String id = user.getId();

                Database.query(String.format("INSERT INTO " + tableName +""+
                        "(key,email,emailvarified,firstname,lastname,userid)" +
                        "values(%s,%s,%s,%s,%s,%s,%s)",
                        name,email,verified,firstname,lastname,id));
            }else{
                String[] nameV = name.split("\\|");
                String email = nameV[0];
                String pass = nameV[1];
                Boolean verified = false;
                Database.query(String.format("INSERT INTO " + tableName +""+
                        "(key,email,emailvarified,password)" +
                        "values(%s,%s,%s,%s)", name,email,verified,pass));
            }
            return "NEW_USER";
        }

        return "RETURNING_USER";
    }

    private boolean verifier(String name) {
        return false;
    }
    private DefaultFacebookClient getFacebookClient(String token){
        return new DefaultFacebookClient(ACCESS_TOKEN);
    }
    private FacebookClient.DebugTokenInfo getClient(String token){
        DefaultFacebookClient facebookClient = new DefaultFacebookClient(ACCESS_TOKEN);
        return new DefaultFacebookClient(ACCESS_TOKEN).debugToken(token);
    }
    private boolean facebookVerifier(String token) {
        return getClient(token).isValid();
    }
    private User getFacebookUserData(String token){
        FacebookClient fbClient = new DefaultFacebookClient(token, Version.VERSION_2_8);
        return fbClient.fetchObject("me", User.class, Parameter.with("fields", "name,email,birthday,isVerified,languages,location,sports"));
    }

    public GoogleIdToken getGoogleToken(String idTokenString) throws GeneralSecurityException, IOException {
        JsonFactory jsonFactory = new GsonFactory();
        NetHttpTransport transport = new NetHttpTransport();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

// (Receive idTokenString by HTTPS POST)
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
        }catch (IllegalArgumentException e){
//            System.out.println("INVALID VALIDATION");
//            e.printStackTrace();
            return null;
        }
        return idToken;
    }


//            // Print user identifier
//            String userId = payload.getSubject();
////            System.out.println("User ID: " + userId);
//
//            // Get profile information from payload
//            String email = payload.getEmail();
//            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
//            String familyName = (String) payload.get("family_name");
//            String givenName = (String) payload.get("given_name");

    // Use or store profile information
    // ...
    public Payload getGooglePayload(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = getGoogleToken(idTokenString);
        if (idToken != null)
            return idToken.getPayload();
        return null;
    }

    public boolean googleVerifier(String idTokenString) throws GeneralSecurityException, IOException {
        return getGoogleToken(idTokenString) != null;
    }
}
