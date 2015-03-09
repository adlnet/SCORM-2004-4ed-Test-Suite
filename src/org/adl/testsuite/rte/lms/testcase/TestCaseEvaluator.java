package org.adl.testsuite.rte.lms.testcase;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.adl.datamodels.DMElement;
import org.adl.datamodels.DMElementDescriptor;
import org.adl.datamodels.DMErrorCodes;
import org.adl.datamodels.DMFactory;
import org.adl.datamodels.DMProcessingInfo;
import org.adl.datamodels.DMRequest;
import org.adl.datamodels.DataModel;
import org.adl.datamodels.RequestToken;
import org.adl.logging.LmsLoggerInterface;
import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.FailedResult;
import org.adl.testsuite.rte.lms.util.LMSMessages;
import org.adl.testsuite.rte.lms.util.Result;
import org.adl.testsuite.rte.lms.util.Results;
import org.adl.testsuite.rte.lms.util.SpecialResult;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * This class is responsible for providing evaluation support for the TestCase
 * module.
 * 
 * @author ADL Technical Team
 */
public class TestCaseEvaluator
{
   /**
    * Contant used to hold the String indicator for which log to send 
    * the message to.
    */
   private static final String DETAILED_KEY = "Detailed";
   
   /**
    * Contant used to hold the String indicator for which log to send 
    * the message to.
    */
   private static final String BOTH_KEY = "Both";
   
   /**
    * The allowed length of a GetDiagnostic or GetErrorString value.
    */
   private static final int MAX_LEN_ERROR_DIAGNOSTIC_STR = 255;
   
   /**
    * Describes the qualities of this data model element.
    */
   protected DMElementDescriptor mDescription;
   
   /**
    * Instance of the Logger Interface. Used to write messages to the test logs.
    */
   private final transient LmsLoggerInterface mLI;

   /**
    * Stores the value of the datamodel binding used during the test.
    */
   private transient String mDMBinding = "";

   /**
    * Describes the set of run-time data models managed for the SCO.
    */
   private transient Map<String, DataModel> mDataModels = null;

   /**
    * Indicates the location of the last initialized index of the
    * cmi.interactions collection. This value is used to minimize the number of
    * times collection index initialization is done per evaluation set.
    */
   private transient int mInteractionIndex = 0;

   /**
    * Indicates the location of the last initialized index of the cmi.objectives
    * collection. This value is used to minimize the number of times collection
    * index initialization is done per evaluation set.
    */
   private transient int mObjectivesIndex = 0;
   
   /**
    * Whether or not the UI answers were correct or not.
    */
   private transient boolean mUISuccess = true;
   
   /**
    * Whether or not the Command's results evaluated successfully or not.
    */
   private transient boolean mResultsSuccess = true;
   
   /**
    * The current result expected by the test.
    */
   private transient Result mCurrExpResult;
   
   /**
    * The current result returned from the LMS.
    */
   private transient Result mCurrRetResult;
   
   /**
    * The current command such as doGetValue
    */
   private transient String mCurrCommand;
   
   /**
    * The current parameters for the command such as cmi.exit!normal
    */
   private transient String mCurrParameters;

   /**
    * Overloaded constructor
    * 
    * @param iLI -
    *           Instance of LmsLoggerInterface
    */
   public TestCaseEvaluator(final LmsLoggerInterface iLI)
   {
      mLI = iLI;
   }

   /**
    * Evaluates if the test package launched by the LMS is the correct test
    * package that the CTS is expecting. No logging is done here. We just verify 
    * that the correct LMSTestContentPackagge was launched. If not we just return 
    * a false and let someone else deal with it.
    * 
    * @param iExpectedTCName
    *           The expected name of the LMSTestCase.
    * @param iReturnedTCName
    *           The LMSTestCase name returned by the LMS.
    * @return boolean representing the correctness of the test package launched.
    */
   public boolean evaluateCorrectTCLaunch(final String iExpectedTCName, final String iReturnedTCName)
   {
      return iExpectedTCName.equals(iReturnedTCName);
   }

   /**
    * Evaluates if the Activity launched by the LMS is the correct activity that
    * the CTS is expecting.
    * 
    * @param iExpectedActName
    *           The expected name of the Activity.
    * @param iReturnedActName
    *           The name of the Activity returned from the LMS.
    * @return boolean representing the correctness of the Activity launched.
    */
   public boolean evaluateCorrectActivityLaunch(final String iExpectedActName, final String iReturnedActName)
   {
      final boolean status = iExpectedActName.equals(iReturnedActName);
      addMessage(BOTH_KEY, MessageType.INFO, "Testing Activity " + iExpectedActName);
      if ( status )
      {
         addMessage(DETAILED_KEY, MessageType.PASSED, "Activity " + iExpectedActName + " was launched as expected");
      }
      else
      {
         addMessage(BOTH_KEY, MessageType.FAILED, "Expected Activity " + iExpectedActName + 
                                           " - LMS Launched Activity " + iReturnedActName);
      }
      return status; 
   }

