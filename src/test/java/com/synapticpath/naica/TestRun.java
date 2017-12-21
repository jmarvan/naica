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

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.synapticpath.naica.TestCaseRunner;
import com.synapticpath.naica.TestProperties;
import com.synapticpath.naica.TestSuiteRunner;
import com.synapticpath.naica.ops.Op;
import com.synapticpath.naica.reports.HtmlFileSystemReport;
import com.synapticpath.naica.selenium.SeleniumAction;
import com.synapticpath.naica.selenium.SeleniumCondition;
import com.synapticpath.naica.selenium.SeleniumOp;
import com.synapticpath.naica.selenium.SeleniumSelector;
import com.synapticpath.naica.selenium.SeleniumSnapOp;
import com.synapticpath.naica.selenium.SeleniumTestContext;
import com.synapticpath.naica.selenium.SeleniumTestProperties;

/**
 * Before running this test case for the first time, make sure that the 
 * Selenium web driver is properly configured.
 * 
 * This means running the test with VM argument -Dwebdriver.firefox.marionette=/usr/bin/firefox or
 * webdriver.gecko.driver=/usr/bin/firefox depending on browser version. 
 * 
 * 
 * @author developer@synapticpath.com
 *
 */
public class TestRun {
	
	@Test
	public void runTest() {
		
		TestProperties props = getTestProperties();
		
		FirefoxDriver driver = new FirefoxDriver();
		SeleniumTestContext context = SeleniumTestContext.getInstance();
		context.setDriver(driver);
		context.setProperties(props);
		
		createRunner().run();		
		
		assertFalse(context.isFailed());
	}
	
	
	public TestProperties getTestProperties() {
		
		SeleniumTestProperties props = new SeleniumTestProperties("selenium-tests");
		
		return props;
	}
	
	public TestSuiteRunner createRunner() {
		
		TestCaseRunner caseRunner = new TestCaseRunner("TestCase1", createTestOps());
		
		TestSuiteRunner runner = new TestSuiteRunner(caseRunner);
		runner.setReportGenerators(new HtmlFileSystemReport());
		return runner;
	}
	
	public Op [] createTestOps() {
		
		return new Op [] {				
		
				SeleniumOp.on(SeleniumAction.get("http://synapticpath.com"), SeleniumCondition.url("synapticpath.com"))
				.newStep(true)
				.description("Go to synapticpath.com"),
				
				SeleniumSnapOp.snapOp("products", 
						SeleniumAction.click(SeleniumSelector.byCss("a[href=\"products.html\"]")), 
						SeleniumCondition.url("products.html")
							.onSuccess("Products page is visible."))
					.description("Navigate to products page."),
				
				SeleniumSnapOp.snapOp("services", 
						SeleniumAction.click(SeleniumSelector.byCss("a[href=\"services.html\"]")), 
						SeleniumCondition.url("services.html")
							.onSuccess("Services page is visible."))
					.description("Navigate to services page.")
	
				
				
		};
	}

}
