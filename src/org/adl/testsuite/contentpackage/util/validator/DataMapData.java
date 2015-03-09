package org.adl.testsuite.contentpackage.util.validator;

/**
 * Object to hold information on a given adl.data map
 * 
 * @author ADL Technical Team
 *
 */
public class DataMapData
{
   /**
    * String value to hold the target ID
    */
   private String mTargetID;
   
   /**
    * boolean indicating whether or not we can write
    */
   private String mWriteSharedData;
   
   /**
    * boolean indicating whether or not we can read
    */
   private String mReadSharedData;
   
   /**
    * Default constructor
    */
   public DataMapData()
   {
      mTargetID = "";
      mWriteSharedData = "";
      mReadSharedData = "";
   }

   /**
    * Constructor
    * 
    * @param iTargetID - targetID
    * @param iWriteSharedData - writeSharedData
    * @param iReadSharedData - readSharedData
    */
   public DataMapData(String iTargetID, String iWriteSharedData, String iReadSharedData)
   {
      mTargetID = iTargetID;
      mWriteSharedData = iWriteSharedData;
      mReadSharedData = iReadSharedData;
   }

   /**
    * gets write shared data
    * 
    * @return Returns the mTargetID.
    */
   public String getTargetID()
   {
      return mTargetID;
   }

   /**
    * sets write shared data
    * 
    * @param iTargetID The mTargetID to set.
    */
   public void setTargetID(String iTargetID)
   {
      mTargetID = iTargetID;
   }

   /**
    * gets write shared data
    * 
    * @return Returns the mWriteSharedData.
    */
   public String getWriteSharedData()
   {
      return mWriteSharedData;
   }

   /**
    * sets write shared data
    * 
    * @param iWriteSharedData The mWriteSharedData to set.
    */
   public void setWriteSharedData(String iWriteSharedData)
   {
      mWriteSharedData = iWriteSharedData;
   }

   /**
    * gets read shared data
    * 
    * @return Returns the mReadSharedData.
    */
   public String getReadSharedData()
   {
      return mReadSharedData;
   }

   /**
    * sets read shared data
    * 
    * @param iReadSharedData The mReadSharedData to set.
    */
   public void setReadSharedData(String iReadSharedData)
   {
      mReadSharedData = iReadSharedData;
   }

}
