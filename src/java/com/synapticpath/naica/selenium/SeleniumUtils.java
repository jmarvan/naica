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

import static java.lang.String.format;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import com.synapticpath.naica.TestContext;
import com.synapticpath.naica.attachments.SnapshotAttachment;

/**
 * Some utilities to make using Selenium easier. TODO double check whether this
 * is still the case with latest Selenium release.
 * 
 * @author developer@synapticpath.com
 *
 */
public class SeleniumUtils {

	private static final Logger logger = Logger.getLogger(SeleniumUtils.class.getName());
	public static final int MAX_WAIT = 30; // maximum seconds to wait for things
											// to happen
	public static final long POLL_INTERVAL_MILLIS = 100;

	/**
	 * Overloads {@link SeleniumUtils#findElementWithTimeout(SeleniumSelector, Boolean, String, int)}
	 * 
	 * @param elementSelector
	 * @param timeout
	 * @return
	 */
	public static WebElement findElementWithTimeout(final SeleniumSelector elementSelector, int timeout) {

		return findElementWithTimeout(elementSelector, null, null, timeout);
	}

	/**
	 * Call this method to find a WebElement. Several parameters are supported.
	 * 
	 * @param elementSelector
	 *            Will be used as primary criteria to find an element.
	 * @param visible
	 *            When not null, will be evaluated for visibility on page. That is, the desired element must
	 *            also be visible when true, or invisible when false.
	 * @param withText
	 *            if specified, the element.getText must match also.
	 * @param timeout
	 *            positive number of seconds to wait
	 * @return the {@link WebElement} when found, null if not found in timeout reached first.
	 */
	public static WebElement findElementWithTimeout(final SeleniumSelector elementSelector, final Boolean visible,
			final String withText, int timeout) {

		final WebDriver driver = SeleniumTestContext.getInstance().getDriver();

		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(timeout > -1 ? timeout : MAX_WAIT, TimeUnit.SECONDS)
				.pollingEvery(POLL_INTERVAL_MILLIS, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class)
				.ignoring(TimeoutException.class);

		try {
			return wait.until((WebDriver d) -> {
				WebElement e = d.findElement(elementSelector.toBySelector());
					if (e != null && (withText == null || e.getText().contains(withText))
							&& (visible == null || visible && e.isDisplayed() || !visible && !e.isDisplayed())) {
						return e;
					}
					return null;
				}
			);
		} catch (TimeoutException te) {
			logger.severe(format("Element selected by %s, visible:%s, withText:%s was not found in time.", elementSelector, visible, withText));
		}

		return null;

	}

	/**
	 * Polls through Selenium driver until an element matched by given {@link SeleniumSelector} can no longer be found.
	 * 
	 * @param elementSelector
	 * @param timeout
	 *            number of seconds to wait if positive number, otherwise
	 *            default will be applied.
	 * @return true when element disappears, false otherwise
	 */

	public static boolean waitUntilElementGoneWithTimeout(final SeleniumSelector elementSelector, final int timeout) {

		final WebDriver driver = SeleniumTestContext.getInstance().getDriver();

		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(timeout > -1 ? timeout : MAX_WAIT, TimeUnit.SECONDS)
				.pollingEvery(POLL_INTERVAL_MILLIS, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);

		try {
			return wait.until((WebDriver d) -> d.findElements(elementSelector.toBySelector()).size() == 0);

		} catch (TimeoutException te) {
			logger.severe(format("Element selected by %s did not go away in time.", elementSelector.toBySelector()));
		}

		return false;
	}

	/**
	 * Polls through Selenium driver until:
	 * a) browser URL contains desired String
	 * b) timeout is reached
	 * 
	 * @param text
	 * @param timeout
	 * @return true when url contains the text, false if timeout reached.
	 */
	public static boolean waitUntilUrlContains(final String text, int timeout) {

		final WebDriver driver = SeleniumTestContext.getInstance().getDriver();

		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(timeout > -1 ? timeout : MAX_WAIT, TimeUnit.SECONDS)
				.pollingEvery(POLL_INTERVAL_MILLIS, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);

		try {
			return wait.until(ExpectedConditions.urlContains(text));
		} catch (TimeoutException te) {
			logger.severe(format("Url did not contain text %s in time.", text));
		}

		return false;
	}

	/**
	 * When called, makes a screenshot and adds it as attachment to currently executed {@link TestStep}.
	 * 
	 * @param name - the name part of the file that will be created.  Note that it has to be unique within
	 * current step, otherwise one will overwrite another.
	 */
	public static void makeSnapshot(String name) {

		WebDriver driver = SeleniumTestContext.getInstance().getDriver();
		if (driver instanceof TakesScreenshot) {
			try {

				File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				
				SnapshotAttachment snapshot = new SnapshotAttachment(name, FilenameUtils.getExtension(srcFile.getName()));
				TestContext.getInstance().addAttachment(snapshot);

				FileUtils.copyFile(srcFile, snapshot.getFileName().toFile());				

			} catch (Exception e) {
				logger.log(Level.SEVERE, format("Cannot create snapshot %s", name, e));
			}
		} else {
			logger.severe("This selenium driver does not take screenshots!");
		}
	}
	
	public static String getExtension(File file) {
		String fileName=file.getName();		
		if(fileName.contains(".") && fileName.lastIndexOf(".")!= 0) {
			return fileName.substring(fileName.lastIndexOf(".")+1);
		}
		
		return "";
	}

}
