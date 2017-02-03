/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import registration.registrationBeanLocal;
import resources.User;

/**
 *
 * @author alessandrotorcetta
 */
@ServerEndpoint("/register")
public class register {

    registrationBeanLocal registrationBean = lookupregistrationBeanLocal();
    
    
    

  @OnMessage
    public void onMessage(String message, Session session, EndpointConfig config) {
        
       // boolean registered = false;
        
        System.out.println("Ricevuto: " + message);
        
        JsonObject jo = Json.createReader(new StringReader(message)).readObject();
        String email = (String) jo.getString("email");
        String name = (String) jo.getString("name");
        String password = (String) jo.getString("password");
        
        
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setPassword(password);
        System.out.println("Ricevuto--->:" + u.getEmail());
        registrationBean.register(u);
        System.out.println("Esco da onMessage");
        
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
      u = entitymanager.;
        
        if(registered) return "OK";
        else return "ERROR";
        */
    }
    
    @OnOpen
    public void onOpen(Session session) {
        
        System.out.println(session.getId() + " ha avviato sessione [fhgfkjhfhjfjhfhj]");
    }

    @OnError
    public void onError(Throwable t) {
    }

    @OnClose
    public void onClose(Session session) {
        
        System.out.println(session.getId() + " ha chiuso la sessione");
    }
    
    
    
    
    private registrationBeanLocal lookupregistrationBeanLocal() {
        try {
            Context c = new InitialContext();
            return (registrationBeanLocal) c.lookup("java:global/FrontEnd/FrontEnd-ejb/registrationBean!registration.registrationBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
