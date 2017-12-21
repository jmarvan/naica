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

import static java.lang.String.format;
import java.util.logging.Logger;

import com.synapticpath.naica.ops.Op;

/**
 * TestCaseRunner executes a single TestCase that consists of one or more {@link Op}s.
 * On top of that it takes care of some bookkeeping with {@link TestContext} to properly
 * end finished {@link TestCase}.
 * 
 * @author developer@synapticpath.com
 *
 */
public class TestCaseRunner {
	
	private static final Logger logger = Logger.getLogger(TestCaseRunner.class.getName());
	
	private Op [] ops;
	
	private String id;
	
	private boolean failFast;
	
	public TestCaseRunner (String id, Op ... ops) {
		this.id = id;
		this.ops = ops;
	}	
	
	/**
	 * Returns current failFast value.
	 * @return
	 */
	public boolean isFailFast() {
		return failFast;
	}

	/**
	 * When set to true, will stop test when first operation fails.
	 * @return
	 */
	public void setFailFast(boolean failFast) {
		this.failFast = failFast;
	}

	public void run() {
		init();
        execute();
        finalize();
    }
	
	/**
	 * This method is called when before execution of the actual TestCase that consists of
	 * an array of Ops.
	 * Override to perform tasks that are needed before execution starts.
	 * 
	 * In this implementation we will create a new TestCase, thus starting it.
	 */
	protected void init() {
		logger.info(format("Starting TestCase %s.", id));
		TestContext.getInstance().newTestCase(id);
	}
	
	/**
	 * Override this method and place code that does book-keeping after
	 * a TestCase is been finished.
	 * 
	 * In this implementation we stop the TestCase primarily to record test duration.
	 */
	protected void finalize() {
		TestContext.getInstance().stop();
		logger.info(format("Ending TestCase %s.", id));
	}


	/**
	 * Executes all test operations.
	 */
	protected void execute() {
		
		for (Op op: ops) {
			boolean result = op.execute();
			
			if (!result && failFast) {
				break;
			}
		}		
	}

	/**
	 * Executes an individual operation.
	 * @param op
	 * @return
	 */
	protected boolean execute(Op op) {
		try {
			return op.execute();
		} catch (Throwable t) {
			return false;
		}
		
	}

}
