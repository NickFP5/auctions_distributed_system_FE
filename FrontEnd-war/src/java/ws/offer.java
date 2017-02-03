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
import javax.json.Json;
import javax.json.JsonObject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import offer.offerBeanLocal;
import offerLiveWatch.offerLiveWatch;

/**
 *
 * @author alessandrotorcetta
 */
@ServerEndpoint("/offer/{item_id}")
public class offer {

    offerBeanLocal offerBean = lookupofferBeanLocal();

    @OnMessage
    public String onMessage(String message) {

        System.out.println("Dentro onMessage offer websocket FRONTEND");
        System.out.println("MSG RICEVUTO-->" + message);
        JsonObject jo = Json.createReader(new StringReader(message)).readObject();

        String item_id_s =  jo.getString("item_id");
        int item_id = Integer.parseInt(item_id_s);
        float requestedPrice = (float) jo.getInt("requestedPrice");
        String user_id_s = jo.getString("user_id");
        int user_id = Integer.parseInt(user_id_s);
        //RICHIAMARE BEAN PER INOLTRARE LA RICHIESTA AL BACKEND
        System.out.println("Sto inviando item_id-->" + item_id + " reqPrice -->" + requestedPrice + " user_id--->" + user_id);
        //offerBean.offerPrice(item_id, requestedPrice, user_id);

        return offerBean.offerPrice(item_id, requestedPrice, user_id);
    }

    @OnClose
    public void onClose(Session session) {
        
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("item_id") String item_id) {
        System.out.println(session.getId() + " ha avviato sessione offer ws");
        offerLiveWatch olw = new offerLiveWatch(session, Integer.parseInt(item_id));
        Thread t = new Thread(olw);
        t.start();
        
    }

    private offerBeanLocal lookupofferBeanLocal() {
        try {
            Context c = new InitialContext();
            return (offerBeanLocal) c.lookup("java:global/FrontEnd/FrontEnd-ejb/offerBean!offer.offerBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