   /**
    * Evaluates the expected UI question Results to the returned UI question
    * Results.
    * 
    * @param iQuestions
    *           The list of questions. This is expected to come as a String with
    *           the questions separated by ~.
    * @param iExpectedUI -
    *           The expected Results for the UI Questions.
    * @param iReturnedUI -
    *           The returned Results for the UI Questions.
    * @return boolean representing the correctness of the returned UI Results.
    */
   public boolean evaluateUIQuestions(final String iQuestions, final String iExpectedUI, final String iReturnedUI)
   {
      final boolean status = iExpectedUI.equals(iReturnedUI);
      
      if ( status )
      {
         addMessage(DETAILED_KEY, MessageType.PASSED, "All User Interface inspection questions were answered as expected");
      }
      else 
      {
         final String[] questions = iQuestions.split("~");
         final String[] expected = iExpectedUI.split("~");
         final String[] actual = iReturnedUI.split("~");
         
         if ( questions.length == expected.length && questions.length == actual.length )
         {
            for ( int i = 0; i < actual.length; i++ )
            {
               if ( ! expected[i].equals(actual[i]) )
               {
                  final String unBold = questions[i].replaceAll("(<b>)|(</b>)", "");
                  addMessage(DETAILED_KEY, MessageType.FAILED, unBold + 
                        " - Expected: " + ((expected[i].equals("Y"))?"Yes":"No") + 
                        " - Answered: " + ((actual[i].equals("Y"))?"Yes":"No"));
               }
            }
         }
         else
         {
            addMessage(DETAILED_KEY, MessageType.FAILED, 
                  "The number of User Interface inspection questions, expected responses or returned " +
                  "responses did not match. This is an unexpected and " +
                  "unrecoverable error. It is recommended to restart the " +
                  "testing session.");
         }
      }
      
      mUISuccess &= status;
      
      return status;
   }

   /**
    * Evaluates the expected call Results to the returned call Results.
    * 
    * @param iCommand -
    *           The list of commands for the current SCO.
    * @param iExpected -
    *           The expected Results for the calls made by the SCOs.
    * @param iReturned -
    *           The returned Results for the calls made by the SCOs.
    * @return boolean representing the correctness of the returned call Results.
    */
   public boolean evaluateResults(final Command iCommand, final Results iExpected, final Results iReturned)
   {
      final List<String> commList = iCommand.getCommList();
      final List<String> paramList = iCommand.getParamList();
      final List<Result> expected = iExpected.getResults();
      final List<Result> returned = iReturned.getResults();
      boolean status = true;
      final int commSize = commList.size();
      addDM(DMFactory.DM_SCORM_2004);
      addDM(DMFactory.DM_SCORM_NAV);
      
      if ( (commSize == paramList.size()) && 
           (commSize == expected.size()) && 
           (commSize == returned.size()) )
      {
         for ( int j = 0; j < commList.size(); j++ )
         {
            mCurrCommand = commList.get(j).toString();
            mCurrParameters = paramList.get(j).toString();
            mCurrRetResult = iReturned.getResult(j);
            mCurrExpResult = iExpected.getResult(j);
            
            status &= evaluateCurrentResult();
         }
      }
      else
      {
         addMessage(DETAILED_KEY, MessageType.FAILED, "An unexpected error has occurred " +
                                            "during evaluation of test results.");
         status = false;
      }
      
      mResultsSuccess &= status;
      
      return status;
   }
   
