SCORM 2004 4th Edition Test Suite
=================================
The SCORM 2004 4th Edition Test Suite is a collection of tests designed to 
valitate software conforming to the SCORM 2004 4th Edtion. It includes a 
Learning Management System (LMS) Test, Content Package Test, SCO Test, and 
Manifest Test, along with a utility test to verify a content package matches 
a Content Package Test log.  

This repository is made available to allow developers to update the source 
to work in their specific environments.

## Building
### Prerequisites  
- Java 6 update 10 Java Development Kit (JDK)  
- Apache Ant 1.7.1  (Instructions [here](http://ant.apache.org/manual/install.html))

### Configuration
- Set JAVA_HOME environment variable to the root folder of Java's JDK  
	- ex: C:\Program Files\Java\jdk1.6.0_31  
	- To enable running the java compiler from any folder, add %JAVA_HOME%\bin to the system path  
- Set ANT_HOME environment variable to the root folder of Apache Ant  
	- ex: C:\apache-ant-1.7.1 
	- To enable running the ant command from any folder, add %ANT_HOME%\bin to the system path	
- Set SCORM4ED_TS111_HOME environment variable to the Test Suite folder containing the ant build scripts  
	- ex: C:\dev\SCORM-2004-4ed-Test-Suite\  
- Include a code signing certificate (keystore) in the `software_development` folder  
- Set the `build.properties` values for the code signing certificate  
```
<!-- signing certificate values -->
keystore=<!-- path to keystore -->
keystoreAlias=
keystorePassword=
```

### Building
The Test Suite uses Apache Ant build scripts. To initiate the build, navigate to the 
folder containing the Ant build scripts in a terminal and execute the following command:  
```
C:\dev\SCORM-2004-4ed-Test-Suite\software_development > ant
```

## Running
### Prerequisites
- Internet Explorer 8 (Or a lesser version)  
- Java 6 update 10 Runtime Environment (JRE)  

### Launching the Test Suite
- Navigate to the TestSuite folder 
	- ex: C:\dev\SCORM-2004-4ed-Test-Suite\software_development\TestSuite\  
- Double click main.htm  

### Test Suite Readme
There is a Readme for operating the Test Suite found at `C:\dev\SCORM-2004-4ed-Test-Suite\software_development\CTS_Readme`  
  
  
## License
   Copyright 2014 Advanced Distributed Learning

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
