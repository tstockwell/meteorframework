package org.jlense.test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.runner.TestSuiteLoader;
import junit.runner.Version;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

/**
 * Runs JUnit test cases. When running JLense test cases it is important that
 * they be run inside the Eclipse runtime environment. This so that the tests
 * will be executed in the same runtime environment as regular JLense code. For
 * instance, classloading is one of the biggest sources of problems in the
 * Eclipse environment, and classloading problems will only be detected if tests
 * are run inside Eclipse.
 * 
 * This class works by running testcase using the classloader associated with
 * the plugin that declares the testcase.
 * 
 * USAGE If no command line arguments are supplied when this class is run then
 * all registered testcases are run otherwise the Eclipse Id of a testcase may
 * be specified and just that testcase is run.
 * 
 * @author ted stockwell [emorning@sourceforge.net]
 */
public class TestRunner extends junit.textui.TestRunner implements TestListener {
    public static final int SUCCESS = 0;
    /**
     * Some tests failed.
     */
    public static final int FAILURES = 1;
    /**
     * An error occured.
     */
    public static final int ERRORS = 2;

    private TestResult _testResult;
    private ArrayList _formatters = new ArrayList();
    private JUnitTest _junitTest;
    private boolean _haltOnError = false;
    private boolean _haltOnFailure = false;

    /**
     * The test category to execute
     */
    private String _categoryId = null;

    private HashMap _testsByCategoryId = new HashMap();


    public TestRunner() {
        _testResult = createTestResult();
    }

