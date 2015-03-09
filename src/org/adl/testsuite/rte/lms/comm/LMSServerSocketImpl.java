package org.adl.testsuite.rte.lms.comm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.adl.testsuite.rte.lms.interfaces.MessageManager;
import org.adl.testsuite.rte.lms.util.LMSSocketThread;

/**
 * This is the socket server. It is responsible for listening for Socket
 * connections and then spinning them off to a thread to handle the
 * communication.
 * 
 * @author ADL Technical Team
 */
public class LMSServerSocketImpl
{
   /**
    * Signals if the Server Socket should be listening for incoming connections.
    */
   protected transient boolean mListening = true;

   /**
    * The server socket thread.
    */
   protected final transient Server mServer;

   /**
    * Constructor. Accepts a message manager.
    * 
    * @param iMM
    *           the message manager
    */
   public LMSServerSocketImpl(final MessageManager iMM)
   {
      mServer = new Server(iMM);
   }

   /**
    * Constructor. Accepts message manager and port.
    * 
    * @param iMM
    *           the message manager
    * @param iPort
    *           the port
    */
   public LMSServerSocketImpl(final MessageManager iMM, final int iPort)
   {
      this(iMM);
      setPort(iPort);
   }

   /**
    * Calls the server thread's <code>start()</code> method.
    */
   public void startServer()
   {
      mServer.start();
   }

   /**
    * Stops the server. This will automatically close the server socket.
    */
   public void stopServer()
   {
      mListening = false;
   }

   /**
    * Closes the server socket.
    */
   public void close()
   {
      stopServer();
      try
      {
         mServer.close();
      }
      catch ( IOException ioe )
      {
         System.err.println("Error closing I/O");
         ioe.printStackTrace();
      }
   }

   /**
    * Send an object to the connected client.
    * 
    * @param iO
    *           the object to send.
    */
   public void sendObject(final Object iO)
   {
      mServer.write(iO);
   }

   /**
    * Gets the port.
    * 
    * @return the port number
    */
   public int getPortNumber()
   {
      return mServer.getPort();
   }

   /**
    * Gets the server socket address
    * 
    * @return the server socket address
    */
   public String getAddress()
   {
      return mServer.getAddress();
   }

   /**
    * Sets the port.
    * 
    * @param iPort
    *           the port
    */
   public final void setPort(final int iPort)
   {
      mServer.setPort(iPort);
   }

   /**
    * The server thread.
    */
   private class Server extends LMSSocketThread
   {
      /**
       * Signals that the server has not started.
       */
      public static final int NOT_STARTED = 0;

      /**
       * Signals that the server is in the process of starting.
       */
      public static final int STARTING = 1;

      /**
       * Signals that the server has started.
       */
      public static final int STARTED = 2;

      /**
       * Signals that there was an error in the starting of the server.
       */
      public static final int ERROR = 3;

      /**
       * The server socket.
       */
      private transient ServerSocket mServerSocket;

      /**
       * The current state of the server.
       */
      private transient int mState = NOT_STARTED;

      /**
       * Constructor.
       * 
       * @param iMM
       *           the message manager
       */
      public Server(final MessageManager iMM)
      {
         super(iMM);
      }

      /**
       * Initializes the server socket.
       * 
       * @return true if successful, false if not.
       */
      private boolean startServerSocket()
      {
         mState = STARTING;
         try
         {
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(mPort));
         }
         catch ( IOException ioe )
         {
            mState = ERROR;
            ioe.printStackTrace();
            return false;
         }

         mState = STARTED;
         return true;
      }

      /**
       * The thread's run method.
       */
      public void run()
      {
         if ( !startServerSocket() )
         {
            return;
         }

         while ( mListening )
         {
            try
            {
               mSocket = mServerSocket.accept();
               mOOS = new ObjectOutputStream(mSocket.getOutputStream());
               mOIS = new ObjectInputStream(mSocket.getInputStream());

               listen();
            }
            catch ( IOException ioe )
            {
               // server socket closed
            }
            catch ( ClassNotFoundException cnfe )
            {
               //
            }
         }
      }

      /**
       * Returns the address of the server socket.
       * 
       * @return the address of the server socket.
       */
      public String getAddress()
      {
         while ( mState != STARTED )
         {
            mServer.sleep();
         }
         return mServerSocket.getInetAddress().toString();
      }

      /**
       * Closes the socket.
       * 
       * @throws IOException
       *            if an error during closing
       */
      public void close() throws IOException
      {
         super.close();

         // if not started, let it start, then kill it
         // if starting, let it finish, then kill it
         if ( (mState == NOT_STARTED) || (mState == STARTING) )
         {
            while ( mServerSocket == null )
            {
               mServer.sleep();
            }
            // prevents closing before binding
            while ( !mServerSocket.isBound() )
            {
               mServer.sleep();
            }
            mServerSocket.close();
         }
         else if ( mState == STARTED )
         {
            mServerSocket.close();
         }
         else if ( mState == ERROR )
         {
            if ( mServerSocket != null )
            {
               mServerSocket.close();
            }
         }
      }
   }
}
