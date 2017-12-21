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

import java.util.ArrayList;
import java.util.List;

import com.synapticpath.naica.attachments.Attachment;

/**
 * Represents a single TestCase, each test case can have multiple steps, while typically
 * one "currentStep" is the one that is being executed.
 * When current step execution is finished, a new step will be created until TestCase is
 * complete.
 * 
 * See {@link TestContext} for more information.
 * 
 * @author developer@synapticpath.com
 */
public class TestCase {

    private String id;

    private List<TestStep> steps;

    private TestStep currentStep;

    private long startTime;

    private long finishTime = -1;
    

    public TestCase(String id) {
        this.id = id;
        steps = new ArrayList<TestStep>();
        startTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TestStep> getSteps() {
        return steps;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    /**
     * Returns the duration of the test. If finish time is not set, the test is
     * considered ongoing, therefore current time is used.
     * 
     * @return
     */
    public long getDuration() {
        if (finishTime<0) {
            return System.currentTimeMillis() - startTime;
        }

        return finishTime - startTime;
    }

    public void stop() {
        finishTime = System.currentTimeMillis();
    }

    public void addStep() {

        currentStep = new TestStep();
        steps.add(currentStep);

    }

    public void addAction(String description) {
        createStepIfMissing();
        currentStep.addAction(description);
    }

    public void addResult(String description) {
        createStepIfMissing();
        currentStep.addResult(description);
    }

    public void addAttachment(Attachment attachment) {
        createStepIfMissing();
        currentStep.addAttachment(attachment);
    }

    public void fail() {
        createStepIfMissing();
        currentStep.fail();
    }

    private void createStepIfMissing() {
        if (currentStep == null) {
            addStep();
        }
    }

    /**
     * TestCase is considered failed when one of constituent steps results in failure.
     * @return
     */
    public boolean isFailed() {

        for (TestStep step : getSteps()) {
            if (step.isFailed()) {
                return true;
            }
        }

        return false;
    }

    public TestStep getCurrentStep() {
        return currentStep;
    }

    public TestOutcome getOutcome() {
        boolean conditional = false;
        
        for (TestStep step : getSteps()) {
            switch (step.getOutcome()) {                
                case FAILURE:
                	//When one of the steps fails, the whole test case fails
                    return step.getOutcome();
                case CONDITIONAL_SUCCESS:
                	//When conditional flag is set, 
                    conditional = true;
                    break;
                case SUCCESS:
                	break;
            }
        }

        return conditional ? TestOutcome.CONDITIONAL_SUCCESS : TestOutcome.SUCCESS;
    }

}
