package org.adl.api.ecmascript;

/**
 * <strong>Filename:</strong> APIErrorCodes<br><br>
 *
 * <strong>Description:</strong><br>
 * This class contains an enumeration of the abstract API error codes
 * <br><br>
 *
 * <strong>Known Problems:</strong><br><br>
 *
 * <strong>Side Effects:</strong><br><br>
 *
 * <strong>References:</strong><br>
 * <ul>
 *     <li>SCORM 2004</li>
 *     <li>IEEE 1484.11.2-2003 Standard for Learning Technology - 
 *     ECMAScript Application Programming Interface for Content to 
 *     Runtime Services Communication</li>
 *     
 * </ul>
 *
 * @author ADL Technical Team
 */
public class APIErrorCodes
{
   /**
    * The No Error constant.  System constant used to indicate that no errors 
    * were encountered (i.e., a successful API call).<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int NO_ERROR = 0;

   /**
    * The General Exception constant.  System constant used to indicate that a 
    * general exception for which a more specific error code is not 
    * available has occurred.<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int GENERAL_EXCEPTION = 101;

   /**
    * The General Initialization Failure constant.  System constant used to 
    * indicate that an attempt to initialize 
    * (i.e., <code>Initialize("")</code>) failed.  No other error information
    * is available.<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int GENERAL_INIT_FAILURE = 102;

   /**
    * The Already Initialized constant.  System constant used to indicate that 
    * an attempt was made to re-initialize communication 
    * (i.e., <code>Initialize("")</code>) that had already been 
    * initialized.<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int ALREADY_INITIALIZED = 103;

   /**
    * The Content Instance Terminated constant.  System constant 
    * used to indicate that an attempt was made to re-initialize 
    * communication (i.e., <code>Initialize("")</code>) by an instance of a
    * content object with a communication state of <b>"terminated"</b>.<br><br>
    * 
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int CONTENT_INSTANCE_TERMINATED = 104;

   /**
    * The General Termination Failure constant.  System constant 
    * used to indicate that an attempt to terminate the communication
    * session (i.e., <code>Terminate("")</code>) failed and no other error
    * information is available.<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int GENERAL_TERMINATION_FAILURE = 111;

   /**
    * The Terminate Before Initialization constant.  System constant
    * used to indicate that an attempt was made to terminate 
    * communication (i.e., <code>Terminate("")</code>) prior to
    * initialization (i.e., <code>Initialization("")</code>).<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int TERMINATE_BEFORE_INIT = 112;

   /**
    * The Terminate After Terminate constant.  System constant
    * used to indicate that an attempt was made to terminate
    * communication (i.e., <code>Terminate("")</code>) that had already
    * been terminated (i.e., <code>Terminate("")</code>).<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int TERMINATE_AFTER_TERMINATE = 113;

   /**
    * The Get Before Initialization constant.  System constant 
    * used to indicate that an attempt was made to retrieve a value for 
    * a data element (i.e., <code>GetValue(parm1)</code>) before communication
    * had been initialized (i.e., <code>Initialize("").</code>)<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int GET_BEFORE_INIT = 122;

   /**
    * The Get After Terminate constant.  System constant 
    * used to indicate that an attempt was made to retrieve a value 
    * for a data element (i.e., <code>GetValue(parm1)</code>) after 
    * communication had been terminated 
    * (i.e., <code>Terminate("").</code>)<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int GET_AFTER_TERMINATE = 123;

   /**
    * The Set Before Initialization constant.  System constant used to indicate 
    * that an attempt was made to store a value for a data element 
    * (i.e., <code>SetValue(parm1,parm2)</code>) before communication
    * had been initialized (i.e., <code>Initialize("").</code>)<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int SET_BEFORE_INIT = 132;

   /**
    * The Set After Terminate constant.  System constant used to indicate that 
    * an attempt was made to store a value for a data element 
    * (i.e., <code>SetValue(parm1,parm2)</code>) after communication had been 
    * terminated (i.e., <code>Terminate("")</code>).<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int SET_AFTER_TERMINATE = 133;

   /**
    * The Commit Before Initialization constant.  System constant used to 
    * indicate that an attempt was made to commit (i.e., <code>Commit("")</code>) data 
    * to storage before communication had been initialized 
    * (i.e., <code>Initialize("")</code>).<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int COMMIT_BEFORE_INIT = 142;

   /**
    * The Commit After Terminate constant.  System constant used to indicate 
    * that an attempt was made to commit (i.e., <code>Commit("")</code>) data 
    * to storage after communication has been terminated 
    * (i.e., <code>Terminate("")</code>).<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int COMMIT_AFTER_TERMINATE = 143;

   /**
    * The General Argument Error constant.  System constant used to indicate 
    * that an attempt was made to pass an invalid argument.<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int GEN_ARGUMENT_ERROR = 201;

   /**
    * The General Commit Failure constant.  System constant used to indicate 
    * that an attempt to commit (i.e., <code>Commit("")</code> data to storage 
    * failed and no other error information is available<br><br>
    * See <b>IEEE 1484.11.2-2003</b> for more details
    */
   public static final int GENERAL_COMMIT_FAILURE = 391;
}

