package fr.insarouen.asi.ihme.tweetanalysis.web;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import fr.insarouen.asi.ihme.tweetanalysis.*;
public class FacebookServlet extends HttpServlet {
   
    public void init(){
    }

  public void doGet(HttpServletRequest requete, HttpServletResponse reponse) throws ServletException, IOException {
    String code = requete.getParameter("code");
    Application appli = new Application();
    appli.doTraitement(code);
  }
}