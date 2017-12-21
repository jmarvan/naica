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
package com.synapticpath.naica.attachments;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.synapticpath.naica.TestCase;
import com.synapticpath.naica.TestContext;
import com.synapticpath.naica.TestProperties;
import com.synapticpath.naica.actions.Action;

/**
 * Represents a snapshot that provides visual confirmation of an {@link Action} result. 
 * 
 * In order for it to function correctly it needs 3 pieces of information.
 * 1) name of the snapshot, note that the name should be unique within a {@link TestCase}
 * 2) fileExtension
 * 3) {@link TestProperties#getAttachmentDirectory()}
 * 
 * @author developer@synapticpath.com
 *
 */
public class SnapshotAttachment extends Attachment {

    private Path fileName;

    private String name;
    
    private static final String FILE_NAME_TEMPLATE = "%s/%<s_%02d_%02d.%s";  //

    /**
     * Create an instance of a snapshot with given name and file extension. The SnapshotAttachment
     * class will gather necessary information in order to provide a full fileName to create the SnapshotAttachment
     * under.
     * 
     * @param name
     * @param fileExtension
     */
    public SnapshotAttachment(String name, String fileExtension) {
        super(AttachmentType.SNAPSHOT);
        this.name = name;
        
        TestContext context = TestContext.getInstance();
        TestCase tc = context.getCurrentTestCase();
        int stepIndex = tc.getSteps().indexOf(tc.getCurrentStep()) + 1;
        int attachmentIndex = tc.getCurrentStep().getAttachments().size() + 1;
        this.fileName = Paths.get(context.getProperties().getAttachmentDirectory(), String.format(FILE_NAME_TEMPLATE, tc.getId(), stepIndex, attachmentIndex, fileExtension));
    }    

    public Path getFileName() {
        return fileName;
    }

    public void setFileName(Path fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }
}
