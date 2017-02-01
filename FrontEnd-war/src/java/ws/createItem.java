/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import crudItem.crudItemBeanLocal;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.*;
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

/**
 *
 * @author Nick F
 */
@ServerEndpoint(value = "/createItem", configurator = GetHttpSessionConfigurator.class)
public class createItem {
    crudItemBeanLocal crudItemBean = lookupcrudItemBeanLocal();

    @OnMessage
    public String onMessage(String message, Session session, EndpointConfig config) {
        System.out.println("Received: " + message);
        JsonObject jo = Json.createReader(new StringReader(message)).readObject();
        System.out.println(jo.toString());
        String title = (String) jo.getString("title");
        System.out.println("title: " + title);
        float price = (float) jo.getJsonNumber("price").doubleValue();
        System.out.println("price: " + price);
        int seller_id = jo.getInt("seller_id");
        System.out.println("seller: " + seller_id);
        long millis = jo.getJsonNumber("millis").longValue();
        System.out.println("millis: " + millis);
        System.out.println("Calling create item bean..");
        crudItemBean.create(title, price, seller_id, millis);
        return null;
    }

    private crudItemBeanLocal lookupcrudItemBeanLocal() {
        try {
            Context c = new InitialContext();
            return (crudItemBeanLocal) c.lookup("java:global/FrontEnd/FrontEnd-ejb/crudItemBean!crudItem.crudItemBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Apertura sessione, websocket createItem");
    }

    @OnError
    public void onError(Throwable t) {
    }

    @OnClose
    public void onClose(Session session) {
    }
    
}
