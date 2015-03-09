package org.adl.testsuite.rte.lms.fileman;

import java.io.File;
import java.util.List;

import org.adl.testsuite.rte.lms.interfaces.FileManager;

/**
 * 
 * @author ADL Technical Team
 *
 */
public class LMSFileManager implements FileManager
{
   /**
    * object that does the heavy lifting (save/load)
    */
   protected final transient FileOperations fileOps = new FileOperations();
   
   /**
    * holds the string value of the save folder
    */
   private final transient String mFileLocation;
   
   /**
    * constructor used to set the default location of the "saves" folder
    * @param envVar used to set up the URI of the "saves" folder
    */
   public LMSFileManager(final String tsHome)
   {
      mFileLocation = tsHome + File.separator + "testsuite" + File.separator 
      + "saves" + File.separator;
   }

   /**
    * returns a List object holding all of the saved File objects in the
    * save folder
    * @return List object holding the saved files in the save folder
    */
   public List<File> getListOfSaves()
   {
      return fileOps.getListOfSaves(mFileLocation);
   }

   /**
    * allows the user to load a previously saved test
    * @param in the String identifier of the saved test to load
    * @return the Object containing the data from the saved test
    */
   public Object load(final String test)
   {
      if(!"".equals(test))
      {
         return fileOps.readObject(mFileLocation, test);
      }
      return null;
   }

   /**
    * writes the saved object to file
    * @param name the name of the object to save
    * @param object Object to be saved
    * @return true if the save is successful, false otherwise
    */
   public boolean save(final String name, final Object object)
   {
      return fileOps.writeObject(mFileLocation, name, object);
   }
   
   /**
    * If the user viewed a current log throught the browser interface then
    * there is a file named "currentLog.xml" in the test log folder. At the end
    * of the test we want to remove this file from the folder
    * @param folderName the directory the current logs are written to
    * @return true if the file is found and deleted, false otherwise
    */
   public boolean deleteTempLog(final String folderName)
   {
      return fileOps.deleteTempLog(folderName);
   }

   public boolean deleteObject(final String iName)
   {
      return fileOps.deleteObject(mFileLocation, iName);
   }

}
