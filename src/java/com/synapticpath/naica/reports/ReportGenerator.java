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
package com.synapticpath.naica.reports;

import com.synapticpath.naica.TestContext;

/**
 * All Generators that turn test result information stored in TestContext into (not only)
 * readable form should implement this interface.
 * This way they can be treated in a generic fashion by the TestSuiteRunner. 
 * 
 * @author developer@synapticpath.com
 *
 */
public interface ReportGenerator {
	
	/**
	 * When called, the ReportGenerator is supposed to generate an output to a text file,
	 * perhaps contacting an application like HP ALM or a DB to store results of the test. 
	 * 
	 * @param testContext
	 */
	void generate(TestContext testContext);

}
