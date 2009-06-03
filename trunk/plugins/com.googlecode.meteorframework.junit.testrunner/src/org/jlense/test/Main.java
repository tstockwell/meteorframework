package org.jlense.test;

import com.rpc.core.utils.CoreApplication;


/**
 * Runs the test runner. This runner extends CoreApplication. This makes it
 * possible for other plugins to register components that can be started when
 * the test runner is started. Third party plugins can use this mechanism to
 * configure the test environment before tests are run.
 * 
 * @see the com.rpc.core.executableComponents and
 *      com.rpc.core.componentExtensions extension points
 * 
 * @author ted stockwell
 */
public class Main extends CoreApplication {

    /*
     * (non-Javadoc)
     * 
     * @see com.rpc.core.utils.CoreApplication#runApplication(java.lang.Object)
     */
    public Object runApplication(Object args) throws Exception {
        TestRunner runner = new TestRunner();
        return runner.run(args);
    }

}
