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

import java.io.File;
import java.nio.file.Paths;

import com.synapticpath.naica.TestProperties;

/**
 * Holds properties that are needed to execute a Selenium test.
 * 
 * At minimum the user needs to set basePath, this is the place on filesystem where the test report
 * will be written.
 * 
 * @author developer@synapticpath.com
 *
 */
public class SeleniumTestProperties implements TestProperties {
	
	private String basePath;
	
	private String snapshotDirectory;
	
	private String reportDirectory;
	
	public SeleniumTestProperties (String basePath) {		
		this.basePath = basePath;
		new File(basePath).mkdirs();
	}


	public String getAttachmentDirectory() {
		if (snapshotDirectory == null) {
			snapshotDirectory = Paths.get(basePath, "attachments").toString();
		}
		
		return snapshotDirectory;
	}


	public String getReportDirectory() {
		if (reportDirectory == null) {
			reportDirectory = Paths.get(basePath, "report").toString();
		}
		
		return reportDirectory;
	}


	
}
