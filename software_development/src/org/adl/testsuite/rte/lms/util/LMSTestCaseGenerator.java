package org.adl.testsuite.rte.lms.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.adl.testsuite.rte.lms.testcase.Activity;
import org.adl.testsuite.rte.lms.testcase.LMSTestCase;
import org.adl.util.EnvironmentVariable;

/**
 * TODO
 * 
 * @author ADL Tech Team
 */
public class LMSTestCaseGenerator
{

   /**
    * TODO
    * 
    * @param args
    *           TODO
    */
   public static void main(String[] args)
   {
      LMSTestCaseGenerator gen = new LMSTestCaseGenerator();
      gen.populateHash();
      gen.writeHash();
      // gen.printContents();
   }

   OutputStreamWriter o;

   HashMap testcaseHash = new HashMap();

   String propLocation = EnvironmentVariable.getValue("SCORM4ED_TS111_HOME") + File.separatorChar + "TestSuite" + File.separatorChar + "LMSRTE" + File.separatorChar + "Courses" + File.separatorChar + "TestCases";

   String writeHashToLocation = EnvironmentVariable.getValue("SCORM4ED_TS111_HOME") + File.separatorChar + "build" + File.separatorChar + "classes" + File.separatorChar + "org" + File.separatorChar + "adl" + File.separatorChar + "testsuite" + File.separatorChar + "rte" + File.separatorChar + "lms" + File.separatorChar + "util";

   String hashName = "LMSTestCases.obj";

   /**
    * TODO
    */
   public LMSTestCaseGenerator()
   {
   }

   /**
    * TODO
    */
   public void writeHash()
   {
      ObjectOutputStream oos;

      try
      {
         oos = new ObjectOutputStream(new FileOutputStream(writeHashToLocation + File.separatorChar + hashName));
         oos.writeObject(testcaseHash);
         oos.close();
      }
      catch ( FileNotFoundException fnfe )
      {
         fnfe.printStackTrace();
      }
      catch ( IOException ioe )
      {
         ioe.printStackTrace();
      }
   }

   /**
    * TODO
    */
   public void populateHash() 
   {
      File loc = new File(propLocation);

      if ( loc.isDirectory() )
      {
         // get the list of files and folders in the directory
         File[] files = loc.listFiles();

         // add the files to the list object
         for ( int i = 0; i < files.length; i++ )
         {
            File tc = files[i];
            String tcName = (tc.getName()).substring(0, tc.getName().indexOf("."));

               testcaseHash.put(tcName, createTC(tc));



         }
      }
   }

