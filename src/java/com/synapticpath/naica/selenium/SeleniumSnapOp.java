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

import static com.synapticpath.naica.selenium.SeleniumUtils.makeSnapshot;

import com.synapticpath.naica.actions.Action;
import com.synapticpath.naica.conditions.Condition;

/**
 * Performs standard operation, but creates screenshots when processing Results.
 * 
 * @author developer@synapticpath.com
 *
 */
public class SeleniumSnapOp extends SeleniumOp {

    private String actionName;

    protected SeleniumSnapOp(String actionName, Action action, Condition ... resultConditions) {
        super(new Action [] {action}, resultConditions);
        name(actionName);        
    }

    public SeleniumSnapOp name(String actionName) {
    	if(actionName == null || actionName.isEmpty()) {
        	throw new IllegalArgumentException("actionName cannot be null or empty.");
        }
        this.actionName = actionName;
		return this;
	}
    
    @Override
	public SeleniumSnapOp action(Action... actions) {
		super.action(actions);
		return this;
	}

	@Override
	public SeleniumSnapOp condition(Condition... conditions) {
		super.condition(conditions);
		return this;
	}

	@Override
	public SeleniumSnapOp newStep(boolean newStep) {		
		super.newStep(newStep);
		return this;
	}

	@Override
	public SeleniumSnapOp description(String... actionDescriptions) {

		super.description(actionDescriptions);
		return this;
	}

	@Override
	public SeleniumSnapOp onSuccess(String... text) {
		super.onSuccess(text);
		return this;
	}

	@Override
	public SeleniumSnapOp onFailure(String... text) {		
		super.onFailure(text);
		return this;
	} 

    @Override
    protected void processResultSuccess() {
        super.processResultSuccess();
        makeSuccessSnapshot();
    }

    @Override
    protected void processResultFailure() {
        super.processResultFailure();
        makeFailureSnapshot();
    }

    private void makeSuccessSnapshot() {
    	makeSnapshot(actionName);
    }

    private void makeFailureSnapshot() {
        makeSnapshot(actionName+"-FAILED");
    }

    /**
     * Creates an instance of Op that performs screenshots.
     * 
     * @param actionName this is needed as resulting file will be named using this parameter
     * @param action see {@link SeleniumOp#on}
     * @param conditions see {@link SeleniumOp#on}
     * @return new instance of Op
     */
    public static SeleniumSnapOp snapOp(String actionName, Action action, Condition ... conditions) {
        SeleniumSnapOp op = new SeleniumSnapOp(actionName, action, conditions);
        return op;
    }
}
