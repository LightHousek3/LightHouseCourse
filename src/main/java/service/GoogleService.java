package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import model.Customer;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import util.ConfigLoader;
import util.PasswordEncrypt;

/**
 * Service for handling Google OAuth authentication.
 */
public class GoogleService {
    
    // Load configuration values from properties file
    private static final String CLIENT_ID = ConfigLoader.getProperty("google.client.id");
    private static final String CLIENT_SECRET = ConfigLoader.getProperty("google.client.secret");
    private static final String REDIRECT_URI = ConfigLoader.getProperty("google.redirect.uri");
    private static final String TOKEN_URL = ConfigLoader.getProperty("google.token.url", "https://accounts.google.com/o/oauth2/token");
    private static final String USER_INFO_URL = ConfigLoader.getProperty("google.userinfo.url", "https://www.googleapis.com/oauth2/v1/userinfo?access_token=");
    private static final String GRANT_TYPE = "authorization_code";
    
    public static String getCLIENT_ID() {
        return CLIENT_ID;
    }

    public static String getREDIRECT_URI() {
        return REDIRECT_URI;
    }

    public String getToken(String code) throws IOException {
        // Send a POST request to get the OAuth2 token from Google
        String response = Request.Post(TOKEN_URL)
                .bodyForm(
                        Form.form()
                                .add("client_id", CLIENT_ID)
                                .add("client_secret", CLIENT_SECRET)
                                .add("redirect_uri", REDIRECT_URI)
                                .add("code", code)
                                .add("grant_type", GRANT_TYPE)
                                .build()
                ) 
                // Execute request and receive response content as String
                .execute().returnContent().asString(); 

        // Parse JSON from Google response
        JsonObject jobj = new Gson().fromJson(response, JsonObject.class);
        // Get access_token from JSON and remove quotes
        String accessToken = jobj.get("access_token").toString().replaceAll("\"", "");

        return accessToken;
    }

    public Customer getInfoUser(String accessToken) throws IOException {
        // Create a path to query user information from Google API
        String link = USER_INFO_URL + accessToken;
        
        // Send a GET request to get user information
        String response = Request.Get(link).execute().returnContent().asString();
        
        // Parse the JSON response
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        
        // Extract data from Google response
        Customer customer = new Customer();
        
        // Map Google data to Customer properties
        if (jsonObject.has("id")) {
            customer.setAuthProviderId(jsonObject.get("id").getAsString());
        }
        
        if (jsonObject.has("email")) {
            customer.setEmail(jsonObject.get("email").getAsString());
        }
        
        if (jsonObject.has("name")) {
            customer.setFullName(jsonObject.get("name").getAsString());
        } else {
            // If no name is provided, try to combine given_name and family_name
            String fullName = "";
            if (jsonObject.has("given_name")) {
                fullName += jsonObject.get("given_name").getAsString();
            }
            if (jsonObject.has("family_name")) {
                if (!fullName.isEmpty()) fullName += " ";
                fullName += jsonObject.get("family_name").getAsString();
            }
            if (!fullName.isEmpty()) {
                customer.setFullName(fullName);
            }
        }
        
        // Generate a username from the email if available, otherwise from name
        String username = customer.getEmail() != null ? 
            customer.getEmail().split("@")[0] : 
            (customer.getFullName() != null ? 
                customer.getFullName().toLowerCase().replaceAll("\\s+", "") : 
                "google_user_" + System.currentTimeMillis());
        customer.setUsername(username);
        
        // Set a random password - won't be used for authentication but needed in DB
        customer.setPassword(PasswordEncrypt.encryptSHA256(customer.getAuthProviderId() + System.currentTimeMillis()));
        
        // Set profile picture if available
        if (jsonObject.has("picture")) {
            customer.setAvatar(jsonObject.get("picture").getAsString());
        }
        
        return customer;
    }    
} 