   /**
    * Does evaluation of the current Result object
    * 
    * @return the success of the Result evaluation
    */
   private boolean evaluateCurrentResult()
   {
      final String returnedValue = mCurrRetResult.getValue();
      final String expectedValue = mCurrExpResult.getValue();
      boolean evalSuccess;
      if ( mCurrRetResult instanceof FailedResult )
      {
         addMessage(DETAILED_KEY, MessageType.FAILED, ((FailedResult)mCurrRetResult).getMessage());
         evalSuccess = false;
      }
      else if ( mCurrRetResult instanceof SpecialResult )
      {
         evalSuccess = evaluateSpecialResult((SpecialResult)mCurrRetResult, 
                                             (SpecialResult)mCurrExpResult); 
      }
      
      else if ( mCurrCommand.equals("doGetValue") &&
                mCurrParameters.contains("learner_name") )
      {
         addMessage(DETAILED_KEY, MessageType.PASSED, "Evaluating: " + getEvalCall());
         evalSuccess = evaluateGetLastError();
      }
//      else if ( mCurrCommand.equals("doGetValue") &&
//                mCurrParameters.indexOf("session_time") > -1 )
//      {
//         addMessage(DL, MessageType.PASSED, "Evaluating: " + getEvalCall());
//         evalSuccess = evaluateGetLastError();
//      }
      // why don't we make get last error calls here?
      else if ( mCurrCommand.equals("doGetValue") &&
                (mCurrParameters.contains("request_valid.choice")  ||
                      mCurrParameters.contains("request_valid.jump")) )
      {
         evalSuccess = evaluateRegularGet();
      }
      else if ( mCurrCommand.equals("doGetValue") && 
                !mCurrParameters.contains("_children") &&
                mCurrParameters.contains("comments_from_lms") )
      {
         evalSuccess = evaluateGetCommentsFromLMS();
      }
      else if ( mCurrCommand.equals("doGetValue") && 
               ((!expectedValue.equals(""))) &&
               (!mCurrParameters.contains("_count")) )
      {
         evalSuccess = evaluateRegularGet();
         evalSuccess &= evaluateGetLastError();               
      }
      else if ( mCurrCommand.equals("doGetValue") &&
                mCurrParameters.contains("&") && 
                mCurrParameters.contains(".id") )
      {
         evalSuccess = compareObjectiveIDs(mCurrParameters, returnedValue);
         evalSuccess &= evaluateGetLastError();
      }
      else if ( mCurrCommand.equals("doGetValue") && 
                returnedValue.startsWith("{") &&
                !mCurrParameters.contains("_count") )
      {
         // to handle cases where "{lang=en}" == ""
         evalSuccess = evaluateRegularGet();
         evalSuccess &= evaluateGetLastError();
      }
      
      else if ( mCurrCommand.equals("doGetErrorString") )
      {
         evalSuccess = evaluateGetErrorString(returnedValue, expectedValue);
         evalSuccess &= evaluateGetLastError();
      }
      else if ( mCurrCommand.equals("doGetDiagnostic") )
      {
         evalSuccess = evaluateGetDiagnostic(returnedValue, expectedValue);
         evalSuccess &= evaluateGetLastError();
      }
      else
      {
         evalSuccess = (returnedValue == null)? false : returnedValue.equals(expectedValue);
         sendGeneralEvalMessage(DETAILED_KEY, evalSuccess?MessageType.PASSED:MessageType.FAILED, 
                                getEvalCall(), expectedValue, returnedValue);
         evalSuccess &= evaluateGetLastError();
      }
      
      return evalSuccess;
   }
   
   /**
    * Does evaluation specific to GetLastError calls
    * 
    * @return evaluation success
    */
   private boolean evaluateGetLastError()
   {
      final boolean errorCodeStatus = mCurrExpResult.getErrorCode().equals(mCurrRetResult.getErrorCode());
      
      sendGeneralEvalMessage(DETAILED_KEY, errorCodeStatus?MessageType.PASSED:MessageType.FAILED, 
                             "GetLastError(\"\")", mCurrExpResult.getErrorCode(), 
                             mCurrRetResult.getErrorCode());
      
      return errorCodeStatus;
   }

   /**
    * Does evaluation specifically for GetErrorString 
    * 
    * @param iReturnedValue The value returned by the test LMS
    * 
    * @param iExpectedValue The expected value.
    * 
    * @return evaluation success
    */
   private boolean evaluateGetErrorString(final String iReturnedValue, final String iExpectedValue)
   {
      boolean localResult;
//      String tempExpVal = "";   ----for rustici issue-----

      if ( iExpectedValue.equals("less255") )
      {
         localResult = iReturnedValue.length() < MAX_LEN_ERROR_DIAGNOSTIC_STR;
      }
      else if ( iExpectedValue.equals("emptyCS") )
      {
         localResult = iReturnedValue.equals("");
      }
//      if ( iReturnedValue.equals("") )        -------for rustici issue-------
//      {
//         localResult = true;
//         tempExpVal = "emptyCS";
//      }
//      else if ( iReturnedValue.length() < MAX_LEN_ERROR_DIAGNOSTIC_STR )
//      {
//         localResult = true;
//         tempExpVal = "less255";
//      } 
      else
      {
         localResult = iReturnedValue.equals(iExpectedValue);
      }
      
//      sendGeneralEvalMessage(DL, localResult?MessageType.PASSED:MessageType.FAILED, 
//                             getEvalCall(), LMSMessages.getInstance().getString(COMMANDS, tempExpVal),
//                             iReturnedValue);   ------for rustici----
      
      sendGeneralEvalMessage(DETAILED_KEY, localResult?MessageType.PASSED:MessageType.FAILED, 
            getEvalCall(), LMSMessages.Commands.getString(iExpectedValue),
            iReturnedValue);
      
      return localResult;
   }
   
   /**
    * Does evaluation specifically for GetDiagnostic
    * 
    * @param iReturnedValue The value returned by the test LMS
    * 
    * @param iExpectedValue The expected value.
    * 
    * @return evaluation success
    */
   private boolean evaluateGetDiagnostic(final String iReturnedValue, final String iExpectedValue)
   {
      final boolean localResult = iReturnedValue.length() < MAX_LEN_ERROR_DIAGNOSTIC_STR;
      
      sendGeneralEvalMessage(DETAILED_KEY, localResult?MessageType.PASSED:MessageType.FAILED, 
                             getEvalCall(), LMSMessages.Commands.getString("less255"),
                             iReturnedValue);
      
      return localResult;
   }

