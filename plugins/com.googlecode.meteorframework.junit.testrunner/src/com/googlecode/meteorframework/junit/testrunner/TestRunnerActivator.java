package com.googlecode.meteorframework.junit.testrunner;

import junit.framework.TestCase;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;


/**
 * When this bundle is started it looks for system properties that 
 * denote the test to run, or the text category to run.
 * 
 * @author Ted Stockwell
 */
public class TestRunnerActivator implements BundleActivator
{
    static public final String TEST_CLASS= "testClass";
    
    @Override
    public void start( final BundleContext context ) throws Exception
    {
        final String testClassName= System.getProperty( TEST_CLASS );
        if (testClassName == null)
            throw new RuntimeException("No test class specified.  Must set Java system property '"+TEST_CLASS+"' to the name of a class that implements "+TestCase.class.getName());
        
        context.addFrameworkListener( new FrameworkListener() {
            @Override
             public void frameworkEvent( FrameworkEvent event )
             {
                 if (FrameworkEvent.STARTED == event.getType())
                 {
                     try {
                         new TestRunner().runTests( context, new String[] { testClassName } );
                     }
                     catch (Throwable t)
                     {
                         t.printStackTrace();
                     }
                 }
             } 
         });
    }

    @Override
    public void stop( BundleContext context ) throws Exception
    {
        // do nothing
    }

}
