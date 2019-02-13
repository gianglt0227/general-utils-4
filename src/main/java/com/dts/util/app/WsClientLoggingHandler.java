/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.app;

import java.io.ByteArrayOutputStream;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author GiangLT
 */
public class WsClientLoggingHandler implements SOAPHandler<SOAPMessageContext> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void close(MessageContext arg0) {
    }

    @Override
    public boolean handleFault(SOAPMessageContext arg0) {
        SOAPMessage message = arg0.getMessage();
        logger.error("[CLIENT] Fault handled: {}", message);
        return true;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext arg0) {
        try {
            SOAPMessage message = arg0.getMessage();

//            Source source = message.getSOAPPart().getContent();
//            Transformer transformer = TransformerFactory.newInstance().newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//            
//            StringWriter writer = new StringWriter();
//            transformer.transform(source, new StreamResult(writer));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.writeTo(baos);
            boolean isOutboundMessage = (Boolean) arg0.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (isOutboundMessage) {
                try {
                    logger.info("[CLIENT] >>>>> Sent SOAP Request: {}", baos);
                } catch (Exception ex) {
                    logger.error("", ex);
                }

            } else {
                try {
                    logger.info("[CLIENT] <<<<< Received SOAP Response: {}", baos);
                } catch (Exception ex) {
                    logger.error("", ex);
                }
            }

        } catch (Exception ex) {
            logger.error("", ex);
        }
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

}