   /**
    * Evaluates SpecialRequest objects. This is for checks to see that the test 
    * LMS properly created objectives declared in the manifest.
    * 
    * @param iReturned The result returned by the LMS
    * 
    * @param iExpected The expected result
    * 
    * @return evaluation success
    */
   private boolean evaluateSpecialResult(final SpecialResult iReturned, final SpecialResult iExpected)
   {
      addMessage(DETAILED_KEY, MessageType.INFO, "Verifying that objectives in the manifest are found in the LMS");

      return countCompare(iReturned.getCount(), iExpected.getCount()) && objCompare(iReturned, iExpected);
   }
   
   /**
    * Compares the counts and does appropriate logging messages.
    * 
    * @param iReturnedCount The returned count
    * @param iExpectedCount The expected count
    * 
    * @return true if the counts were the same
    */
   private boolean countCompare(final int iReturnedCount, final int iExpectedCount)
   {
      final boolean countCompare = iReturnedCount == iExpectedCount;
      if ( countCompare )
      {
         addMessage(DETAILED_KEY, MessageType.PASSED, "The number of expected objectives matched the expected number");
      }
      else
      {
         addMessage(DETAILED_KEY, MessageType.FAILED, "The LMS returned " + iReturnedCount +
                                 " and " + iExpectedCount + " were expected");
      }
      return countCompare;
   }
   
   /**
    * Compares the expected objectives against those returned by the LMS
    * 
    * @param iReturned The objectives returned by the LMS
    * @param iExpected The expected objectives
    * 
    * @return evaluation success
    */
   private boolean objCompare(final SpecialResult iReturned, final SpecialResult iExpected)
   {
      boolean objCompare = true;
      
      for ( int i = 0; i < iExpected.getCount(); i++ )
      {
         objCompare &= iExpected.contains(iReturned.getObj(i));
         if ( objCompare )
         {
            addMessage(DETAILED_KEY, MessageType.PASSED, "Objective " + iReturned.getObj(i) + " was expected");
         }
         else
         {
            addMessage(DETAILED_KEY, MessageType.FAILED, "Objective " + iReturned.getObj(i) + " was not expected");
         }
      }
      return objCompare;
   }

   /**
    * General evaluation code for a GetValue request.
    * 
    * @return evaluation success
    */
   private boolean evaluateRegularGet()
   {
      boolean returnValue;
      final String returnedValue = mCurrRetResult.getValue();
      final String expectedValue = mCurrExpResult.getValue();
      final int resultIndex = mCurrRetResult.getIndex();

      if ( mCurrParameters.indexOf("request_valid") > -1 )
      {
         returnValue = evaluateRequestValid(returnedValue);
      }
      else
      {
         // if we find a '&' it means that the dm request has an objective
         // id in it as the index placeholder. We need to remove that 
         // placeholder and put in the index we found and stored in the 
         // Result object.
         if ( mCurrParameters.indexOf('&') > -1 )
         {
            mCurrParameters = replaceObjWithIndex(mCurrParameters, resultIndex);
         }
         // if mparams has an index... set the expected value first
         setUpDM(mCurrParameters, expectedValue);
         
         // Try to find the element
         final DMProcessingInfo pInfo = new DMProcessingInfo();
         final DMRequest request = new DMRequest(mCurrParameters);
         mDMBinding = request.getNextToken().getValue();
         final int finderrcode = findElement(request, pInfo);
         
         returnValue = finderrcode == DMErrorCodes.NO_ERROR;
         if ( returnValue )
         {
            if ( !request.hasMoreTokens() )
            {
               // used to have iValidate instead of true
               final DMElement del = pInfo.mElement;
               del.setValue(new RequestToken(expectedValue, true), true);
               final int errorcode = del.equals(new RequestToken(returnedValue, true), true);
               returnValue = errorcode == DMErrorCodes.COMPARE_EQUAL;

               sendGeneralEvalMessage(DETAILED_KEY, returnValue?MessageType.PASSED:MessageType.FAILED, 
                     getEvalCall(), expectedValue, returnedValue);
            }
            else
            {
               returnValue = false;
               addMessage(DETAILED_KEY, MessageType.FAILED, "Malformed data model element: " + getDMElement());
            }
         }
         else
         {
            returnValue = false;
            addMessage(DETAILED_KEY, MessageType.FAILED, "Unable to find data model element: " + getDMElement());
         }
      }
      
      return returnValue;
   }

