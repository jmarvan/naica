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

import static com.synapticpath.naica.selenium.SeleniumUtils.findElementWithTimeout;
import static java.lang.String.format;

import java.util.function.Function;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Locates DOM element through the Selenium driver, attempts to send keys to it with various options.
 * 
 * @author developer@synapticpath.com
 *
 */
public class SeleniumTextAction extends SeleniumAction {

    private static final Logger logger = Logger.getLogger(SeleniumAction.class.getName());    
    
    private static Function<SeleniumAction, Boolean> textFn = (SeleniumAction sa) -> {    
		
		SeleniumTextAction sta = (SeleniumTextAction)sa;
		
		WebElement element = findElementWithTimeout(sta.selector, sta.timeout);
        if (element != null) {

            if (sta.overwrite) {
                element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            }

            if (sta.text == null || sta.text.isEmpty()) {
                logger.info(format("Clearing text of element %s", sta.selector.toBySelector()));
                element.clear();
            } else {
                logger.info(format("Sending text '%s' to element %s", sta.text, sta.selector.toBySelector()));
                element.sendKeys(sta.text);
            }

            if (sta.sendEnter) {
               element.sendKeys(Keys.RETURN);
            }

            sta.evaluateBlur(element);
            return true;
        }
        logger.info(format("Error locating element by %s", sta.selector.toBySelector()));
        return false;
	};

    protected boolean overwrite;

    protected boolean blurByTab = false;
    
    protected boolean blurByClick = false;
    
    protected boolean blurByJavascript = false;

    protected boolean sendEnter = false;

    protected SeleniumTextAction(SeleniumSelector selector, String text) {
        super(textFn);
        this.selector = selector;
        this.text = text;
    }
    
    /**
     * Several methods are offered because each one can be suitable for different situation.
     * 
     * @param element
     */
    private void evaluateBlur(WebElement element) {
    	 if (blurByClick) {
         	doBlurByClick(element);
         } else if (blurByTab) {
         	doBlurByTab(element);
         } else if (blurByJavascript) {
         	doBlurByJavascript(element);
         }

    }

    public SeleniumTextAction timeout(int timeout) {
    	super.timeout(timeout);
        return this;
    }

    /**
     * Clears previous text of selected element before sending new text to it.
     * 
     * @param overwrite
     * @return
     */
    public SeleniumTextAction overwrite(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }

    /**
     * After text is written to selected element, click outside of the element to cause blur.
     * Multiple blur versions
     * 
     * @param blur
     * @return
     */
    public SeleniumTextAction blurByClick(boolean blur) {
        this.blurByClick = blur;
        if (blur) {
        	this.blurByJavascript = false;
        	this.blurByTab = false;
        }
        return this;
    }
    
    /**
     * After text is written to selected element, send tabulator to leave the element in order to trigger blur.
     * 
     * @param blur
     * @return
     */
    public SeleniumTextAction blurByTab(boolean blur) {
        this.blurByTab = blur;
        if (blur) {
        	this.blurByJavascript = false;
        	this.blurByClick = false;
        }
        return this;
    }
    
    /**
     * After text is written to selected element, blur the element by sending javascript blur event.
     * 
     * @param blur
     * @return
     */
    public SeleniumTextAction blurByJavascript(boolean blur) {
        this.blurByJavascript = blur;
        if (blur) {
        	this.blurByTab = false;
        	this.blurByClick = false;
        }
        return this;
    }
    

    /**
     * After a text is written to selected element, finish with an enter key.
     * 
     * @param sendEnter
     * @return
     */
    public SeleniumTextAction sendEnter(boolean sendEnter) {
        this.sendEnter = sendEnter;
        return this;
    }  

    public static void doBlurByJavascript(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) SeleniumTestContext.getInstance().getDriver();
        js.executeScript("arguments[0].blur(); return true", element);
    }
    
    public static void doBlurByTab(WebElement element) {
    	element.sendKeys(Keys.TAB);
    }
    
    public static void doBlurByClick(WebElement element) {
    	element.findElement(By.xpath("..")).click();
    }

}
