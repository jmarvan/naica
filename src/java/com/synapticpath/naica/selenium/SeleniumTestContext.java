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
package com.synapticpath.naica.selenium;

import org.openqa.selenium.WebDriver;

import com.synapticpath.naica.TestContext;

/**
 * TestContext to be used for Selenium tests. This subclass also holds Selenium
 * driver and provides it to all that need it.
 * 
 * @author developer@synapticpath.com
 *
 */
public final class SeleniumTestContext extends TestContext {
	
	private WebDriver driver;
	
	/**
	 * Call this when tests are finished.
	 */
	@Override
	public void finalize() {
		super.finalize();
    	driver.close();    	
    }
	
	public WebDriver getDriver() {
		return driver;
	}
	
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	} 
	
	public synchronized static SeleniumTestContext getInstance() {
		
		TestContext instance = TestContext.getInstance();
		if (!(instance instanceof SeleniumTestContext)) {
			
			instance.mergeInstances(new SeleniumTestContext());
		}
		
		return (SeleniumTestContext)TestContext.getInstance();
	}

}
