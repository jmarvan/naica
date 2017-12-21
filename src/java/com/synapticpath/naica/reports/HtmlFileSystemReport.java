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

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.synapticpath.naica.TestCase;
import com.synapticpath.naica.TestContext;
import com.synapticpath.naica.TestOutcome;
import com.synapticpath.naica.TestProperties;
import com.synapticpath.naica.TestStep;
import com.synapticpath.naica.attachments.Attachment;
import com.synapticpath.naica.attachments.SnapshotAttachment;
import com.synapticpath.naica.ops.BaseOp;

/**
 * This particular implementation of ReportGenerator will create series of html pages on local
 * machine, where test results can be easily viewed.
 * 
 *  {@link TestProperties#getReportDirectory()} has to return a filename of a directory where otuput
 *  will be generated.
 *  
 *  This generator's output will take following structure:
 *  [ReportDirectory]
 *  	[ReportName*]
 *  		index.html
 *  		[TestCase1]
 *  			TestCase1.html
 *  			attachment1.png
 *  		[TestCasen]
 *  			TestCasen.html
 *  			attachment2.png
 *  
 *  *) ReportName is a formatted date when particular TestSuite has been completed.
 *  
 *  Note that index.html contains overall results of the test, and is navigable to each TestCase.
 * 
 * @author developer@synapticpath.com
 *
 */
public class HtmlFileSystemReport implements ReportGenerator {
	
	private static final Logger logger = Logger.getLogger(BaseOp.class.getName());
    
    private static final String DATE_PARAM = "%tF %<tT";
    
    private static final String HEADER_TEMPLATE ="<!DOCTYPE html>\n<html><head>\n"
        + "<title>"+DATE_PARAM+"</title>\r\n"
        + "<style type=\"text/css\">\n"
        + "body, td { font-family : serif, sans-serif, monospace; font-size: 1em; }\n"
    	+ "table, tr, td, th { border: 1px solid grey; vertical-align:top; border-collapse:collapse;}\n"
    	+ "td, th { padding: 4px; }\n"
    	+ ".failed { color:crimson; }\n"
    	+ ".success { color:darkGreen;}\n"
    	+ ".conditional { color:darkTurquoise; }\n"
    	+ "</style>\n"
    	+ "</head><body>\n";

    private static final String INDEX_BODY_TEMPLATE = "Automated tests were <span class=\"%s\">%s</span>. Tests completed at "+DATE_PARAM+"<br/><br/>\n";    
    
    private static final String FOOTER_TEMPLATE ="</body></html>";
    
    private static final String TEST_CASE_SUMMARY_TEMPLATE = "Test case <a href=\"%s/%<s.html\">%<s</a> <span class=\"%s\">%s</span>";  //Params testCaseId, outcomeClassName, outcomeText
    private static final String TEST_CASE_BODY_TEMPLATE = "Execution of Test case %s was <span class=\"%s\">%s</span>";  //Params: testCaseId, outcomeClassName, outcomeText
    
    private static final String TEST_CASE_DURATION_TEMPLATE = "<span>, duration %s %s.</span>\n";   //Params: time, unit 

    
    public void generate(TestContext testContext) {

        File rootDir = new File(TestContext.getInstance().getProperties().getReportDirectory());
        rootDir.mkdir();
        
        //Removing colons because Windows
        File currentDir = Paths.get(rootDir.toString(), format("%tF %<tH%<tM%<tS", new Date()).replace(":", "")).toFile();
        currentDir.mkdir();

        for (TestCase testCase : testContext.getTestCases()) {
            renderResults(testCase, currentDir);
        }
        
        renderSummary(testContext, currentDir);
    }

