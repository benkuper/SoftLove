package fr.insarouen.asi.ihme.tweetanalysis.fb;


import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;
import org.json.*;

public class FacebookConnector {
    public static final String apiKey = "1680288462211946";
    public static final String apiSecret = "5fbec4b9a4ea91e0c842339bacb88a10";
    public static final String REDIRECT_URI = "http://www.leclairobscur.net/soft-love-login/";
    private static final Token EMPTY_TOKEN = null;    
    private static final String PROTECTED_RESOURCE_BASE_URL = "https://graph.facebook.com/";

    private OAuthService fbSrv;
    private Token accessToken = EMPTY_TOKEN;

    public FacebookConnector() {
         this.fbSrv = new ServiceBuilder()
                          .provider(FacebookApi.class)
                          .apiKey(apiKey)
                          .apiSecret(apiSecret)
                          .scope("user_likes,user_birthday,user_location,user_photos,user_posts")
                          .callback(REDIRECT_URI)
                          .build();
    }


    public String getAuthorizationUrl() {
        return this.fbSrv.getAuthorizationUrl(EMPTY_TOKEN);
    }

    public void connect(String authKey) {
        Verifier verifier = new Verifier(authKey);
        this.accessToken = this.fbSrv.getAccessToken(EMPTY_TOKEN, verifier);
    }

    public Response get(String where) {
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_BASE_URL + where);
        this.fbSrv.signRequest(this.accessToken, request);
        return request.send();
        //System.out.println(response.getCode());
        //System.out.println(response.getBody());
    }

    public String getPostLiked(){
        Response response = this.get("v2.5/me/likes");
        if (response.getCode()!=200){
            System.out.println("Erreur dans la requête facebook "+response.getCode());
        }
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        JSONArray arr = obj.getJSONArray("data");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length(); i++)
        {
            response = this.get("v2.5/"+arr.getJSONObject(i).getString("id")+"/posts");
            body = response.getBody();
            obj = new JSONObject(body);
            JSONArray arrPost = obj.getJSONArray("data");
            for (int j = 0; j < arrPost.length(); j++)
            {
                if (arrPost.getJSONObject(j).has("message")){
                    sb.append(arrPost.getJSONObject(j).getString("message"));                        
                }
            }
        }
        return sb.toString();
    }

    public String[] getLikedPosts(){
        Response response = this.get("v2.5/me/likes");
        if (response.getCode()!=200){
            System.out.println("Erreur dans la requête facebook "+response.getCode());
        }
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        JSONArray arr = obj.getJSONArray("data");
        String[] strArray = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++)
        {
            strArray[i] = "";
            response = this.get("v2.5/"+arr.getJSONObject(i).getString("id")+"/posts");
            body = response.getBody();
            obj = new JSONObject(body);
            JSONArray arrPost = obj.getJSONArray("data");
            for (int j = 0; j < arrPost.length(); j++)
            {
                if (arrPost.getJSONObject(j).has("message")){
                    strArray[i] += (arrPost.getJSONObject(j).getString("message"));                        
                }
            }
        }
        return strArray;
    }

    public String getUserprofilePicture(){
        Response response = this.get("v2.5/me/picture?redirect=0&width=9999");
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        JSONObject picture = obj.getJSONObject("data");

        return picture.getString("url");
    }

    // public String getUserAlbumPictures(){
    //     Response response = this.get("v2.5/me/albums");
    //     String body = response.getBody();
    //     JSONObject obj = new JSONObject(body);
    //     JSONArray arr = obj.getJSONArray("data");
    //     StringBuilder sb = new StringBuilder();

    //     for (int i = 0; i < arr.length(); i++)
    //     {
    //         response = this.get("v2.5/"+arr.getJSONObject(i).getString("id")+"/photos");
    //         body = response.getBody();
    //         JSONObject obj2 = new JSONObject(body);
    //         JSONArray arr2 = obj2.getJSONArray("data");

    //         for (int j = 0; j < arr2.length(); j++)
    //         {
    //             response = this.get("v2.5/"+arr2.getJSONObject(i).getString("id"));
    //             body = response.getBody();
    //             return body;
    //         }
    //         return sb.toString();
    //     }
    //     return sb.toString();

    // }
}
