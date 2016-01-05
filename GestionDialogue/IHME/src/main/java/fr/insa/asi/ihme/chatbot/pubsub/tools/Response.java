package fr.insa.asi.ihme.chatbot.pubsub.tools;

public class Response {
    private String response;

    public String getResponseString() {
        return response;
    }

    public void setResponseString(String response) {
        this.response = response;
    }

    public Response(String response){
        this.setResponseString(response);
    }

}
