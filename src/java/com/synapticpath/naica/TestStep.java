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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.synapticpath.naica.attachments.Attachment;

/**
 * The TestStep class holds together execution information and metadata of a single step in a TestCase 
 * 
 *  TODO: Detailed documentation
 * 
 * @author developer@synapticpath.com
 *
 */
public class TestStep {

    private UUID guid;

    private Set<String> actions;
    private Set<String> results;
    private List<Attachment> attachments;
    private boolean failed;
    private boolean executedByHuman;

    public TestStep() {
        guid = UUID.randomUUID();
        actions = new LinkedHashSet<String>();
        results = new LinkedHashSet<String>();
        attachments = new ArrayList<Attachment>();
    }

    public UUID getGuid() {
        return guid;
    }

    /**
     * Add single action description, note that a TestStep can have multiple actions that may need a description.
     * Action description is essentially human-readable text of what the test is actually doing.
     * 
     * For example: "Click navigation link to open contact information page."
     * 
     * @param description
     */
    public void addAction(String description) {
        actions.add(description);
    }

    /**
     * Adds a result description, this is another human-readable text that describes what the result of an action was. 
     * 
     * For example: "Contact information page is shown."
     * 
     * @param description
     */
    public void addResult(String description) {
        results.add(description);
    }

    /**
     * Adds a single attachment object.  Attachments contain things like screenshots etc.; they provide
     * traceability and visual confirmation of an action happening.
     * 
     * @param attachment
     */
    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public Set<String> getActions() {
        return actions;
    }

    public Set<String> getResults() {
        return results;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public boolean isFailed() {
        return failed;
    }

    public void fail() {
        this.failed = true;
    }

    /**
     * When true, we can show in a report that automated test could not be executed and should be done manually.
     * This can happen when a Test Operation is missing some preconditions in order to proceed. 
     * 
     * @return
     */
    public boolean isExecutedByHuman() {
        return executedByHuman;
    }

    public void setExecutedByHuman(boolean humanExecuted) {
        this.executedByHuman = humanExecuted;
    }

    public TestOutcome getOutcome() {
        if (isFailed()) {
            return TestOutcome.FAILURE;
        } else if (isExecutedByHuman()) {
            return TestOutcome.CONDITIONAL_SUCCESS;
        }

        return TestOutcome.SUCCESS;
    }
}
