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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.synapticpath.naica.actions.Action;

/**
 * Implementation of Action interface for Selenium.
 * 
 * @author developer@synapticpath.com
 *
 */
public class SeleniumAction implements Action {

    private static final Logger logger = Logger.getLogger(SeleniumAction.class.getName());
    
    private static Function<SeleniumAction, Boolean> clickFn = (SeleniumAction sa) -> {									
		WebElement element = findElementWithTimeout(sa.selector, sa.timeout);
        if (element != null) {
            logger.info(format("Clicking element %s", sa.selector.toString()));
            element.click();
            return true;
        }
        logger.warning(format("Failed to locate element %s", sa.selector.toString()));
        return false;		
	};
	
	private static Function<SeleniumAction, Boolean> urlFn = (SeleniumAction sa) -> {
							
		WebDriver driver = SeleniumTestContext.getInstance().getDriver();
    	
    	logger.info(format("Performing get on %s", sa.text));
    	driver.get(sa.text);
    	
    	//Use @see Condition to evaluate whether this action succeeded.
    	return true;        
	};
	
	private static Function<SeleniumAction, Boolean> noActionFn = (SeleniumAction sa) -> true;
	
    Function<SeleniumAction, Boolean> toPerform;

    protected SeleniumSelector selector;    
    
    protected String text;

    protected int timeout = -1;
    
    //and others.

    protected SeleniumAction(Function<SeleniumAction, Boolean> toPerform) {
    	this.toPerform = toPerform;
    }

    public boolean perform() {    		
       return toPerform.apply(this);
    }
    

    public SeleniumAction timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
    

    /**
     * Make an instance of Action that performs a browser click.
     * 
     * @param selector
     * @return
     */
    public static SeleniumAction click(SeleniumSelector selector) {
        SeleniumAction action = new SeleniumAction(clickFn);
        action.selector = selector;
        return action;
    }

    /**
     * Make an instance of Action that does nothing. Use it as filler when an action is
     * required.
     *  
     * @return
     */
    public static SeleniumAction noAction() {
        SeleniumAction action = new SeleniumAction(noActionFn);        
        return action;
    }
       
    /**
     * Calls Selenium driver to perform a get request on given url.
     * @param url
     * @return
     */
    public static SeleniumAction get(String url) {
        SeleniumAction action = new SeleniumAction(urlFn);
        action.text = url;
        return action;
    }
    
    /**
     * Sends text to a DOM element using underlying driver.
     * @param selector
     * @param text
     * @return
     */
    public static SeleniumTextAction text(SeleniumSelector selector, String text) {
        SeleniumTextAction action = new SeleniumTextAction(selector, text);
        return action;
    }

    /**
     * Clears text in given DOM element.
     * @param selector
     * @return
     */
    public static SeleniumTextAction clear(SeleniumSelector selector) {
        SeleniumTextAction action = new SeleniumTextAction(selector, "");
        return action;
    }
    
}