   /**
    * Evaluates GetValue calls to comments_from_lms
    * 
    * @return evaluation success
    */
   private boolean evaluateGetCommentsFromLMS()
   {
      final int errcode = Integer.parseInt(mCurrRetResult.getErrorCode());
      
      if ( errcode == DMErrorCodes.NO_ERROR ||
           errcode == DMErrorCodes.GEN_GET_FAILURE ||
           errcode == DMErrorCodes.NOT_INITIALIZED ||
          (mCurrParameters.equals("cmi.comments_from_lms") && 
           errcode == DMErrorCodes.UNDEFINED_ELEMENT) ) 
      {
         addMessage(DETAILED_KEY, MessageType.PASSED, getEvalCall() + " returned a valid value or error code");
         
         return true;
      }
      
      addMessage(DETAILED_KEY, MessageType.FAILED, getEvalCall() + " returned a non-valid value or error code");
      
      return false;
   }

   /**
    * Takes a string such as cmi.objectives.&obj1&.success_status and changes it 
    * to cmi.objectives.0.success_status.
    * 
    * @param iStringToParse 
    *             The String to split and replace the objective id with the index.
    * @param oIndex
    *             The index location where this objective id was found.
    * @return
    *       The parsed string with the index instead of the id
    */
   private String replaceObjWithIndex(final String iStringToParse, final int oIndex)
   {
      final String firstPart = iStringToParse.substring(0,iStringToParse.indexOf('&'));
      final String secondPart = 
         iStringToParse.substring(iStringToParse.lastIndexOf('&') + 1, iStringToParse.length());
      return firstPart + oIndex + secondPart;
   }

   /**
    * Adds the identified data model to the set of run-time data models managed
    * for this SCO. First checks the current set of managed data models to
    * ensure that the data model to be added is not aready present in the
    * Hashtable.
    * 
    * @param iModel
    *           Describes the run-time data model to be added.
    */
   private void addDM(final int iModel)
   {
      // Create the indicated data model
      final DataModel dm = DMFactory.createDM(iModel);

      if ( dm != null )
      {
         // Make sure this data model isn't already being managed
         if ( mDataModels == null )
         {
            mDataModels = new Hashtable<String, DataModel>();

            mDataModels.put(dm.getDMBindingString(), dm);
         }
         else
         {
            final DataModel check = (DataModel) mDataModels.get(dm.getDMBindingString());

            if ( check == null )
            {
               mDataModels.put(dm.getDMBindingString(), dm);
            }
         }
      }
   }

   /**
    * Retrieves a specific Data Model managed by this
    * <code>SCODataManager</code>.
    * 
    * @param iDataModel
    *           Describes the dot-notation binding string of the desired data
    *           model.
    * @return The <code>DataModel</code> object associated with the requested
    *         data model.
    */
   private DataModel getDataModel(final String iDataModel)
   {
      DataModel dm = null;

      if ( mDataModels != null )
      {
         dm = mDataModels.get(iDataModel);
      }

      return dm;
   }

   /**
    * Returns the success of this evaluation.
    * 
    * @return the success of the Activity
    */
   public boolean reportActivityStatus()
   {
      final boolean success = mUISuccess && mResultsSuccess;

      mUISuccess = true;
      mResultsSuccess = true;
      
      return success;
   }
   
   /**
    * Writes the general logging message to the requested log. This will come out 
    * something like: 
    * "Evaluating GetValue(cmi.exit) - Expected: normal - Returned: normal"
    * 
    * @param iLog the log to send receive this message - "Detailed", "Summary", "Both"
    * @param iMessageType MessageType type.. usually PASSED or FAILED
    * @param iWhatWeEvaluated The call we evaluated
    * @param iExpectedValue The expected return value
    * @param iReturnedValue The actual return value
    */
   private void sendGeneralEvalMessage(final String iLog, 
                                       final int iMessageType, 
                                       final String iWhatWeEvaluated, 
                                       final String iExpectedValue, 
                                       final String iReturnedValue)
   {
      if ( (iWhatWeEvaluated.indexOf("ErrorString") > -1 || 
            iWhatWeEvaluated.indexOf("Diagnostic") > -1) && 
            (iExpectedValue.equals(LMSMessages.Commands.getString("less255")) || 
             iExpectedValue.equals(LMSMessages.Commands.getString("emptyCS"))) )
      {
         addMessage(iLog, iMessageType, "Evaluating " + iWhatWeEvaluated + 
               " - Expected: " + iExpectedValue + 
               " - LMS Returned: \"" + iReturnedValue + "\"");
      }
      else
      {
         addMessage(iLog, iMessageType, "Evaluating " + iWhatWeEvaluated + 
                                 " - Expected: \"" + iExpectedValue + 
                                 "\" - LMS Returned: \"" + iReturnedValue + "\"");
      }
   }

