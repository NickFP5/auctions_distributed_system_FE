/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.json.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import login.loginBeanLocal;
import resources.User;

/**
 *
 * @author Nick F
 */
@ServerEndpoint(value="/login", 
                configurator = GetHttpSessionConfigurator.class)
public class login {
    
    loginBeanLocal loginBean = lookuploginBeanLocal();
    
    
    
    
    
    
    @OnMessage
    public String onMessage(String message, Session session, EndpointConfig config) {
        
        int user_id;
        
        System.out.println("Ricevuto: " + message);
        
        JsonObject jo = Json.createReader(new StringReader(message)).readObject();
        String email = (String) jo.getString("email");
        String password = (String) jo.getString("password");
        
        HttpSession hs;
        
        User u = new User();
        u.setEmail(email);
        u.setPassword(password);
        user_id = loginBean.check(u);
        
        /*
        if( session.isOpen() && registered ){
            session.getUserProperties().put("email", email);
            hs = (HttpSession) config.getUserProperties()
                                           .get(HttpSession.class.getName());
            //System.out.println("VALORE creazione time HTTPSESSION--> " + hs.getCreationTime());
            if(hs!= null) {
                hs.setAttribute("email", email);
                System.out.println("Http session creata!!!!..");
            }
            else System.out.println("Http session non trovata..");
        }
        
        /*UsersFacade uf = new UsersFacade();
        Users u = new Users();
        
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory( "Eclipselink_JPA" );
      
      EntityManager entitymanager = emfactory.createEntityManager( );
      entitymanager.getTransaction( ).begin( );
      u = entitymanager.;*/
        
        if(user_id > 0) return String.valueOf(user_id);
        else return "ERROR(";
    }
    
    @OnOpen
    public void onOpen(Session session) {
        
        System.out.println(session.getId() + " ha avviato sessione [kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk]");
    }

    @OnError
    public void onError(Throwable t) {
    }

    @OnClose
    public void onClose(Session session) {
        
        System.out.println(session.getId() + " ha chiuso la sessione");
    }

    private loginBeanLocal lookuploginBeanLocal() {
        try {
            Context c = new InitialContext();
            return (loginBeanLocal) c.lookup("java:global/FrontEnd/FrontEnd-ejb/loginBean!login.loginBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    
}
