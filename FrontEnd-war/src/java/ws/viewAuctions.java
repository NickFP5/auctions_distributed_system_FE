/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import viewAuctions.viewAuctionsBeanLocal;

/**
 *
 * @author Nick F
 */
@ServerEndpoint(value="/viewAuctions", configurator = GetHttpSessionConfigurator.class)
public class viewAuctions {
    viewAuctionsBeanLocal viewAuctionsBean = lookupviewAuctionsBeanLocal();

    
    
    @OnMessage
    public String onMessage(String message, Session session, EndpointConfig config) {
        return viewAuctionsBean.viewAuctions();
    }

    @OnClose
    public void onClose(Session session) {
    }

    @OnOpen
    public void onOpen(Session session) {
        try {
            session.getBasicRemote().sendText(viewAuctionsBean.viewAuctions());
        } catch (IOException ex) {
            Logger.getLogger(viewAuctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @OnError
    public void onError(Throwable t) {
    }

    private viewAuctionsBeanLocal lookupviewAuctionsBeanLocal() {
        try {
            Context c = new InitialContext();
            return (viewAuctionsBeanLocal) c.lookup("java:global/FrontEnd/FrontEnd-ejb/viewAuctionsBean!viewAuctions.viewAuctionsBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
