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
package com.synapticpath.naica.ops;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.synapticpath.naica.TestContext;
import com.synapticpath.naica.TestStep;
import com.synapticpath.naica.actions.Action;
import com.synapticpath.naica.conditions.Condition;

/**
 * Base implementation of Op (Operation). A Test Operation consists of {@link Action}(s) and {@link Condition}(s).
 * When executed it will go through following steps:
 * 
 * 1) Create a new test step if it is configured as such.
 * 2) Add Action descriptions to current step.
 * 3) Perform {@link Action}(s)
 * 4) Evaluate {@link Condition}(s)
 * 5) Depending on success of Action and Condition add result descriptions to current step. 
 * 
 * @author developer@synapticpath.com
 *
 */
public class BaseOp implements Op {

    private List<Action> actions;
    private List<Condition> resultConditions;

    private String [] actionDescriptions;
    
    protected Set<String> onSuccess;
    
    protected Set<String> onFailure;

    private boolean newStep;

    protected BaseOp(Action [] actions, Condition [] resultConditions) {
        this.actions = new ArrayList<Action>();
        this.actions.addAll(Arrays.asList(actions));
        this.resultConditions = new ArrayList<Condition>();
        this.resultConditions.addAll(Arrays.asList(resultConditions));
    }

    /**
     * Depending on values set, calling this will execute the navigation.
     */
    public boolean execute() {

        evaluateNewStep();
        evaluateActionDescription();

        boolean success = doActions();
        if (success) {
            success = evaluateConditions();
        }

        if (success) {  //Result condition true
            processResultSuccess();
        } else {
            processResultFailure();
        }
        return success;
    }


    protected boolean doActions() {

        for (Action action: actions) {            

            if (!action.perform()) {
            	processActionFailure(action);            	
                return false;
            } else {
            	processActionSuccess(action);
            }
        }

        return true;
    }    
        

    protected boolean evaluateConditions() {
        for (Condition condition: resultConditions) {
            if (!condition.evaluate()) {
                return false;
            }
        }
        return true;
    }

    protected void evaluateNewStep() {
        if (newStep) {
            TestContext.getInstance().addStep();
        }
    }

    protected void evaluateActionDescription() {
        if (actionDescriptions != null) {
            for (String text : actionDescriptions) {
                TestContext.getInstance().addAction(text);
            }
        }
    }
    
	protected void processActionSuccess(Action action) {
	    
	}
	
	protected void processActionFailure(Action action) {
	     
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

    /**
     * Adds an action to other actions that are to be executed in this operation.
     * 
     * @param actions
     * @return
     */
    public BaseOp action(Action ... actions) {
        for (Action action : actions) {
            this.actions.add(action);
        }
        return this;
    }

    /**
     * Adds an extra condition to evaluate after all actions have been successfully performed.
     * 
     * @param conditions
     * @return
     */
    public BaseOp condition(Condition ... conditions) {
        for (Condition condition : conditions) {
            resultConditions.add(condition);
        }
        return this;
    }

    public BaseOp newStep(boolean newStep) {
        this.newStep = newStep;
        return this;
    }

    /**
     *
     * @param actionDescriptions
     * @return
     */
    public BaseOp description(String ... actionDescriptions) {
        this.actionDescriptions = actionDescriptions;
        return this;
    }

    /**
     * Add one or more lines of text that will be added to {@link TestStep#addResult(String)} when
     * this Condition is executed successfully.
     *  
     * @param text
     * @return
     */
    public BaseOp onSuccess(String ... text) {
    	if (onSuccess == null) {
    		onSuccess = new LinkedHashSet<>(); 
    	}
    	onSuccess.addAll(Arrays.asList(text));  
        return this;
    }
    
    /**
     * Add one or more lines of text that will be added to {@link TestStep#addResult(String)} when
     * this Condition is not executed successfully.
     *  
     * @param text
     * @return
     */
    public BaseOp onFailure(String ... text) {
    	if (onFailure == null) {
    		onFailure = new LinkedHashSet<>(); 
    	}
    	onFailure.addAll(Arrays.asList(text));  
        return this;
    }
    
}
