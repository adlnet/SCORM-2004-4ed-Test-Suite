package org.adl.testsuite.rte.lms.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.adl.testsuite.rte.lms.comm.LMSMessageObject;
import org.adl.testsuite.rte.lms.interfaces.MessageManager;

/**
 * This class contains the socket for the LMS test communication 
 * between the test and the SCO in the LMS. 
 * 
 * @author ADL Technical Team
 */
public class LMSSocketThread extends Thread
{
   /**
    * Initial port number
    */
   public static final int PORT = 8686;
   /**
    * The port number.
    */
   protected int mPort = PORT;
   
   /**
    * Signals if the thread is listening for incoming messages.
    */
   protected boolean mIsListening = false;
   
   /**
    * The socket.
    */
   protected Socket mSocket;
   
   /**
    * The output stream.
    */
   protected ObjectInputStream mOIS;
   
   /**
    * The Input stream.
    */
   protected ObjectOutputStream mOOS;
   
   /**
    * The message manager.
    */
   protected MessageManager mMessageManager;
   
   /**
    * Constructor.
    * @param iMM message manager
    */
   public LMSSocketThread(MessageManager iMM)
   {
      mMessageManager = iMM;
      mMessageManager.registerSocketThread(this);
   }
   
   /**
    * Listens for incoming messages.
    * @throws IOException on an error
    * @throws ClassNotFoundException on an error
    */
   public void listen() throws IOException, ClassNotFoundException
   {
      mIsListening = true;
      Object o;
      while ((o = mOIS.readObject()) != null)
      {
         mMessageManager.receiveMessage((LMSMessageObject)o);
      }
      write(null);
      mIsListening = false;
   }
   
   /**
    * Writes an object across the socket stream.
    * 
    * @param iO The object to be written across the stream.
    */
   public void write(Object iO)
   {
      while (!mIsListening)
      {
         // you're not ready to send until you're ready
         // to listen
         sleep();
      }
      
      try
      {
         if( ! mSocket.isOutputShutdown() )
         {
            mOOS.writeObject(iO);
            mOOS.flush();
         }
      }
      catch (IOException ioe)
      {
         ioe.printStackTrace();
      }
   }
   
   /**
    * Closes the thread.
    * 
    * @throws IOException on error.
    */
   public void close() throws IOException
   {
      while (mIsListening)
      {
         sleep();
      }
      
      if (mOOS != null)
      {
         mOOS.flush();
         mOOS.close();
      }
      
      if (mOIS != null)
      {
         mOIS.close();
      }
      
      if (mSocket != null)
      {   
         mSocket.close();
      }
   }
   
   /**
    * Set the port
    * @param iPort the port
    */
   public void setPort(int iPort)
   {
      mPort = iPort;
   }
   
   /**
    * Get the port
    * @return the port
    */
   public int getPort()
   {
      return mPort;
   }
   
   /**
    * forces the thread to take a break
    */
   public void sleep()
   {
      try
      {
         Thread.sleep(1);
      }
      catch (InterruptedException ie)
      {
         //
      }
   }
}
