package org.adl.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.adl.util.EnvironmentVariable;


public class createPrintableReadmeCTS
{     
   public createPrintableReadmeCTS()
   {                
   } 
   public void startCopy()
   {
      String CTSReadmeLocation = EnvironmentVariable.getValue("SCORM4ED_TS111_HOME")
      + File.separatorChar + "TestSuite"
      + File.separatorChar + "CTS_Readme" + File.separatorChar ;
   
      // CTS files
      String[] filesToAppend = {"toc.html","ctsIntroduction.html","installInstructions.html",
            "revisionsToCTS.html","runOverview.html","runLMS.html","runCP.html","runSCO.html",
            "runManifest.html","runChecksum.html", "knownIssuesCTS.html","licenseCopyright.html"};
      
      BufferedReader in;
      BufferedWriter out;
     
      try
      {
          // Open an input stream from the header html file
          in = new BufferedReader(new InputStreamReader(new FileInputStream(CTSReadmeLocation + "printableHeader.html")));
                   
          // create the output stream writer to the readme file
          out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CTSReadmeLocation + "printReadmeCTS.html",false)));
          
          String q = in.readLine();          
          while(q!=null)
          {            
             // mark the file at each line, if the end body tag is found this will be directly before it
             // can read 1000 characters while keeping the mark.
             in.mark(1000);      
             // if q contains "<body" stop writing to output file
             if ( q.indexOf("</body>") != -1)
             {
                // reset to the mark and append the body contents from each other file here.
                in.reset();
                
                // start looping through files, call copyBody(output filestream, string filename)
                for(int i=0; i<filesToAppend.length;i++)
                {
                   System.out.println(filesToAppend[i]);
                   copyBody(out, filesToAppend[i], CTSReadmeLocation );
                }
             }
             
             out.write(q);
             out.newLine();
             
             q = in.readLine();
          }
          // Close our streams
          in.close();     
          out.close();
      }
      // Catches any error conditions
      catch (IOException e)
      {
         System.err.println ("Unable to read from file " + e);
         e.printStackTrace();
         System.exit(-1);
      }      
   }    
   
   public void copyBody(BufferedWriter out, String file, String fileLoc)
   {
      boolean inBody = false;
      String line;
      
      try {
         FileInputStream fis = new FileInputStream(fileLoc + file);
         //System.out.println("File: " + fis.toString());
         BufferedReader reader = new BufferedReader ( new InputStreamReader(fis));
         
         out.write("<div class=\"bg\" id=\"" + file + "\">");
         out.newLine();
         
         while((line = reader.readLine()) != null) 
         {
            if (line.indexOf("<body") != -1)
            {
               //skip that line
               line = reader.readLine();
               inBody = true;               
            }
            else if(line.indexOf("</body>") != -1)
            {
               inBody = false;
            }            
            if(inBody)
            {
               out.write(line);
               out.newLine();
            }            
         }
         
         reader.close();
         fis.close();
         
         out.write("</div>");
         out.newLine();        
      }
      catch (Throwable e) 
      {
        e.printStackTrace();
        System.exit(-1);
      }
   }
   
   /**
    * @param args
    */
   public static void main(String[] args)
   {
      createPrintableReadmeCTS cpr = new createPrintableReadmeCTS();
      cpr.startCopy(); 
   }

}
