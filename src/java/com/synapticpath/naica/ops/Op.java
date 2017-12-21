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

import com.synapticpath.naica.actions.Action;
import com.synapticpath.naica.conditions.Condition;

/**
 * The Op (short for Operation) interface is the most basic building block for structured testing.  Essentially
 * a structured test consists of one or more Ops that are executed.
 * 
 * Although the Op interface does not dictate what should happen when executed, the base implementation
 * {@link BaseOp} performs {@link Action} that is followed by evaluating a {@link Condition} that determines
 * whether the Action was successful or not. 
 * 
 * @author developer@synapticpath.com
 *
 */
@FunctionalInterface
public interface Op {
	
	/**
	 * When called, will execute testing business logic contained within. 
	 * 
	 * @return true if execution successful, false otherwise.
	 */
    boolean execute();
}
