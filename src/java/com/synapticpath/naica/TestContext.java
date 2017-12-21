/* Copyright (C) 2017 synapticpath.com - All Rights Reserved

 This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.synapticpath.naica;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.synapticpath.naica.attachments.Attachment;

/**
 * The TestContext class holds the entire structure of a test together.
 * 
 * TestContext encapsulates a 
 * 
 *  TODO: detailed documentaiton 
 * 
 * @author developer@synapticpath.com
 *
 */
public class TestContext {

	//Here is where all testCases that were/are running are stored.
    private Map<String, TestCase> testCases = new HashMap<String, TestCase>();
    
    //TestProperties store configuration information that is needed.
    private TestProperties properties;

    //For convenience reasons, a TestCase that is currently executed is stored here.
    private TestCase currentTestCase;
    
    private static ThreadLocal<TestContext> instance = new ThreadLocal<TestContext>();

    public void stop() {
    	if (currentTestCase != null) {
    		currentTestCase.stop();
    	}
    }
    
    /**
     * Override this method to perform cleanup and other duties after tests are complete.
     */
    public void finalize() {
    	stop();
    }

    /**
     * Obtain the properties that were set.  In them find important information needed for the
     * test to be able to run through.
     * 
     * The 2 most important 
     * 
     * @return
     */
    public TestProperties getProperties() {
        return properties;
    }

    /**
     * Set the TestProperties, you typically want to do this before the test starts. 
     * @param properties
     */
    public void setProperties(TestProperties properties) {
        this.properties = properties;
    }

    /**
     * Get all accumulated TestCases in this {@link TestContext}.
     * @return
     */
    public Collection<TestCase> getTestCases() {
        return testCases.values();
    }

    /**
     * Stops current {@link TestCase} and starts a new one with given id.
     * @param id
     * @return
     */
    public TestCase newTestCase(String id) {
        
        stop();
         
        currentTestCase = testCases.get(id);
        if (currentTestCase == null) {

        	currentTestCase = new TestCase(id);
            testCases.put(id, currentTestCase);
        }

        return currentTestCase;
    }

    /**
     * Current TestCase is null when test is not started yet (there are no TestCases) or the
     * test is already finished - in which case you want to use {@link TestContext#getLastTestCase()}.
     * @return
     */
    public TestCase getCurrentTestCase() {
        return currentTestCase;
    }

    /**
     * A test will have a number of TestCases, call this to get the last one.
     * 
     * @return
     */
    public TestCase getLastTestCase() {
        TestCase last = null;
        for (TestCase testCase :  testCases.values()) {
            last = testCase;
        }

        return last;
    }

    /**
     * Add a new test step to current TestCase while adding a number of action descriptions.
     * @param actions
     */
    public void addStep(String ... actions) {

        if (currentTestCase != null) {
            getCurrentTestCase().addStep();
            if (actions != null) {
                for (String action : actions) {
                    addAction(action);
                }
            }
        }
    }

    /**
     * Adds an action description to current TestCase.  When all actions are printed
     * to a report, they should allow a person to recreate the test by following the action
     * descriptions.
     * 
     * @param description
     */
    public void addAction(String description) {
        if (currentTestCase != null) {
            getCurrentTestCase().addAction(description);
        }
    }

    /**
     * Adds a test result description.  Use this when you want to report an outcome of a test. 
     * @param description
     */
    public void addResult(String description) {
        if (currentTestCase != null) {
            getCurrentTestCase().addResult(description);
        }
    }

    /**
     * Fails current TestCase at current step.
     */
    public void fail() {
        if (currentTestCase != null) {
            getCurrentTestCase().fail();
        }
    }

    /**
     * Call this to find out if the test has failed or not.
     * @return
     */
    public boolean isFailed() {
    	return getOutcome().equals(TestOutcome.FAILURE);       
    }

    /**
     * Add an attachment to current testCase.
     * @param attachment
     */
    public void addAttachment(Attachment attachment) {
        if (currentTestCase != null) {
            getCurrentTestCase().addAttachment(attachment);
        }
    }
    

    /**
     * Get the outcome of the test by analyzing the outcome of each test case.
     * When one testCase fails, the whole test fails, when one testCase ends with
     * {@link TestOutcome#CONDITIONAL_SUCCESS}, the entire test will have {@link TestOutcome#CONDITIONAL_SUCCESS}.
     * 
     * {@link TestOutcome#SUCCESS} will be returned only if every single TestCase ends
     * with the same outcome.
     * 
     * @return
     */
    public TestOutcome getOutcome() {

        boolean conditional = false;
        for (TestCase testCase : getTestCases()) {
            switch (testCase.getOutcome()) {
                //When one of the steps fails, the whole test case fails
                case FAILURE:
                    return TestOutcome.FAILURE;
                case CONDITIONAL_SUCCESS:
                    conditional = true;
                    break;
                case SUCCESS:
                	//Success is good, nothing to do here.
                	break;
            }
        }
        //Failed test never gets here.
        return conditional ? TestOutcome.CONDITIONAL_SUCCESS : TestOutcome.SUCCESS;
    }
    
    /**
     * Since we can only have one instance of TestContext, subclassing a testContext wouldn't
     * be possible. This method will merge this instance of TestContext with another one, thus
     * calling {@link TestContext#getInstance()} will always return relevant instance.
     * 
     * @param testContext
     */
    public synchronized void mergeInstances(TestContext testContext) {
    	
    	TestContext currentInstance = instance.get();
    	if (currentInstance != null) {
    		currentInstance.stop();
    		testContext.testCases.putAll(currentInstance.testCases); 
    	}
    	instance.set(testContext);
    }

    /**
     * TestContext is a singleton, get the single instance of it from ThreadLocal or 
     * create new one if it doesn't exist yet.
     * 
     * @return
     */
    public static synchronized TestContext getInstance() {
    	
    	if (instance.get() == null) {    	
    		instance.set(new TestContext());
    	}
    	
    	return instance.get();
    }

}
