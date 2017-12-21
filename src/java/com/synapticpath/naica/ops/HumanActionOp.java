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

import java.util.Arrays;
import java.util.List;

import com.synapticpath.naica.TestCase;
import com.synapticpath.naica.TestContext;
import com.synapticpath.naica.TestOutcome;

/**
 * Test operation that is performed by a human. When executed, this Operation
 * simply adds action descriptions and expectedResults to TestContext.
 * 
 * Note: Execution of this Op will cause {@link TestOutcome#CONDITIONAL_SUCCESS}.
 * 
 * @author developer@synapticpath.com
 *
 */
public class HumanActionOp implements Op {

	private List<String> actions;
	private List<String> expectedResults;
	private boolean addStep = true;

	private HumanActionOp(List<String> actions, List<String> expectedResults) {
		this.actions = actions;
		this.expectedResults = expectedResults;
	}

	/**
	 * Addstep being true, normally you should have a good reason to modify
	 * previous step.
	 *
	 * @param doit
	 * @return
	 */
	public HumanActionOp addStep(boolean doit) {
		this.addStep = doit;
		return this;
	}

	@Override
	public boolean execute() {

		TestCase tc = TestContext.getInstance().getCurrentTestCase();
		if (addStep) {
			tc.addStep();
		}

		tc.getCurrentStep().setExecutedByHuman(true);
		tc.getCurrentStep().getActions().addAll(actions);
		tc.getCurrentStep().getResults().addAll(expectedResults);

		return true;
	}

	/**
	 * Creates an instance of HumanActionOp with given action descriptions.
	 * @param actions
	 * @return
	 */
	public static HumanActionOp on(String ... actions) {
		return new HumanActionOp(Arrays.asList(actions), null);
	}
}
