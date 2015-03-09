package org.adl.testsuite.rte.lms.comm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.adl.testsuite.rte.lms.interfaces.MessageManager;
import org.adl.testsuite.rte.lms.util.LMSSocketThread;

/**
 * Implementation of a client socket.
 */
public class LMSSocketImpl
{
   /**
    * The address.
    */
   private transient InetAddress mInetAddressStr;
   
   /**
    * The client socket thread.
    */
   private final transient Client mClient;
   
   /**
    * Default constructor. Uses the default values for the address of the server
    * and the port number.<br>
    * Address: InetAddress.getLocalHost()<br>
    * Port: 8686
    * 
    * @param iMM the message manager
    */
   public LMSSocketImpl(final MessageManager iMM)
   {
      // use the default values
      try
      {
         mInetAddressStr = InetAddress.getLocalHost();
      }
      catch ( UnknownHostException e )
      {
         e.printStackTrace();
      }
      mClient = new Client(iMM);
   }
   
   /**
    * Constructor used to initialize the port number to something other than the
    * default value. The default server address will still be used.<br>
    * Address: InetAddress.getLocalHost()
    * 
    * @param iMM The message manager.
    * @param iPortNumber The port number to use.
    */
   public LMSSocketImpl(final MessageManager iMM, final int iPortNumber)
   {
      this(iMM);
      setPort(iPortNumber);
   }
   
   /**
    * Calls the client thread's <code>start()</code> method.
    */
   public void start()
   {
      mClient.start();
   }
   
   /**
    * Gets the address of the socket.
    * @return the address, or a message if null.
    */
   public String getAddress()
   {
      return (mInetAddressStr != null) ? mInetAddressStr.toString()
                                     : "mInetAddressStr not initialized yet";
   }

   /**
    * Gets the port number.
    * @return Returns the port number.
    */
   public int getPortNumber()
   {
      return mClient.getPort();
   }

   /**
    * Sets the port.
    * @param iPort the port.
    */
   public final void setPort(final int iPort)
   {
      mClient.setPort(iPort);
   }
   
   /**
    * Sends an object to the server.
    * @param iO the object to send.
    */
   public void sendObject(final LMSMessageObject object)
   {
      mClient.write(object);
   }
   
   /**
    * Closes the socket and streams.
    */
   public void close()
   {
      try
      {
         mClient.close();
      }
      catch (IOException ioe)
      {
         System.err.println("LSI::squashSocket--Error closing I/O");
         ioe.printStackTrace();
      }
   }
   
   /**
    * Client thread. It is a thread.
    */
   private class Client extends LMSSocketThread
   {
      /**
       * Constructor. Takes a message manager.
       * @param iMM a message manager
       */
      public Client(final MessageManager iMM)
      {
         super(iMM);
      }
      
      /**
       * Run.
       */
      public void run()
      {
         try
         {
            mSocket = new Socket(mInetAddressStr, mPort);
            mOOS = new ObjectOutputStream(mSocket.getOutputStream());
            mOIS = new ObjectInputStream(mSocket.getInputStream());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         
         try
         {
            listen();
         }
         catch (IOException e)
         {
            //System.out.println("LSI SOCKET PROBLEM");
         }
         catch (ClassNotFoundException e)
         {
            //System.out.println("LSI SOCKET PROBLEM");
         }
      }
   }
}
