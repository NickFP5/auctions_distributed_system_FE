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
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import payment.paymentBeanLocal;

/**
 *
 * @author alessandrotorcetta
 */
@ServerEndpoint("/payment")
public class payment {

    paymentBeanLocal paymentBean = lookuppaymentBeanLocal();

    
    
    @OnMessage
    public void onMessage(String message, Session session, EndpointConfig config) {
        
       // boolean registered = false;
        
        System.out.println("Ricevuto: " + message);
        
        JsonObject jo = Json.createReader(new StringReader(message)).readObject();
        int user_id = (int) jo.getInt("user_id");
        int item_id = (int) jo.getInt("item_id");
        
        System.out.println("FE payment WEB SOCKET, uso  user id-->" + user_id +" e item id--> " + item_id);
        
        paymentBean.paga(user_id, item_id);
        
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

    private paymentBeanLocal lookuppaymentBeanLocal() {
        try {
            Context c = new InitialContext();
            return (paymentBeanLocal) c.lookup("java:global/FrontEnd/FrontEnd-ejb/paymentBean!payment.paymentBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
