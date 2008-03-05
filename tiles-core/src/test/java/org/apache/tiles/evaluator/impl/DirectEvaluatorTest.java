/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.evaluator.impl;

import org.apache.tiles.Attribute;

import junit.framework.TestCase;

/**
 * Tests {@link DirectEvaluator}.
 *
 * @version $Rev$ $Date$
 */
public class DirectEvaluatorTest extends TestCase {

    /**
     * The evaluator to test.
     */
    private DirectEvaluator evaluator;

    /** {@inheritDoc} */
    public void setUp() throws Exception {
        evaluator = new DirectEvaluator();
    }

    /**
     * Tests
     * {@link DirectEvaluator#evaluate(Attribute, org.apache.tiles.context.TilesRequestContext)}.
     */
    public void testEvaluate() {
        String expression = "This is an expression";
        Attribute attribute = new Attribute(expression);
        Object result = evaluator.evaluate(attribute, null);
        assertEquals("The expression has not been evaluated correctly", result,
                expression);
        expression = "${attributeName}";
        attribute.setValue(expression);
        result = evaluator.evaluate(attribute, null);
        assertEquals("The expression has not been evaluated correctly", result,
                expression);
    }
}