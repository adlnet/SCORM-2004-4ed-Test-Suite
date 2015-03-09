package org.adl.testsuite.rte.lms.interfaces;

import org.adl.testsuite.rte.lms.comm.LMSMessageObject;
import org.adl.testsuite.rte.lms.util.LMSSocketThread;

/**
 * This interface defines the methods used to
 * handle messages to and from a socket. This 
 * interface assumes that there is a thread 
 * that contains the socket and functionality 
 * to read and write objects via that socket.
 * 
 * @author ADL Technical Team
 */
public interface MessageManager
{
   /**
    * Gets a message from the socket.
    * 
    * @param iMessage The object sent from the socket.
    */
   void receiveMessage(LMSMessageObject iMessage);
   
   /**
    * Sends a message to the socket.
    * 
    * @param iMessage The object to send to the socket.
    */
   void sendMessage(LMSMessageObject iMessage);
   
   /**
    * Registers the Thread containing the socket 
    * to provide the ability to send and receive 
    * messages from the socket.
    * 
    * @param iThread The thread containing the socket
    */
   void registerSocketThread(LMSSocketThread iThread);
}
