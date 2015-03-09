package org.adl.testsuite.rte.lms.fileman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class is responsible for persisting test session data to file, and
 * reading the same information to continue a previously saved session. It also
 * deletes the temporary log file from the logs directory.
 *  
 * @author ADL Technical Team
 */
public class FileOperations
{
   /**
    * Logger object used for debug logging
    */
   protected static final Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");
   
   /**
    * Writes a given Object to a specified location.
    * @param iLocation String value of the folder URI the object will be written to
    * @param iName String value of the name of the object (file) to be written
    * @param iObject The Object to be written to file 
    * @return boolean representing the success of the write
    */
   public boolean writeObject(final String iLocation, final String iName, final Object iObject)
   {
      LOGGER.entering(getClass().getSimpleName(),"writeObject()");
      
      try
      {
         File file = new File(iLocation);
         file.mkdirs();
         file = new File(file, iName);
         final FileOutputStream fos = new FileOutputStream(file);
         final ObjectOutputStream oos = new ObjectOutputStream(fos);
   
         oos.writeObject(iObject);
         
         LOGGER.info("Wrote object " + iLocation + iName);
         oos.close();
      }
      catch (FileNotFoundException fnfe)
      {
         LOGGER.severe("FileNotFoundException occurred in FileOperations.writeObject()\n");
         fnfe.printStackTrace();
         return false;
      }
      catch (IOException ioe)
      {
         LOGGER.severe("IOException occurred in FileOperations.writeObject()");
         ioe.printStackTrace();
         return false;
      }      
      
      LOGGER.exiting(getClass().getSimpleName(),"writeObject()");
      return true;
   }
   
   /**
    * Reads an Object from file given a specified location.
    * @param iLocation The directory that holds the Object.
    * @param iName The name of the Object.
    * @return Object that was requested.
    */
   public Object readObject(final String iLocation, final String iName)
   {
      LOGGER.entering(getClass().getSimpleName(),"readObject()");
      try
      {
         final FileInputStream fis = new FileInputStream(iLocation + File.separator + iName);
         final ObjectInputStream ois = new ObjectInputStream(fis);
         LOGGER.info("Read in object " + iName + " from " + iLocation);
         LOGGER.exiting(getClass().getSimpleName(),"readObject()");
         return ois.readObject();      
      }
      catch (FileNotFoundException fnfe)
      {
         LOGGER.severe("FileNotFoundException occurred in FileOperations.readObject()\n"+
            fnfe.getStackTrace());
         return null;
      }
      catch (IOException ioe)
      {
         LOGGER.severe("IOException occurred in FileOperations.readObject()\n");
         ioe.printStackTrace();
         return null;
      }
      catch (ClassNotFoundException cnfe)
      {
         LOGGER.severe("ClassNotFoundException occurred in FileOperations.readObject()\n");
         cnfe.printStackTrace();
         return null;
      }
   }
   
   /**
    * Deletes an Object from file given a specified location.
    * @param iLocation The directory that holds the Object.
    * @param iName The name of the Object.
    * @return Whether or not the delete was successful.
    */
   public boolean deleteObject(final String iLocation, final String iName)
   {
      final File fileToDelete = new File(iLocation, iName);
      return fileToDelete.delete();
   }
   
   /**
    * Returns a list of Objects found at a specified location.
    * 
    * @param iLocation The location of where to look for Objects 
    * @return List of saves (File objects) found at the given location.
    */
   public List<File> getListOfSaves(final String iLocation)
   {
      LOGGER.entering(getClass().getSimpleName(),"getListOfSaves()");
      
      // declare the List object to return
      List<File> results;
      
      // declare the file object from String iLocation 
      final File loc = new File(iLocation);
      
      if(loc.isDirectory())
      {
         // get the list of files and folders in the directory
         final File[] files = loc.listFiles();
         results = Arrays.asList(files);
         // add the files to the list object
      }
      else
      {
         results = new ArrayList<File>();
      }
      
      LOGGER.exiting(getClass().getSimpleName(),"getListOfSaves()");
      return results;
   }
   
   /**
    * If the user viewed a current log throught the browser interface then
    * there is a file named "currentLog.xml" in the test log folder. At the end
    * of the test we want to remove this file from the folder
    * @param iLogDirectory the directory the current logs are written to
    * @return true if the file is found and deleted, false otherwise
    */
   public boolean deleteTempLog(final String iLogDirectory)
   {
      LOGGER.entering(getClass().getSimpleName(),"deleteTempLog()");
      LOGGER.finer("searching for currentLog.xml in directory: " + iLogDirectory);
      
      final boolean result = AccessController.doPrivileged(new PrivilegedDeleteTempLog(iLogDirectory));
      LOGGER.exiting(getClass().getSimpleName(),"deleteTempLog()-file deleted is " + result);
      return result;
   }
   
   /**
    * Privileged method that deletes the temp log.
    */
   private class PrivilegedDeleteTempLog implements PrivilegedAction<Boolean>
   {      
      /**
       * String representing the directory.
       */
      protected final transient String mDirectory;
      
      /**
       * Constructor.
       * 
       * @param iDirectory - location of the temp log.
       */
      PrivilegedDeleteTempLog(final String iDirectory)
      {
         mDirectory = iDirectory;
      }
      
      /* (Javadoc)
       * @see java.security.PrivilegedAction#run()
       */
      public Boolean run()
      {
         boolean result = false;
         final File logFolder = new File(mDirectory);
         final File[] files = logFolder.listFiles();
         for(int i=0;i<files.length;i++)
         {
            if(files[i].getName().equals("currentLog.xml"))
            {
               LOGGER.finer("found and deleting file: " + files[i].getAbsolutePath());
               result = files[i].delete();
            }
         }
         return result;
      }
      
   }// end class PrivilegedDeleteTempLog
}
