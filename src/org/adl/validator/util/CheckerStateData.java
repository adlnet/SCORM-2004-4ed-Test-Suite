package org.adl.validator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for holding any information that needs to be 
 * shared between package checkers.
 *  
 * @author ADL Technical Team
 *
 */
public final class CheckerStateData
{   
   /**
    * Instance of the CheckerStateData.
    */
   private static CheckerStateData instance;
   
   /**
    * Collection that holds the objects that are shared across multiple package 
    * checkers.
    */
   private final Map mObjectMap = new HashMap();
   
   /**
    * List of String that are considered reserved key names.
    */
   private final List<String> mReservedKeyNames = new ArrayList<String>();
   
    /**
    * Constructor
    */
   private CheckerStateData()
   {
      //default Constructor
   }
   
   /**
    * Returns a protected instance of this class.
    * 
    * @return An instance of CheckerStateData.
    */
   public static CheckerStateData getInstance()
   {
      if(instance == null)
      {
         instance = new CheckerStateData();
      }
      return instance;
   }

   /**
    * Associates a specified object with the specified String in this map and 
    * vertifies that the key being set is not one of the validator specified
    * reserved keys.
    * 
    * @param iKey - String that represents a key in the collection.
    * @param iObject - Any object that is needed to be shared across package 
    * checkers.
    * @return boolean - Indicating if the key and value were successfully added
    * to the collection.
    */
   public boolean setObject(String iKey, Object iObject)
   {
      if ( !mReservedKeyNames.contains(iKey) )
      {
         return (mObjectMap.put(iKey, iObject)) == null;
      }
      else
      {
         return false;
      }      
   }
   
   /**
    * Provides the ability to get a List of the String keys in the collection.
    * 
    * @return List of String keys in the collection.
    */
   public List<String> getValueKeys()
   {
      String objectKey; 
      List<String> mValueNames = new ArrayList<String>();
      Iterator<String> itr = mObjectMap.keySet().iterator();
      while ( itr.hasNext())
      {
         objectKey = itr.next().toString();
         mValueNames.add(objectKey);
      }
      return mValueNames;
   }
   
   /**
    * Provides the ability to get a specific object from the collection by 
    * providing the associated key.
    * 
    * @param iKey - String that represents a key in the collection.
    * @return Object - Any object that is needed to be shared across package 
    * checkers.
    */
   public Object getObjectValue(String iKey)
   {
      return mObjectMap.get(iKey);
   }
   
   /**
    * Provides the ability to add a unique String that represents a key to the 
    * list of reserved keys for the current instance of the package validator.
    * 
    * @param iReservedKey - String to be added to the reserved key list.
    * @return boolean - Indicates if the String was successfully added to the 
    * list of reserved keys.
    */
   public boolean addReservedKey(String iReservedKey)
   {
      if ( !mReservedKeyNames.contains(iReservedKey) )
      {
         return mReservedKeyNames.add(iReservedKey);
      }
      else
      {
         return false;
      }      
   }
   
   /**
    * Clears the CheckerStateData object
    */
   public final void clearCollection()
   {
      instance = null;
   }
   
}
