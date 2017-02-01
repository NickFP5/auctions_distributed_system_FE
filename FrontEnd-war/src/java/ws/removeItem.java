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
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Nick F
 */
@ServerEndpoint(value="/removeItem", configurator = GetHttpSessionConfigurator.class)
public class removeItem {
    crudItemBeanLocal crudItemBean = lookupcrudItemBeanLocal();

    @OnMessage
    public String onMessage(String message) {
        JsonObject jo = Json.createReader(new StringReader(message)).readObject();
        int id = jo.getInt("id");
        crudItemBean.remove(id);
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

    @OnClose
    public void onClose(Session session) {
    }

    @OnOpen
    public void onOpen(Session session) {
    }
    
}
