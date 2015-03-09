package org.adl.testsuite.rte.lms.interfaces;

import java.io.File;
import java.util.List;

/**
 * TODO
 *
 */
public interface FileManager
{
   /**
    * Supplies the ability to save an object.
    * 
    * @param name the name of the object to be written to file
    * @param in The object to be written to file.
    * 
    * @return boolean value representing whether the save was successful.
    */
   boolean save(String name, Object in);
   
   /**
    * Supplies the ability to load an object from a file.
    * 
    * @param in The name of the file to load.
    * 
    * @return The Object requested.
    */
   Object load(String in);
   
   /**
    * Gets a List of saves.
    * @return The List of saves available.
    */
   List<File> getListOfSaves();
   
   /**
    * Deletes an object with the given name.
    * 
    * @param iName the name of object to be deleted
    * 
    * @return whether or not the delete was successful
    */
   boolean deleteObject(String iName);
}
