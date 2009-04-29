
// Copyright 2008-2009 Emilio Lopez-Gabeiras
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License

package apb.testrunner.output;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import apb.utils.StringUtils;
import apb.utils.XmlUtils;
import apb.utils.StandaloneEnv;
import apb.utils.FileUtils;
import apb.tasks.XsltTask;
import apb.Environment;

import org.jetbrains.annotations.NotNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
//
// User: emilio
// Date: Nov 14, 2008
// Time: 5:08:03 PM

//
public class XmlTestReport
    extends BaseTestReport
{
    //~ Instance fields ......................................................................................

    String ATTR_CLASSNAME = "classname";
    String ATTR_FAILURES = "failures";
    String ATTR_MESSAGE = "message";
    String ATTR_NAME = "name";
    String ATTR_PKG = "package";
    String ATTR_TESTS = "tests";
    String ATTR_TIME = "time";
    String ATTR_TYPE = "type";
    String FAILURE = "failure";
    String HOSTNAME = "hostname";
    String PROPERTIES = "properties";
    String TESTCASE = "testcase";
    String TESTSUITE = "testsuite";
    String TIMESTAMP = "timestamp";

    /**
     * The XML document.
     */
    private Document doc;

    /**
     * tests that failed.
     */
    private Set<String> failedTests = new HashSet<String>();

    /**
     * The wrapper for the whole testsuite.
     */
    private Element rootElement;

    /**
     * Element for the current test.
     */
    private Map<CharSequence, Element> testElements = new HashMap<CharSequence, Element>();

    /**
     * Timing helper.
     */
    private Map<String, Long> testStarts = new HashMap<String, Long>();

    //~ Constructors .........................................................................................

    public XmlTestReport(boolean showOutput, @NotNull String prefix)
    {
        super(showOutput, prefix);
    }

    //~ Methods ..............................................................................................

    public void startSuite(@NotNull String suiteName)
    {
        super.startSuite(suiteName);

        doc = getDocumentBuilder().newDocument();

        rootElement = doc.createElement(TESTSUITE);
        doc.appendChild(rootElement);

        rootElement.setAttribute(ATTR_NAME, suiteName);
        rootElement.setAttribute(ATTR_PKG, suiteName.substring(0, suiteName.lastIndexOf(".")));

        //add the timestamp
        final String timestamp = timestamp();
        rootElement.setAttribute(TIMESTAMP, timestamp);

        //and the hostname.
        rootElement.setAttribute(HOSTNAME, getHostname());

        // Output properties
        Element propsElement = doc.createElement(PROPERTIES);
        rootElement.appendChild(propsElement);
    }

    public void stopRun() {
        super.stopRun();
        XsltTask task = new XsltTask(getEnvironment());
        final File reportFile = new File(reportsDir, "Test-Suites.xml");
        reportFile.delete();
        final File[] files = reportsDir.listFiles(new FileFilter(){
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".xml");
            }
        });

        try {

            FileOutputStream fos = new FileOutputStream(reportFile);

            fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n".getBytes());
            fos.write("<testsuites>\n".getBytes());
            fos.flush();
            fos.close();

            List<FileUtils.Filter> filters = new ArrayList<FileUtils.Filter>();
            filters.add(new FileUtils.Filter(){
                public String filter(String str) {
                    if(str.startsWith("<?xml")){
                    str = "";
                    }
                         return str+"\n";
                }
            });
            
            for (File file : files) {
                FileUtils.copyFileFiltering(file, reportFile, true, "UTF-8", filters);
            }

            fos = new FileOutputStream(reportFile, true);
            fos.write("</testsuites>\n".getBytes());


            fos.flush();
            fos.close();

            final InputStream stylesheet = getClass().getResourceAsStream("/resources/junit-noframes.xsl");
            task.process(reportFile.getAbsolutePath(), stylesheet, new File(reportsDir, "report.html").getAbsolutePath());


        } catch (FileNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }


    private Environment getEnvironment() {
        final StandaloneEnv standaloneEnv = new StandaloneEnv();
        standaloneEnv.setVerbose();
        return standaloneEnv;
    }

    public void endSuite()
    {
        if (suiteOpen) {
            super.endSuite();
            rootElement.setAttribute(ATTR_TESTS, "" + getSuiteTestsRun());
            rootElement.setAttribute(ATTR_FAILURES, "" + getSuiteTestFailures());
            rootElement.setAttribute(ATTR_TIME, "" + (getSuiteTimeEllapsed() / ONE_SECOND));

            writeDocument(doc);
        }
    }

    public void startTest(@NotNull String testName)
    {
        super.startTest(testName);
        testStarts.put(testName, System.currentTimeMillis());
    }

    public void endTest()
    {
        super.endTest();
        final String test = getCurrentTest();

        if (!testStarts.containsKey(test)) {
            startTest(test);
        }

        Element currentTest;

        if (!failedTests.contains(test)) {
            currentTest = doc.createElement(TESTCASE);
            currentTest.setAttribute(ATTR_NAME, test == null ? UNKNOWN : test);
            currentTest.setAttribute(ATTR_CLASSNAME, getCurrentSuite());
            rootElement.appendChild(currentTest);
            testElements.put(test, currentTest);
        }
        else {
            currentTest = testElements.get(test);
        }

        long ellapsed = System.currentTimeMillis() - testStarts.get(test);
        currentTest.setAttribute(ATTR_TIME, "" + (ellapsed / ONE_SECOND));
    }

    public void failure(@NotNull Throwable t)
    {
        super.failure(t);
        formatError(FAILURE, getCurrentTest(), t);
    }

    @NotNull public XmlTestReport init(@NotNull File dir)
    {
        XmlTestReport result = new XmlTestReport(showOutput, fileName);
        result.reportsDir = dir;
        return result;
    }

    protected void printOutput(String title, String content)
    {
        Element nested = doc.createElement(title);
        rootElement.appendChild(nested);
        nested.appendChild(doc.createCDATASection(content));
    }

    private static String timestamp()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateFormat.setLenient(true);
        return dateFormat.format(new Date());
    }

    private static DocumentBuilder getDocumentBuilder()
    {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (Exception exc) {
            throw new ExceptionInInitializerError(exc);
        }
    }

    private String getHostname()
    {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            return "localhost";
        }
    }

    private void formatError(String type, String test, Throwable t)
    {
        if (test != null) {
            endTest();
            failedTests.add(test);
        }

        Element nested = doc.createElement(type);

        Element currentTest = test != null ? testElements.get(test) : rootElement;

        currentTest.appendChild(nested);

        String message = t.getMessage();

        if (message != null && message.length() > 0) {
            nested.setAttribute(ATTR_MESSAGE, t.getMessage());
        }

        nested.setAttribute(ATTR_TYPE, t.getClass().getName());

        String strace = StringUtils.getStackTrace(t);
        Text   trace = doc.createTextNode(strace);
        nested.appendChild(trace);
    }

    private void writeDocument(@NotNull Document document)
    {
        final String suite = getCurrentSuite();

        if (suite != null) {
            XmlUtils.writeDocument(document, reportFile(suite, ".xml"));
        }
    }

    //~ Static fields/initializers ...........................................................................

    private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private static final long serialVersionUID = 7656301463663508457L;

    /** constant for unnnamed testsuites/cases */
    private static final String UNKNOWN = "unknown";
}
