package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import model.Customer;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import util.ConfigLoader;
import util.PasswordEncrypt;

/**
 * Service for handling Facebook OAuth authentication.
 */
public class FacebookService {
    
    // Load configuration values from properties file
    private static final String FACEBOOK_CLIENT_ID = ConfigLoader.getProperty("facebook.client.id");
    private static final String FACEBOOK_CLIENT_SECRET = ConfigLoader.getProperty("facebook.client.secret");
    private static final String FACEBOOK_REDIRECT_URI = ConfigLoader.getProperty("facebook.redirect.uri");
    private static final String FACEBOOK_LINK_GET_TOKEN = ConfigLoader.getProperty("facebook.token.url", "https://graph.facebook.com/v19.0/oauth/access_token");
    private static final String FACEBOOK_LINK_GET_USER_INFO = ConfigLoader.getProperty("facebook.userinfo.url", "https://graph.facebook.com/me?fields=id,name,email,picture.width(200).height(200)&access_token=");

    public static String getFACEBOOK_CLIENT_ID() {
        return FACEBOOK_CLIENT_ID;
    }

    public static String getFACEBOOK_REDIRECT_URI() {
        return FACEBOOK_REDIRECT_URI;
    }
    
    public String getToken(String code) throws ClientProtocolException, IOException {
        String response = Request.Post(FACEBOOK_LINK_GET_TOKEN)
                .bodyForm(
                        Form.form()
                                .add("client_id", FACEBOOK_CLIENT_ID)
                                .add("client_secret", FACEBOOK_CLIENT_SECRET)
                                .add("redirect_uri", FACEBOOK_REDIRECT_URI)
                                .add("code", code)
                                .build()
                )
                .execute().returnContent().asString();

        JsonObject jobj = new Gson().fromJson(response, JsonObject.class);

        String accessToken = jobj.get("access_token").toString().replaceAll("\"", "");
        return accessToken;
    }

    public Customer getUserInfo(final String accessToken) throws ClientProtocolException, IOException {
        String link = FACEBOOK_LINK_GET_USER_INFO + accessToken;
        String response = Request.Get(link).execute().returnContent().asString();
        
        // Parse the JSON response
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        
        // Extract data from Facebook response
        Customer customer = new Customer();
        
        // Map Facebook data to Customer properties
        if (jsonObject.has("id")) {
            customer.setAuthProviderId(jsonObject.get("id").getAsString());
        }
        
        if (jsonObject.has("email")) {
            customer.setEmail(jsonObject.get("email").getAsString());
        }
        
        if (jsonObject.has("name")) {
            customer.setFullName(jsonObject.get("name").getAsString());
            // Generate a username from the email if available, otherwise from name
            String username = customer.getEmail() != null ? 
                customer.getEmail().split("@")[0] : 
                customer.getFullName().toLowerCase().replaceAll("\\s+", "");
            customer.setUsername(username);
        }
        
        // Set a random password - won't be used for authentication but needed in DB
        customer.setPassword(PasswordEncrypt.encryptSHA256(customer.getAuthProviderId() + System.currentTimeMillis()));
        
        // Set profile picture if available
        if (jsonObject.has("picture") && jsonObject.get("picture").isJsonObject()) {
            JsonObject pictureObj = jsonObject.get("picture").getAsJsonObject();
            if (pictureObj.has("data") && pictureObj.get("data").isJsonObject()) {
                JsonObject dataObj = pictureObj.get("data").getAsJsonObject();
                if (dataObj.has("url")) {
                    customer.setAvatar(dataObj.get("url").getAsString());
                }
            }
        }
        
        return customer;
    }
} 