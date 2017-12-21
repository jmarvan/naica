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
import static com.synapticpath.naica.selenium.SeleniumUtils.waitUntilElementGoneWithTimeout;
import static com.synapticpath.naica.selenium.SeleniumUtils.waitUntilUrlContains;
import static java.lang.String.format;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

import org.openqa.selenium.WebElement;

import com.synapticpath.naica.TestContext;
import com.synapticpath.naica.TestStep;
import com.synapticpath.naica.conditions.Condition;

/**
 * Implementation of {@link Condition} for Selenium.
 * 
 * @author developer@synapticpath.com
 *
 */
public class SeleniumCondition implements Condition {

    private static final Logger logger = Logger.getLogger(SeleniumCondition.class.getName());
    
    
	private static Function<SeleniumCondition, Boolean> existsFn = (SeleniumCondition c) -> {
							
		boolean result = c.evaluateElementExists(c.selector);
		if (!result) {
			String inversion = c.invertCondition ? "!" : "";
			logger.warning(format("Condition %selementExists %s returns negative result.", inversion,	c.selector.toBySelector()));
		}
		return result;
	};

	private static Function<SeleniumCondition, Boolean> visibleFn = (SeleniumCondition c) -> {

		boolean result = c.evaluateElementVisible(c.selector);
		if (!result) {
			String inversion = c.invertCondition ? "!" : "";
			logger.warning(format("Condition %selementVisible %s returns negative result.", inversion, c.selector.toBySelector()));
			return false;
		}
		
		return true;
	};
	
	private static Function<SeleniumCondition, Boolean> textFn = (SeleniumCondition c) -> {

		boolean result =  c.evaluateElementText(c.selector, c.text);
		if (!result) {
			String inversion = c.invertCondition ? "!" : "";
			logger.warning(format("Condition %selementText %s returns negative result.", inversion, c.selector.toBySelector()));
			return false;
		}
		
		return true;
	};
	
	
	private static Function<SeleniumCondition, Boolean> urlFn = (SeleniumCondition c) -> {
		
		boolean result = waitUntilUrlContains(c.text, c.timeout);
		if (!result) {
			logger.warning(format("Condition urlContains (%s) returns negative result.", c.text));
		}
		return result;
	};
	
	private Function<SeleniumCondition, Boolean> toEvaluate;
	
	private SeleniumSelector selector;

    private String text;

    private boolean invertCondition;

    private int timeout = -1;
    
    protected Set<String> onSuccess;
    
    protected Set<String> onFailure;

    //and others.

    private SeleniumCondition(Function<SeleniumCondition, Boolean> fn) {
    	this.toEvaluate = fn;
    }

    public boolean evaluate() {
    	
    	boolean result = toEvaluate.apply(this);
    	
    	if (result) {
    		processResultSuccess();
    	} else {
    		processResultFailure();
    	}    	
    	
    	return result;
    }
    
    protected void processResultSuccess() {
    	
    	if (onSuccess != null) {
    		onSuccess.forEach((String text) -> TestContext.getInstance().addResult(text));
    	}
    	
    }

    protected void processResultFailure() {
        TestContext.getInstance().fail();
        
        if (onFailure != null) {
    		onFailure.forEach((String text) -> TestContext.getInstance().addResult(text));
    	}
        
    }


    private boolean evaluateElementExists(final SeleniumSelector elementSelector) {

        if (invertCondition) {
            return waitUntilElementGoneWithTimeout(elementSelector, timeout);
        }
        return findElementWithTimeout(elementSelector, timeout) != null;
    }
    
    private boolean evaluateElementVisible(final SeleniumSelector elementSelector) {

        WebElement element = findElementWithTimeout(elementSelector, !invertCondition, null, timeout);
        return element == null && !invertCondition || element != null && invertCondition;
					
    }

    private boolean evaluateElementText(final SeleniumSelector elementSelector, final String text) {

        return findElementWithTimeout(elementSelector, null, text, timeout) != null;
    }

    /**
     * Timeout is always in seconds.
     * @param timeout
     * @return
     */
    public SeleniumCondition timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
    
    /**
     * Add one or more lines of text that will be added to {@link TestStep#addResult(String)} when
     * this Condition is executed successfully.
     *  
     * @param text
     * @return
     */
    public SeleniumCondition onSuccess(String ... text) {
    	if (onSuccess == null) {
    		onSuccess = new LinkedHashSet<>(); 
    	}
    	onSuccess.addAll(Arrays.asList(text));  
        return this;
    }
    
    /**
     * Add one or more lines of text that will be added to {@link TestStep#addResult(String)} when
     * this {@link Condition} is not executed successfully.
     *  
     * @param text
     * @return
     */
    public SeleniumCondition onFailure(String ... text) {
    	if (onFailure == null) {
    		onFailure = new LinkedHashSet<>(); 
    	}
    	onFailure.addAll(Arrays.asList(text));  
        return this;
    }

    /**
     * Create a {@link Condition} instance that tests for existence of a DOM element.
     * 
     * @param selector
     * @return
     */
    public static SeleniumCondition exists(SeleniumSelector selector) {
        SeleniumCondition resultCondition = new SeleniumCondition(existsFn);
        resultCondition.selector = selector;
        return resultCondition;
    }

    /**
     * Create a {@link Condition} instance that tests that DOM element does not exist.
     * 
     * @param selector
     * @return
     */
    public static SeleniumCondition notexists(SeleniumSelector selector) {
        SeleniumCondition resultCondition = exists(selector);
        resultCondition.invertCondition = true;
        return resultCondition;
    }

    /**
     * Creates a {@link Condition} instance that tests if a DOM element is visible.
     * NOTE: An element that doesn't exist cannot be visible.
     * 
     * @param selector
     * @return
     */
    public static SeleniumCondition visible(SeleniumSelector selector) {
        SeleniumCondition resultCondition = new SeleniumCondition(visibleFn);
        resultCondition.selector = selector;
        return resultCondition;
    }

    /**
     * Creates a {@link Condition} instance that tests if a given DOM element is invisible.
     * 
     * @param selector
     * @return
     */
    public static SeleniumCondition invisible(SeleniumSelector selector) {
        SeleniumCondition resultCondition = visible(selector);
        resultCondition.invertCondition = true;
        return resultCondition;
    }

    /**
     * Creates a {@link Condition} instance that tests if browser url contains given text as substring.
     * 
     * @param url
     * @return
     */
    public static SeleniumCondition url(String url) {
        SeleniumCondition resultCondition = new SeleniumCondition(urlFn);
        resultCondition.text = url;
        return resultCondition;
    }

    /**
     * Creates a {@link Condition} that tests if a DOM element described by given selector contains
     * given text.
     * 
     * @param selector
     * @param text
     * @return
     */
    public static SeleniumCondition text(SeleniumSelector selector, String text) {
        SeleniumCondition resultCondition = new SeleniumCondition(textFn);
        resultCondition.selector = selector;
        resultCondition.text = text;
        return resultCondition;
    }
}
