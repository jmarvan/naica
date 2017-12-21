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

import java.util.Iterator;

import org.openqa.selenium.WebDriver;

import com.synapticpath.naica.actions.Action;
import com.synapticpath.naica.conditions.Condition;
import com.synapticpath.naica.ops.BaseOp;

/**
 * This is an implementation of {@link Op} for Selenium.
 * 
 * @author developer@synapticpath.com
 *
 */
public class SeleniumOp extends BaseOp {

    
    protected SeleniumOp(Action [] actions, Condition [] resultConditions) {
    	super(actions, resultConditions);
    }

    protected void processActionSuccess(Action action) {
    	WebDriver driver = SeleniumTestContext.getInstance().getDriver();
    	Iterator<String> iter = driver.getWindowHandles().iterator();
        String firstHandle = iter.next();
        String lastHandle = firstHandle;
        while (iter.hasNext()) {
            lastHandle = iter.next();
        }
        if (!lastHandle.equals(firstHandle)) {
            driver.switchTo().window(lastHandle);
        }
    }    	    

    /**
     * Add one or more actions to this Op to execute.
     */
	@Override
	public SeleniumOp action(Action ... actions) {
		super.action(actions);
		return this;
	}

	/**
	 * Add one or more {@link Condition}s to evaluate to validate execution.
	 */
	@Override
	public SeleniumOp condition(Condition ... conditions) {
		super.condition(conditions);
		return this;
	}

	/**
	 * Create a new {@link TestStep} before execution of this Op starts.
	 */
	@Override
	public SeleniumOp newStep(boolean newStep) {		
		super.newStep(newStep);
		return this;
	}

	/**
	 * Add one or more action descriptions to current TestStep when executed.
	 */
	@Override
	public SeleniumOp description(String ... actionDescriptions) {
		super.description(actionDescriptions);
		return this;
	}
	
	
	/**
	 * Set text(s) to add to TestStep results when execution of this Operation succeeds. 
	 */
	@Override
	public SeleniumOp onSuccess(String ... text) {
		super.onSuccess(text);
		return this;
	}

	/**
	 * Set text(s) to add to TestStep results when execution of this Operation fails. 
	 * 
	 * 
	 */
	@Override
	public SeleniumOp onFailure(String ... text) {		
		super.onFailure(text);
		return this;
	} 
	
	/**
	 * Create Op (Operation) instance given an {@link Action} to perform and one or more
	 * {@link Condition}s to evaluate.
	 * @param action
	 * @param resultCondition
	 * @return
	 */
	public static SeleniumOp on(Action action, Condition ... resultCondition) {
        return new SeleniumOp(new Action [] {action}, resultCondition);
    }
   
}