   /**
    * Provides the ability to log a message if a LmsLoggingInterface instance is
    * available.
    * 
    * @param iLogType
    *           String representing the log type, either "Detailed" or "Summary"
    * @param iMsgType
    *           The MessageType value for this log message
    * @param iMsg
    *           The message being added.
    */
   private void addMessage(final String iLogType, final int iMsgType, final String iMsg)
   {
      if ( mLI != null )
      {
         mLI.addMessage(iLogType, new LogMessage(iMsgType, iMsg));
      }
      else
      {
         System.out.println("\n------Message------" + 
                            "\nLog: " + iLogType + 
                            "\nType: " + iMsgType + 
                            "\nMessage: " + iMsg + 
                            "\n----End Message----\n");
      }
   }
   
   /**
    * Returns the evaluation call in a human friendly format. ex: GetValue(cmi.exit)
    * 
    * @return String representing the current evaluation call made.
    */
   private String getEvalCall()
   {
//    this comes in as doAPICall we're parsing it to be APICall(xxx)
      final String apiMethod = mCurrCommand.substring(2, mCurrCommand.length());
      String param = getDMElement();      
      
      if ( param.indexOf('&') > -1 )
      {
         final String firstPart = param.substring(0, param.indexOf('&'));
         final String secondPart = param.substring(param.lastIndexOf('&') + 1, param.length());
         param = firstPart + mCurrRetResult.getIndex() + secondPart;
      }
      
      return apiMethod + "(" + param + ")";
   }
   
   /**
    * Parses the "cmi.exit!normal" into a more readable format such as 
    * "cmi.exit, normal"
    * 
    * @return The dm element in a format that is easier to read
    */
   private String getDMElement()
   {
      if ( mCurrParameters.indexOf('!') != -1 )
      {
         String[] params;
         if( mCurrParameters.indexOf('!') == mCurrParameters.length()-1 )
         {
            params = new String[]{mCurrParameters.split("!")[0], ""};
         }
         else
         {
            params = new String[] {mCurrParameters.substring(0, mCurrParameters.indexOf('!')),
               mCurrParameters.substring(mCurrParameters.indexOf('!') + 1, mCurrParameters.length())};
         }
         
         return "\"" + params[0] + "\", \"" + params[1] + "\"";
      }
      
      return "\"" + mCurrParameters + "\"";
   }

   /**
    * Processes a data model request by finding the target leaf element.
    * 
    * @param iRequest
    *           The request (<code>DMRequest</code>) being processed.
    * @param oInfo
    *           Provides the value returned by this request.
    * @return A data model error code indicating the result of this operation.
    */
   private int findElement(final DMRequest iRequest, final DMProcessingInfo oInfo)
   {
      // Assume no processing errors
      int result = DMErrorCodes.NO_ERROR;

      // Get the first specified element
      RequestToken tok = iRequest.getCurToken();

      if ( tok != null && tok.getType() == RequestToken.TOKEN_ELEMENT )
      {

         DMElement element = getDataModel(mDMBinding).getDMElement(tok.getValue());

         if ( element != null )
         {
            oInfo.mElement = element;

            // Check if we need to stop before the last token
            tok = iRequest.getNextToken();
            boolean done = false;

            if ( tok != null )
            {
               if ( iRequest.isGetValueRequest() )
               {
                  if ( tok.getType() == RequestToken.TOKEN_ARGUMENT )
                  {
                     // We're done
                     done = true;
                  }
                  else if ( tok.getType() == RequestToken.TOKEN_VALUE )
                  {
                     // Get requests cannot have value tokens
                     result = DMErrorCodes.INVALID_REQUEST;

                     done = true;
                  }
               }
               else
               {
                  if ( tok.getType() == RequestToken.TOKEN_VALUE )
                  {
                     // We're done
                     done = true;
                  }
                  else if ( tok.getType() == RequestToken.TOKEN_ARGUMENT )
                  {
                     // Set requests cannot have argument tokens
                     result = DMErrorCodes.INVALID_REQUEST;

                     done = true;
                  }
               }
            }

            // Process remaining tokens
            while ( !done && iRequest.hasMoreTokens() && result == DMErrorCodes.NO_ERROR )
            {
               // verifies that the request is permittable for this element
               result = element.processRequest(iRequest, oInfo);

               // Move to the next element if processing was successful
               if ( result == DMErrorCodes.NO_ERROR )
               {
                  element = oInfo.mElement;
               }
               else
               {
                  oInfo.mElement = null;
               }

               // Check if we need to stop before the last token
               tok = iRequest.getCurToken();

               if ( tok != null )
               {
                  if ( iRequest.isGetValueRequest() )
                  {
                     if ( tok.getType() == RequestToken.TOKEN_ARGUMENT )
                     {
                        // We're done
                        done = true;
                     }
                     else if ( tok.getType() == RequestToken.TOKEN_VALUE )
                     {
                        // Get requests cannot have value tokens
                        result = DMErrorCodes.INVALID_REQUEST;

                        done = true;
                     }
                  }
                  else
                  {
                     if ( tok.getType() == RequestToken.TOKEN_VALUE )
                     {
                        // We're done
                        done = true;
                     }
                     else if ( tok.getType() == RequestToken.TOKEN_ARGUMENT )
                     {
                        // Set requests cannot have argument tokens
                        result = DMErrorCodes.INVALID_REQUEST;

                        done = true;
                     }
                  }
               }
            }
         }
         else
         {
            // Unknown element
            result = DMErrorCodes.UNDEFINED_ELEMENT;
         }
      }
      else
      {
         // No initial element specified
         result = DMErrorCodes.INVALID_REQUEST;
      }

      // Make sure we are at a leaf element
      if ( result == DMErrorCodes.NO_ERROR && oInfo.mElement.getDescription().mChildren != null )
      {
         // Unknown element
         result = DMErrorCodes.UNDEFINED_ELEMENT;
      }

      return result;
   }