    protected void renderSummary(TestContext testContext, File currentDir) {

        try {
            PrintWriter out = new PrintWriter(Paths.get(currentDir.toString(), "index.html").toFile());
            writeIndexHtml(testContext, out);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void renderResults(TestCase testCase, File currentDir) {

        try {
            File testCaseDir = Paths.get(currentDir.toString(), testCase.getId()).toFile();
            testCaseDir.mkdir();
            PrintWriter out = new PrintWriter(Paths.get(testCaseDir.toString(), testCase.getId() +".html").toFile());
            writeTestcase(testCase, testCaseDir, out);            

            out.close();


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeIndexHtml(TestContext testContext, PrintWriter out) {
        
    	Date testDate = new Date();
    	
        out.write(format(HEADER_TEMPLATE, testDate));        
        out.write(format(INDEX_BODY_TEMPLATE, outcomeToClassName(testContext.getOutcome()), outcomeToText(testContext.getOutcome()), testDate));

        for (TestCase testCase: testContext.getTestCases()) {
        	TestOutcome outcome = testCase.getOutcome();
        	out.write(format(TEST_CASE_SUMMARY_TEMPLATE, testCase.getId(), outcomeToClassName(outcome) , outcomeToText(outcome)));            
        }

        out.write(FOOTER_TEMPLATE);
        
    }


    private void writeTestcase(TestCase testCase, File testCaseDir, PrintWriter out) {        
        
        out.write(format(HEADER_TEMPLATE, new Date()));
        
        long durationSeconds = testCase.getDuration()/1000;
        long durationMinutes = testCase.getDuration()/1000/60;
        long duration = durationMinutes == 0 ? durationSeconds : durationMinutes;
        
        //TODO For example represent anything less than 5 minutes in seconds
        String durationTimeUnit = durationMinutes == 0 ? "seconds" : "minutes";
        String outcomeClass = outcomeToClassName(testCase.getOutcome());
        String outcome = outcomeToText(testCase.getOutcome());

        out.write(format(TEST_CASE_BODY_TEMPLATE, testCase.getId(), outcomeClass, outcome));        
        out.write(format(TEST_CASE_DURATION_TEMPLATE, duration, durationTimeUnit));
        
        writeTestCaseDetails(testCase, testCaseDir, out);
        
        out.write(FOOTER_TEMPLATE);
    }

    private String outcomeToText(TestOutcome outcome) {
        switch (outcome) {
            case FAILURE :
                return "failed";
            case CONDITIONAL_SUCCESS :
                return "conditionally successful";                
            default :
            	return "successful";            	
        }
    }
    

    private String outcomeToClassName(TestOutcome outcome) {
    	return outcome.name().toLowerCase();
    }

    private void writeTestCaseDetails(TestCase testCase, File testCaseDir, PrintWriter out) {
   	
    	out.write("<table><tr><th>Step</th><th>Description</th><th>Observed Results</th><th>Attachments</th></tr>");
    	
        int stepIndex = 0;
        
        for (TestStep testStep : testCase.getSteps()) {
            out.write(format("<tr><td class=\"%s\">Step %s</td><td>", outcomeToClassName(testCase.getOutcome()), ++stepIndex));

            //Write Actions
            for (String action : testStep.getActions()) {
                out.write(action);
                out.write("<br/>");
            }
            out.write("</td><td>");

            //Write Results
            for (String result : testStep.getResults()) {
            	out.write(result);
                out.write("<br/>");
            }
            out.write("</td><td>");
            
            //Write Attachments
            for (Attachment attachment : testStep.getAttachments()) {

                if (attachment.getType().isSnapshot()) {
                    SnapshotAttachment screenshot = (SnapshotAttachment)attachment;
                    Path path = copySnapshot(screenshot, testCaseDir);
                    out.write(format("<a target=\"_blank\" href=\"%s\">%s</a><br/>", path.getFileName(), attachment.getName()));                    
                }
            }


            out.write("</td></tr>");
        }
        
        out.write("</table>");
    }

    private Path copySnapshot(SnapshotAttachment attachment, File testCaseDir) {

        File file = attachment.getFileName().toFile();
        String name = file.getName();

        Path src = file.toPath();
        Path dst = Paths.get(testCaseDir.toString(), name);

        try {
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
        	logger.log(Level.SEVERE, format("Failed to copy snapshot file %s", attachment.getFileName()), ioe);
        }
        return dst;
    }
}