   /**
    * TODO
    * 
    * @param in
    *           TODO
    * @return tc TODO
    * @throws Exception 
    */
   public LMSTestCase createTC(File in) 
   {
      LMSTestCase tc = null;
      String title = "";
      Map<String, String> instrs = new HashMap<String, String>();
      List<Activity> acts = new ArrayList<Activity>();
      List<String> commands = new ArrayList<String>();
      String uiq = "";
      String uia = "";
      String cui = "";
      String currentActNv = "";
      boolean firstAct = true;
      String act = "";
      String line = "";
      int linecounter = 0;
      try
      {
         BufferedReader file = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(in))));

         while ( (line = file.readLine()) != null )
         {
            linecounter++;
            
            while ( line.charAt(line.length() - 1) == '\\' )
            {
               line = line.substring(0, line.length() - 1);
               line = line.concat(file.readLine());
            }
            
            if ( line.indexOf("####") == -1 )
            {
               String[] pairs = new String[2];
               pairs[0] = line.substring(0, line.indexOf("="));
               pairs[1] = line.substring(line.indexOf("=") + 1, line.length());

               if ( pairs[0].equals("title") )
               {
                  title = pairs[1];
               }
               else if ( (pairs[0].equals("UserInterAct")) || (pairs[0].equals("start")) || (pairs[0].equals("purpose")) || (pairs[0].equals("RTDInit")) || (pairs[0].equals("seqInfo")) || (pairs[0].equals("TT")) || (pairs[0].equals("end")) )
               {
                  if ( !(pairs.length < 2) )
                  {
                     instrs.put(pairs[0], pairs[1]);
                  }
                  else
                  {
                     instrs.put(pairs[0], "");
                  }
               }
               else if ( pairs[0].indexOf("Act") != -1 )
               {
                  String actInfo = pairs[0].substring(0, pairs[0].indexOf('.'));
                  // if act is different from cur act
                  if ( !actInfo.equals(currentActNv) )
                  {
                     if ( !firstAct )
                     {
                        // add new act w/ info
                        act = currentActNv.substring(3, currentActNv.indexOf('V'));
                        acts.add(new Activity(act, uiq, uia, commands, cui));
                        // commands = new commands
                        commands = new ArrayList();
                        // set all act params to ""
                        uiq = "";
                        uia = "";
                        cui = "";
                        // set cur act to act
                        currentActNv = actInfo;
                     }
                     else
                     {
                        firstAct = !firstAct;
                        currentActNv = actInfo;
                     }
                  }
                  if ( pairs[0].indexOf("UIQ") != -1 )
                  {
                     if ( !(pairs.length < 2) )
                     {
                        uiq = pairs[1];
                     }
                     else
                     {
                        uiq = "";
                     }
                  }
                  else if ( pairs[0].indexOf("UIA") != -1 )
                  {
                     if ( !(pairs.length < 2) )
                     {
                        uia = pairs[1];
                     }
                     else
                     {
                        uia = "";
                     }
                  }
                  else if ( pairs[0].indexOf("CUI") != -1 )
                  {
                     if ( !(pairs.length < 2) )
                     {
                        cui = pairs[1];
                     }
                     else
                     {
                        cui = "";
                     }
                  }
                  else if ( pairs[0].indexOf("commands") != -1 )
                  {
                     if ( !(pairs.length < 2) )
                     {
                        commands.add(doUnicode(pairs[1]));
                     }
                     else
                     {
                        commands.add("");
                     }
                  }
               }
            }
         }

         file.close();
      }
      catch ( IOException e )
      {
         System.out.println(e);
         return null;
      }
      catch (StringIndexOutOfBoundsException se)
      {
         throw new RuntimeException(title + ".properties file has an error on line "
               + linecounter + line, se);
      }
      act = currentActNv.substring(3, currentActNv.indexOf('V'));
      acts.add(new Activity(act, uiq, uia, commands, cui));
      tc = new LMSTestCase(title, instrs, acts);
      return tc;
   }

   protected String doUnicode(String iValue)
   {
      int ucLen = 4;
      StringBuffer sb = new StringBuffer();
      char currentChar;

      List prevChars = new ArrayList();

      for ( int i = 0; i < iValue.length(); )
      {
         currentChar = iValue.charAt(i++);
         prevChars.add(new Character(currentChar));
         if ( currentChar == '\\' )
         {
            currentChar = iValue.charAt(i++);
            prevChars.add(new Character(currentChar));
            if ( currentChar == 'u' )
            {
               int newCharHolder = 0;
               boolean valid = true;
               for ( int oo = 0; oo < ucLen; oo++ )
               {
                  currentChar = iValue.charAt(i++);
                  prevChars.add(new Character(currentChar));
                  switch ( currentChar )
                  {
                     case '0':
                     case '1':
                     case '2':
                     case '3':
                     case '4':
                     case '5':
                     case '6':
                     case '7':
                     case '8':
                     case '9':
                        newCharHolder = (newCharHolder << 4) + currentChar - '0';
                        break;
                     case 'a':
                     case 'b':
                     case 'c':
                     case 'd':
                     case 'e':
                     case 'f':
                        newCharHolder = (newCharHolder << 4) + 10 + currentChar - 'a';
                        break;
                     case 'A':
                     case 'B':
                     case 'C':
                     case 'D':
                     case 'E':
                     case 'F':
                        newCharHolder = (newCharHolder << 4) + 10 + currentChar - 'A';
                        break;
                     default:
                        valid = false;
                  }
               }
               if ( valid )
               {
                  sb.append((char) newCharHolder);
               }
               else
               {
                  for ( int a = 0; a < prevChars.size(); a++ )
                     sb.append(((Character) prevChars.get(a)).charValue());
               }
            }
            else
            {
               for ( int w = 0; w < prevChars.size(); w++ )
                  sb.append(((Character) prevChars.get(w)).charValue());
            }
         }
         else
         {
            sb.append(currentChar);
         }

         prevChars = new ArrayList();
      }
      return sb.toString();
   }

   /**
    * TODO
    * 
    * @param s
    *           TODO
    * @throws IOException
    */
   private void println(String s) throws IOException
   {
      o.write(s + "\n");
   }

   /**
    * TODO
    */
   public void printContents()
   {
      try
      {
         o = new OutputStreamWriter(new FileOutputStream("C:\\TCGenPrint.txt"));

         Iterator itr = testcaseHash.keySet().iterator();
         while ( itr.hasNext() )
         {
            /* System.out. */println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
            String tcKey = itr.next().toString();
            /* System.out. */println(tcKey);
            LMSTestCase tc = (LMSTestCase) testcaseHash.get(tcKey);
            /* System.out. */println("title: " + tc.getName());
            /* System.out. */println("UserInterAct: " + tc.getInstructionsUIR());
            /* System.out. */println("start: " + tc.getInstructionsStart());
            /* System.out. */println("purpose: " + tc.getInstructionsPurpose());
            /* System.out. */println("TRDInit: " + tc.getInstructionsRTDInit());
            /* System.out. */println("seqInfo: " + tc.getInstructionsSeqInfo());
            /* System.out. */println("TT: " + tc.getInstructionsTestType());
            /* System.out. */println("end: " + tc.getInstructionsEnd());
            while ( tc.getActivityName() != "" )
            {
               /* System.out. */println("--------------------------------------------------------");
               /* System.out. */println("Act: " + tc.getActivityName());
               /* System.out. */println("UI Q: " + tc.getUIQuestionsKey());
               /* System.out. */println("UI A: " + tc.getUIAnswers());
               Iterator commands = tc.getCommandsKey();
               while ( commands.hasNext() )
               {
                  /* System.out. */println(tc.getName() + " command: " + commands.next().toString());
               }
               /* System.out. */println("Activity CUI: " + tc.getCurrentUserInstructions());
            }
         }
         o.close();
      }
      catch ( Exception e )
      {
         e.printStackTrace();
      }
   }
}
