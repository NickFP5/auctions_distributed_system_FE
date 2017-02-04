/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservTotalOrder;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import model.TotalOrderMessageType;
import model.TotalOrderMulticastMessage;
import totalOrderReplicazione.totalOrderMulticastSender;

/**
 *
 * @author alessandrotorcetta
 */
@WebService(serviceName = "prTsWebService")
public class prTsWebService {

    
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "proposedTs")
    public void proposed(@WebParam(name = "proposedTs") String proposedTs) {
        JsonObject jo = Json.createReader(new StringReader(proposedTs)).readObject();
        TotalOrderMulticastMessage msg = new TotalOrderMulticastMessage();
        msg.setMessageId(jo.getInt("messageId"));
        msg.setTotalOrderSequence(jo.getInt("totalOrderSequence"));
	msg.setMessageType(TotalOrderMessageType.PROPOSAL);
	msg.setGroupId(jo.getInt("groipId"));
	msg.setSource(jo.getInt("source"));
        msg.setSequence(jo.getInt("sequence"));
        msg.setContent(jo.getString("content"));

        ///------->
        
        totalOrderMulticastSender.getInstance().delivery(msg);
    }
}