    ArrayList _classLoaders = new ArrayList();
    HashMap _classes = new HashMap();
    TestSuiteLoader _loader = new TestSuiteLoader() {
        public Class load(String suiteClassName) throws ClassNotFoundException {
            Class klass = (Class) _classes.get(suiteClassName);
            if (klass != null)
                return klass;
            for (Iterator iloader = _classLoaders.iterator(); iloader.hasNext();) {
                ClassLoader loader = (ClassLoader) iloader.next();
                try {
                    klass = loader.loadClass(suiteClassName);
                    if (klass != null)
                        return klass;
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            throw new ClassNotFoundException(suiteClassName);
        }

        public Class reload(Class aClass) throws ClassNotFoundException {
            return aClass;
        }
    };

    /**
     * Runs this runnable with the given args and returns a result. The content
     * of the args is unchecked and should conform to the expectations of the
     * runnable being invoked. Typically this is a <code>String<code> array.
     *
     * @exception Exception if there is a problem running this runnable.
     */
    public Object run(Object arg) throws Exception {
        FileOutputStream fileOutputStream = null;
        ArrayList testIds = new ArrayList(); // list of test ids to execute

        try {

            String[] args = new String[0];
            if (arg != null && arg instanceof String[])
                args = (String[]) arg;

            String testId = null;
            boolean wait = false;

            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-wait"))
                    wait = true;
                else if (args[i].equals("-c"))
                    testId = extractClassName(args[++i]);
                else if (args[i].equals("-haltOnError"))
                    _haltOnError = true;
                else if (args[i].equals("-haltOnFailure"))
                    _haltOnFailure = true;
                else if (args[i].equals("-v"))
                    System.err.println("JUnit " + Version.id() + " by Kent Beck and Erich Gamma");
                else if (args[i].equals("-category"))
                    _categoryId = args[++i];
                else
                    testIds.add(args[i]);
            }

            TestSuite allTests = new TestSuite();

            // set up junit test
            _junitTest = new JUnitTest(allTests.getClass().getName());
            // Hashtable props= new Hashtable();
            // Hashtable p= System.getProperties();
            // for (Enumeration enum = p.keys(); enum.hasMoreElements(); ) {
            // Object key = enum.nextElement();
            // props.put(key, p.get(key));
            // }
            // _junitTest.setProperties(props);

            // set up output formatters
            // in the future we could get formatters from an extension point
            PlainJUnitResultFormatter plainFormatter = new PlainJUnitResultFormatter();
            plainFormatter.setOutput(System.out);
            addFormatter(plainFormatter);
            XMLJUnitResultFormatter xmlFormatter = new XMLJUnitResultFormatter();
            IPath logPath = Platform.getStateLocation(Platform.getBundle("org.jlense.test"));
            logPath = logPath.append(".testResults.xml");
            logPath = logPath.makeAbsolute();
            fileOutputStream = new FileOutputStream(logPath.toString());
            xmlFormatter.setOutput(fileOutputStream);
            addFormatter(xmlFormatter);

            // ///////////////////
            //
            // iterate through the extenstion point registry and
            // run the requested test, or all tests if no test is specified
            //
            IExtensionRegistry registry = Platform.getExtensionRegistry();
            IExtensionPoint extensionPoint = registry.getExtensionPoint("org.jlense.test.testCases");
            if (extensionPoint == null) {
                if (testId != null)
                    throw new Exception("Test not found:" + testId);
                return null;
            }
            IExtension[] extensions = extensionPoint.getExtensions();
            if (extensions == null) {
                if (testId != null)
                    throw new Exception("Test not found:" + testId);
                return null;
            }

            /*
             * If we are running a category of tests then populate the category
             * maps first.
             */
            if (_categoryId != null) {
                populateCategories(extensions);
                List ids = (List) _testsByCategoryId.get(_categoryId);
                if (ids == null)
                    throw new Exception("No such category id: " + _categoryId);
                testIds.addAll(ids);
            }

            for (int i = 0; i < extensions.length; i++) {
                IExtension iExtension = extensions[i];
                IConfigurationElement[] configurationElements = iExtension.getConfigurationElements();

                /*
                 * Add the plugin classloader to our list of classloaders
                 */
                ClassLoader classLoader = iExtension.getDeclaringPluginDescriptor().getPluginClassLoader();
                if (!_classLoaders.contains(classLoader))
                    _classLoaders.add(classLoader);

                for (int iconf = 0; iconf < configurationElements.length; iconf++) {
                    IConfigurationElement element = configurationElements[iconf];
                    if (element.getName().equals("test")) {

                        TestDescriptor descriptor = new TestDescriptor(element);

                        if (testIds.contains(descriptor.getId()) == false)
                            continue;

                        String className = element.getAttribute("class");
                        Test suite = getTest(className);
                        configureTest(suite, element);
                        allTests.addTest(suite);
                    }
                }
            }

            if (testId != null && testId.length() <= 0 && allTests.testCount() <= 0) {
                throw new Exception("Test not found:" + testId);
            }

            doRun(allTests, wait);

            return null;
        } finally {
            if (fileOutputStream != null)
                fileOutputStream.close();
        }
    }

    /*
     * fills the _testsByCategoryId map with lists of test ids for each category
     */
    void populateCategories(IExtension[] extensions) throws CoreException {
        for (int i = 0; i < extensions.length; i++) {
            IExtension iExtension = extensions[i];
            IConfigurationElement[] configurationElements = iExtension.getConfigurationElements();

            for (int iconf = 0; iconf < configurationElements.length; iconf++) {
                IConfigurationElement element = configurationElements[iconf];

                if (element.getName().equals("category")) {
                    String categoryId = element.getAttribute("id");
                    if (categoryId == null)
                        EclipseUtils.throwCoreException(element, "category element is missing id attribute");
                    _testsByCategoryId.put(categoryId, new ArrayList());
                }
            }
        }
        for (int i = 0; i < extensions.length; i++) {
            IExtension iExtension = extensions[i];
            IConfigurationElement[] configurationElements = iExtension.getConfigurationElements();

            for (int iconf = 0; iconf < configurationElements.length; iconf++) {
                IConfigurationElement element = configurationElements[iconf];

                if (element.getName().equals("categoryExtension")) {

                    String categoryId = element.getAttribute("categoryId");
                    if (categoryId == null)
                        EclipseUtils.throwCoreException(element, "categoryExtension element is missing categoryId attribute");

                    String testId = element.getAttribute("testId");
                    if (testId == null)
                        EclipseUtils.throwCoreException(element, "categoryExtension element is missing testId attribute");

                    ArrayList testList = (ArrayList) _testsByCategoryId.get(categoryId);
                    if (testList == null)
                        EclipseUtils.throwCoreException(element, "No such category id: " + categoryId);

                    testList.add(testId);
                }

                if (element.getName().equals("test")) {

                    TestDescriptor testDescriptor = new TestDescriptor(element);

                    IConfigurationElement[] categoryExtensionElements = element.getChildren("categoryExtension");
                    for (int j = 0; j < categoryExtensionElements.length; j++) {
                        IConfigurationElement categoryExtensionElement = categoryExtensionElements[j];

                        String categoryId = categoryExtensionElement.getAttribute("categoryId");
                        if (categoryId == null)
                            EclipseUtils.throwCoreException(element,
                                    "categoryExtension element is missing categoryId attribute");

                        ArrayList testList = (ArrayList) _testsByCategoryId.get(categoryId);
                        if (testList == null)
                            EclipseUtils.throwCoreException(element, "No such category id: " + categoryId);

                        testList.add(testDescriptor.getId());
                    }
                }
            }
        }
    }

    public TestResult doRun(Test suite, boolean wait) {
        _testResult.addListener(this);
        for (Iterator i = _formatters.iterator(); i.hasNext();)
            _testResult.addListener((TestListener) i.next());

        long start = System.currentTimeMillis();
        fireStartTestSuite();

        ByteArrayOutputStream errStrm = new ByteArrayOutputStream();
        PrintStream fSystemError = new PrintStream(errStrm);

        ByteArrayOutputStream outStrm = new ByteArrayOutputStream();
        PrintStream fSystemOut = new PrintStream(outStrm);

        try {
            suite.run(_testResult);
        } finally {
            fSystemError.close();
            fSystemError = null;
            fSystemOut.close();
            fSystemOut = null;
            sendOutAndErr(new String(outStrm.toByteArray()), new String(errStrm.toByteArray()));
            _junitTest.setCounts(_testResult.runCount(), _testResult.failureCount(), _testResult.errorCount());
            _junitTest.setRunTime(System.currentTimeMillis() - start);
        }

        fireEndTestSuite();

        pause(wait);
        return _testResult;
    }

    private void sendOutAndErr(String out, String err) {
        for (Iterator i = _formatters.iterator(); i.hasNext();) {
            JUnitResultFormatter formatter = (JUnitResultFormatter) i.next();

            formatter.setSystemOutput(out);
            formatter.setSystemError(err);
        }
    }

    private void fireStartTestSuite() {
        for (Iterator i = _formatters.iterator(); i.hasNext();)
            ((JUnitResultFormatter) i.next()).startTestSuite(_junitTest);
    }

    private void fireEndTestSuite() {
        for (Iterator i = _formatters.iterator(); i.hasNext();)
            ((JUnitResultFormatter) i.next()).endTestSuite(_junitTest);
    }

    public void addFormatter(JUnitResultFormatter f) {
        _formatters.add(f);
    }

    /*
     * @see TestListener.addFailure
     */
    public void startTest(Test t) {
    }

    /*
     * @see TestListener.addFailure
     */
    public void endTest(Test test) {
    }

    /*
     * @see TestListener.addFailure
     */
    public void addFailure(Test test, AssertionFailedError t) {
        if (_haltOnFailure) {
            _testResult.stop();
        }
    }

    /*
     * @see TestListener.addError
     */
    public void addError(Test test, Throwable t) {
        if (_haltOnError) {
            _testResult.stop();
        }
    }

    public TestSuiteLoader getLoader() {
        return _loader;
    }

    public void configureTest(Test suite, IConfigurationElement element) throws CoreException {
        if (suite instanceof IExecutableExtension) {
            IConfigurationElement[] parms = element.getChildren("parameter"); //$NON-NLS-1$
            Hashtable initParms = new Hashtable();
            if (parms != null) {
                for (int i = 0; i < parms.length; i++) {
                    String pname = parms[i].getAttribute("name"); //$NON-NLS-1$
                    if (pname != null)
                        initParms.put(pname, parms[i].getAttribute("value")); //$NON-NLS-1$
                }
            }

            ((IExecutableExtension) suite).setInitializationData(element, null, initParms);
        }

        if (suite instanceof TestSuite) {
            TestSuite suite2 = (TestSuite) suite;
            for (Enumeration e = suite2.tests(); e.hasMoreElements();) {
                Test test = (Test) e.nextElement();
                configureTest(test, element);
            }
        }
    }

}
