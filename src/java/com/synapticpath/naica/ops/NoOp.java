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

/**
 * There are some higher-level test structures that require that an Op is specified.
 * Use this when in need of an Op that does exactly nothing. 
 * 
 * ConditionOp executes an Op in case a Condition evaluates to true, otherwise it will evaluate
 * optional "fail" Ops. This is nice, but the true Ops are a required parameter.  We can use the NoOp
 * to supply ConditionOp a success Op, if we want nothing done in case the Condition evaluates to true.  
 *  
 * @author developer@synapticpath.com
 *
 */
public class NoOp implements Op {

    private static NoOp instance = new NoOp();

    private NoOp() {
    }

    @Override
    public boolean execute() {
        return true;
    }

    public static NoOp make() {
        return instance;
    }
}
