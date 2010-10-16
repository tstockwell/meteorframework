package com.googlecode.meteorframework.junit.testrunner;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * A test extension definition.
 * 
 * @author ted stockwell
 */
public class TestDescriptor {
    private IConfigurationElement _configurationElement;
    private String _id;
    private String _className;

    TestDescriptor(IConfigurationElement configurationElement) {
        _configurationElement = configurationElement;

        _id = _configurationElement.getAttribute("id");

        _className = _configurationElement.getAttribute("class");
        if (_className == null)
            _className = _configurationElement.getChildren("run")[0].getAttribute("class");

        if (_id == null) {
            String name = _configurationElement.getAttribute("id");
            if (name != null) {
                // this is an old style test extension, use the extension id
                _id = _configurationElement.getDeclaringExtension().getUniqueIdentifier();
            } else
                _id = _className;
        }
    }

    protected IConfigurationElement getConfigurationElement() {
        return _configurationElement;
    }

    protected String getId() {
        return _id;
    }

    // TestCase createTestCase() {
    // TestCase testCase= null;
    // IConfigurationElement[] runs= _configurationElement.getChildren("run");
    // if (runs == null || runs.length <= 0) {
    // // test case is not also an executable extension
    // testCase=
    // }
    // if ()
    // }
}
