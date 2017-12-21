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

import com.synapticpath.naica.TestStep;

/**
 * This class represents an attachment to a {@link TestStep}. There can be many types
 * of attachment types, at the moment we only support SNAPSHOT.  
 * 
 * @author developer@synapticpath.com
 *
 */
public abstract class Attachment {

	//TOOD add other attachment types as needed.
    public static enum AttachmentType {
        SNAPSHOT;
        
        public boolean isSnapshot() {
        	return SNAPSHOT.equals(this);
        }
    }

    private AttachmentType type;

    public Attachment(AttachmentType type) {
        this.type = type;
    }

    /**
     * @see AttachmentType
     * @return
     */
    public AttachmentType getType() {
        return type;
    }

    /**
     * The name under which an attachment will be presented in report.
     * @return
     */
    public abstract String getName();
}