   /**
    * Decides if any initialization needs done to prevent dependency errors. This 
    * method will look over the element to see if it is a collection. If it is, 
    * it will redirect the element to the setDMElement function to set the 
    * necessary ids to prevent the dependency errors.
    * 
    * @param iElement The dot notation representation of the element being evaluated.
    * 
    * @param iValue The value of the element being evaluated.
    */
   private void setUpDM(final String iElement, final String iValue)
   {
      // breaks up the element tokens into an array for easier cycling over the 
      // dm element.
      final String[] elementTokens = parseToArray(iElement, '.');

      int idx1 = -1;
      int idx2 = -1;
      
      // For the list of element tokens, see if any are numbers. If they are,
      // set index 1 first then index 2 if there are two. Then call setDMElement.
      for ( int i = 0; i < elementTokens.length; i++ )
      {
         try
         {
            if ( idx1 == -1 )
            {
               idx1 = Integer.parseInt(elementTokens[i]);
            }
            else
            {
               idx2 = Integer.parseInt(elementTokens[i]);
            }
         }
         catch ( NumberFormatException nfe )
         {
            // don't do anything, we are just interested in the tokens that are
            // ints.
         }
      }
      if ( idx1 > -1 )
      {
         setDMElement(iElement, iValue, idx1, idx2);
      }
   }

   /**
    * Responsible for setting any and all dm elements needed to set up the 
    * datamodel to compare and expect the results we want. If there are no 
    * indecies then iIdx1 and iIdx2 should be set to -1.
    * 
    * @param iElement The element to set.
    * @param iValue The value to be set for the element.
    * @param iIdx1 The first index found in the element.
    * @param iIdx2 The second index found in the element.
    */
   private void setDMElement(final String iElement, final String iValue, final int iIdx1, final int iIdx2)
   {
      if ( iIdx1 > -1 )
      {
         // check to see if there are 2 indecies which means we have a 
         // cmi.interactions.n.objectives.m.id type call
         // also see if we have a count token. 
         if ( iElement.indexOf("_count") != -1 )
         {
            // no implementation needed, just skip if _count found
         }
         else if ( iElement.indexOf("comments_from_lms") != -1 )
         {
             // no implementation needed, just skip if comment_from_lms found
         }
         else if ( iIdx2 > -1 )
         {
            // both interaction and objective
            final String[] temp = parseToArray(iElement, '.');

            final String[] interArray = { temp[0], temp[1], temp[2], temp[3] };

            // this will just set the interactions (n) part of a cmi.interactions.n.objectives.m.id
            setCollectionIds(convertArrayToElement(interArray), mInteractionIndex, iIdx1);

            // this sets the objectives (m) part of cmi.interactions.n.objectives.m.id 
            setCollectionIds(iElement, 0, iIdx2);

            // store the last index set by setCollectionIds
            mInteractionIndex = iIdx1;
         }
         else
         {
            // only one collection
            setCollectionIds(iElement, mObjectivesIndex, iIdx1);

            // store the last index set by setCollectionIds
            mObjectivesIndex = iIdx1;
         }
      }
      final DMRequest req = new DMRequest(iElement, iValue, true);
      getDataModel(req.getNextToken().getValue()).setValue(req);
   }

   /**
    * This function will set all of the collection ids to some default value. 
    * It starts setting ids at the last known set id for this element
    * 
    * @param iElement the element to set
    * @param iStartingAt the starting index
    * @param iEndingAt the ending index
    */
   private void setCollectionIds(final String iElement, final int iStartingAt, final int iEndingAt)
   {
      int idx = iStartingAt;
      final String[] parsed = parseToArray(iElement, '.');
      String[] tempArray;
      
      if ( (parsed.length > 5) && (parsed[3].indexOf("correct_responses") > -1) )
      {
         tempArray = updateArray(parsed, "pattern");
      }
      else
      {
         tempArray = updateArray(parsed, "id");
      }
            
      while ( idx <= iEndingAt )
      {
         tempArray[tempArray.length - 2] = Integer.toString(idx);
         final DMRequest dmreq = new DMRequest(convertArrayToElement(tempArray), "id" + idx, true);
         getDataModel(dmreq.getNextToken().getValue()).setValue(dmreq);
         
         if ( ( tempArray.length < 5 ) && (tempArray[1].equals("interactions")) )
         {
            tempArray[ tempArray.length - 1 ] = "type";
            final DMRequest dmreqType = new DMRequest(convertArrayToElement(tempArray), mCurrExpResult.getType(), true);
            getDataModel(dmreqType.getNextToken().getValue()).setValue(dmreqType);
            tempArray[ tempArray.length - 1 ] = "id";
         }  
         
         idx++;
      }
   }

