package org.adl.validator.util.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.adl.validator.util.Messages;
import org.xml.sax.InputSource;

/**
 * This class will handle the setup of an inputSource in preparation for parsing
 * The encoding of the xml instance is determined and any necessary adjustments are
 * made
 * 
 * @author ADL Technical Team
 *
 */
public class FileHandler
{
   /**
    * A Constant value of 1
    */
   private static final int ONE = 1;
   
   /**
    * A Constant value of 2
    */
   private static final int TWO = 2;
   
   /**
    * A Constant value of 3
    */
   private static final int THREE = 3;
   
   /**
    * A Constant value of 4
    */
   private static final int FOUR = 4;
   
   /**
    * A Constant value of 5
    */
   private static final int FIVE = 5;
   
   /**
    * A Constant value of 6
    */
   private static final int SIX = 6;
   
   /**
    * A Constant value for 0xEF
    */
   private static final int EF = 0xEF;
   
   /**
    * A Constant for 0xBB
    */
   private static final int BB = 0xBB;
   
   /**
    * A Constant value for 0xFE
    */
   private static final int FE = 0xFE;
   
   /**
    * A Constant for 0xFF
    */
   private static final int FF = 0xFF;
   
   /**
    * A Constant for 0xBF
    */
   private static final int BF = 0xBF;
   
   /**
    * A List object containing all error messages
    */
   List<String> mErrorMessages;
   
   /**
    * Default constructor
    */
   public FileHandler()
   {
      mErrorMessages = new ArrayList<String>();
   }     
   
   /**
    * Sets up the file source for the test subject file.
    *
    * @param iFileName file to setup input source for.
    *
    * @return InputSource
    */
   public InputSource setupFileSource( String iFileName)
   {
      String msgText;
      boolean defaultEncoding = true;
      String encoding = null;
      PushbackInputStream inputStream;
      FileInputStream inFile;
                  
      try
      {
         File xmlFile = new File( iFileName );

         if ( xmlFile.isFile() )
         {
            InputSource is = null;            
            
            defaultEncoding = true;
            if (xmlFile.length() > 1)
            {              
               inFile = new FileInputStream( xmlFile );
               inputStream = new PushbackInputStream(inFile, FOUR);
               
               // Reads the initial 4 bytes of the file to check for a Byte
               // Order Mark and determine the encoding
               
               byte bom[] = new byte[FOUR];
               int n, pushBack;
               n = inputStream.read(bom, 0, bom.length);

               // UTF-8 Encoded
               if (  (bom[0] == (byte)EF) && (bom[ONE] == (byte)BB) &&
                    (bom[TWO] == (byte)BF) ) 
               {
                  encoding = "UTF-8";
                  defaultEncoding = false;
                  pushBack = n - THREE;
               }
               // UTF-16 Big Endian Encoded
               else if ( (bom[0] == (byte)FE) && (bom[ONE] == (byte)FF) ) 
               {
                  encoding = "UTF-16BE";
                  defaultEncoding = false;
                  pushBack = n - TWO;
               }
               // UTF-16 Little Endian Encoded               
               else if ( (bom[0] == (byte)FF) && (bom[ONE] == (byte)FE) ) 
               {
                  encoding = "UTF-16LE";
                  defaultEncoding = false;
                  pushBack = n - TWO;
               }               
               // Default encoding
               else
               {
                  // Unicode BOM mark not found, unread all bytes                  
                    pushBack = n;
               }

               // Place any non-BOM bytes back into the stream
               if (pushBack > 0)
               {
                  inputStream.unread(bom, n - pushBack, pushBack);
               }
               
               if (defaultEncoding == true)
               {   //Reads in ASCII file.
                  FileReader fr = new FileReader( xmlFile );
                  is = new InputSource(fr);
               }
               // Reads the file in the determined encoding
               else
               {
                  //Creates a buffer with the size of the xml encoded file
                  BufferedReader inStream =  new BufferedReader(new InputStreamReader( inputStream, encoding )); 
                  StringBuffer dataString = new StringBuffer();
                  String s = ""; 

                  //Builds the encoded file to be parsed
                  while ((s = inStream.readLine()) != null)
                  {
                     dataString.append(s);
                  }

                  inStream.close();                  
                  is = new InputSource(new StringReader(dataString.toString()));
                  is.setEncoding(encoding); 
               }
               inputStream.close();
               inFile.close();
            }                       
            return is;
         }
         else if ( ( iFileName.length() > SIX ) &&
                   ( iFileName.substring(0,FIVE).equals("http:") || 
                    iFileName.substring(0,SIX).equals("https:") ) ) 
         {
            URL xmlURL = new URL( iFileName );
            InputStream xmlIS = xmlURL.openStream();
            InputSource is = new InputSource(xmlIS);
            return is;
         }
         else
         {
            msgText = Messages.getString("FileHandler.0", iFileName);
            mErrorMessages.add(msgText);
         }
      }
      catch ( NullPointerException  npe )
      {
         msgText = Messages.getString("FileHandler.1", npe.toString());
         mErrorMessages.add(msgText);
      }
      catch ( SecurityException se )
      {
         msgText = Messages.getString("FileHandler.2", se.toString());
         mErrorMessages.add(msgText);
      }
      catch ( FileNotFoundException fnfe )
      {
         msgText = Messages.getString("FileHandler.3", fnfe.toString());
         mErrorMessages.add(msgText);
      }
      catch ( IOException ioe )
      {
         msgText = Messages.getString("FileHandler.4", ioe.toString());
         mErrorMessages.add(msgText);
      }
      
      return new InputSource();
   }
   
   /**
    * This method will return a list of all error messages
    * 
    * @return a List of all error messages
    */
   public List<String> getErrorMessages()
   {
      return mErrorMessages;
   }

}
