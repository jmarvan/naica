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

import com.synapticpath.naica.reports.HtmlFileSystemReport;
import com.synapticpath.naica.reports.ReportGenerator;

/**
 * The TestSuiteRunner class is a top level component for running structured tests.
 * It's responsibility is to execute main business method of TestCaseRunners passed here in constructor.
 * 
 * When all runners are finished, the TestSuiteRunner finalizes TestContext and then
 * calls a report generator to turn Test result information stored in TestContext into something
 * human can view.
 * 
 * @author developer@synapticpath.com
 *
 */
public class TestSuiteRunner {
	
	private TestCaseRunner [] runners;
	
	private ReportGenerator [] reports;
	
	/**
	 * Initialize the TestSuiteRunner with TestCaseRunners that constitute the 
	 * "TestSuite" to test.
	 * 
	 * @param runners
	 */
	public TestSuiteRunner(TestCaseRunner ... runners) {
		this.runners = runners;
	}
	
	/**
	 * Use this to set one or more generators whose job is to export the results
	 * of testing into something that a human can read.
	 * 
	 *  See {@link HtmlFileSystemReport} for basic report generation.
	 * 
	 * 
	 * @param reports
	 */
	public void setReportGenerators(ReportGenerator ... reports){
		this.reports = reports;
	}
	
	/**
	 * Runs all TestCaseRunners, finalizes TestContext, generates report if {@link ReportGenerator} is specified. 
	 */
	public void run() {
        execute();      
        finalize();
        renderResults();        
    }

	protected void execute() {		
		for (TestCaseRunner runner: runners) {
			runner.run();
		}		
	}
	
	protected void finalize() {
		TestContext.getInstance().finalize();
	}

	protected void renderResults() {
		if (reports != null) {
			for (ReportGenerator report: reports) {
				report.generate(TestContext.getInstance());
			}
		}
		
	}

}