   /**
    * Parses a string into an array based on the given character. This was 
    * written to bypass issues found using the split() function.
    * 
    * @param iDMElementString The string to parse.
    * 
    * @param iChar The character on which to parse.
    * 
    * @return The parsed segments of the String stored in a String[].
    */
   private String[] parseToArray(String iDMElementString, final char iChar)
   {
      int arraySize = 1;
      for (int i = iDMElementString.indexOf(iChar); i != -1; i = iDMElementString.indexOf(iChar, i + 1))
      {
         arraySize++;
      }
      
      final String[] elementTokens = new String[arraySize];
      int idx = 0;
      while ( !iDMElementString.equals("") )
      {
         if ( iDMElementString.indexOf(iChar) != -1 )
         {
            elementTokens[idx++] = iDMElementString.substring(0, iDMElementString.indexOf(iChar));
            iDMElementString = iDMElementString.substring(iDMElementString.indexOf(iChar) + 1, iDMElementString.length());
         }
         else
         {
            elementTokens[idx++] = iDMElementString;
            iDMElementString = "";
         }
      }

      return elementTokens;
   }

   /**
    * Converts an array back to a dm element with the dots and stuff.
    * 
    * @param iArray The array, each index containing one segmented piece of the 
    * dm element.
    * 
    * @return DM element string.
    */
   private String convertArrayToElement(final String[] iArray)
   {
      final StringBuffer sb = new StringBuffer();
      for ( int i = 0; i < iArray.length; i++ )
      {
         if ( i == 0 )
         {
            sb.append(iArray[i]);
         }
         else
         {
            sb.append('.');
            sb.append(iArray[i]);
         }
      }
      return sb.toString();
   }
   
   /**
    * Inserts the value into the tokenized element array
    * 
    * @param iarray The element array
    * @param iValue The value to insert
    * @return the updated array
    */
   private String[] updateArray(final String[] iarray, final String iValue)
   {
      // assumes the tokens are something like cmi.objectives.0.completion_threshold
      // or cmi.interactions.0.objectives.0.id
      if (iarray.length == 4 || iarray.length == 6)
      {
         iarray[iarray.length - 1] = iValue;
         return iarray;
      }
      
      int index = 0;
      
      for (int i = 0; i < iarray.length; i++)
      {
         try
         {
            Integer.parseInt(iarray[i]);
         }
         catch ( NumberFormatException nfe )
         {
            // don't do anything, we are just interested in the tokens that are
            // ints.
         }
         
         index = i;
      }
      
      String[] temp = new String[index];
      
      for (int i = 0; i < temp.length; i++)
      {
         if ( i == temp.length - 1 )
         {
            temp[i] = iValue;
            break;
         }
         
         temp[i] = iarray[i];
      }
      
      return temp;
   }
   
   /**
    * Compares objective ids
    * @param iDMElement the dm element
    * @param iReturnValue the return value
    * @return true or false.
    */
   private boolean compareObjectiveIDs(final String iDMElement, final String iReturnValue)
   {
      final String firstSymbolRemoved = 
         iDMElement.substring(iDMElement.indexOf('&') + 1, iDMElement.length());
      final String objVal = 
         firstSymbolRemoved.substring(0, firstSymbolRemoved.indexOf('&'));
      final boolean compareSuccess = objVal.equals(iReturnValue);
      
      sendGeneralEvalMessage(DETAILED_KEY, compareSuccess?MessageType.PASSED:MessageType.FAILED, 
                             getEvalCall(), objVal, iReturnValue);
      
      return compareSuccess;
   }
   
   /**
    * Compares the returned value against the list of acceptable values and returns 
    * whether the returned value was valid or not.
    * 
    * @param iReturnedValue The value returned by the LMS.
    * 
    * @return boolean of whether the returned value was acceptable
    */
   private boolean evaluateRequestValid(final String iReturnedValue)
   {
      final boolean compareSuccess = iReturnedValue.equals("true") ||
                               iReturnedValue.equals("false") ||
                               iReturnedValue.equals("unknown");
      
      if ( compareSuccess )
      {
         sendGeneralEvalMessage(DETAILED_KEY, MessageType.PASSED, 
               getEvalCall(), iReturnedValue, iReturnedValue);
      }
      else
      {
         sendGeneralEvalMessage(DETAILED_KEY, MessageType.FAILED, 
               getEvalCall(), "true, false, or unknown", iReturnedValue);
      }
      
      return compareSuccess;
   }
}