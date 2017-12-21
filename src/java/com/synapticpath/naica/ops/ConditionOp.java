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

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.synapticpath.naica.conditions.Condition;

/**
 * A conditional {@link Op}, use this Operation to conditionally execute nested Ops. This operation first evaluates 
 * a {@link Condition}, then depending on result executes either successOps or failOps that have been set. 
 * 
 * @author developer@synapticpath.com
 *
 */
public class ConditionOp implements Op {

    private Condition condition;

    private List<Op> successOps;

    private List<Op> failOps;

    private boolean failFast = true;

    private ConditionOp(Condition condition, Op ... successOps) {
        this.successOps = Arrays.asList(successOps);
        this.condition = condition;
    }

    /**
     * Add one or more Ops that will be executed when evaluation of the Condition returns false.
     * @param failOp
     * @return
     */
    public ConditionOp failOp(Op ... failOp) {
    	if (this.failOps == null) {
    		this.failOps = Lists.newArrayList();
    	}
    	
        this.failOps.addAll(Arrays.asList(failOp));
        return this;
    }

    /**
     * Will execute all the Ops even if one fails, note that the default value is true.
     * 
     * @param failFast
     * @return
     */
    public ConditionOp failFast(boolean failFast) {
        this.failFast = failFast;
        return this;
    }

    @Override
    public boolean execute() {
        boolean res = condition.evaluate();
        if (res) {
             evaluateSuccess();
        } else {
            evaluateFail();
        }
        return res;
    }

    protected void evaluateSuccess() {
        if (successOps != null) {

            for (Op successOp: successOps) {
                if (!successOp.execute() && failFast) {
                    return;
                }
            }
        }

    }

    protected void evaluateFail() {
        if (failOps != null) {

            for (Op failOp: failOps) {
                if (!failOp.execute() && failFast) {
                    return;
                }
            }
        }
    }

    /**
     * Creates an instance of ConditionOp.
     * @param condition {@link Condition} to evaluate at start of execution.
     * @param successOp operations to execute when condition evaluation returns true
     * @return
     */
    public static ConditionOp on(Condition condition, Op ... successOp) {
        return new ConditionOp(condition, successOp);
    }
}
