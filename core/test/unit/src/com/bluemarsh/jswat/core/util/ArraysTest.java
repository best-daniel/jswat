/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is JSwat. The Initial Developer of the Original
 * Software is Nathan L. Fiedler. Portions created by Nathan L. Fiedler
 * are Copyright (C) 2007-2010. All Rights Reserved.
 *
 * Contributor(s): Nathan L. Fiedler.
 *
 * $Id$
 */

package com.bluemarsh.jswat.core.util;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Unit tests for the Arrays class.
 */
public class ArraysTest {

    @Test
    public void test_Arrays_join() {
        assertNull(Arrays.join(null, null));
        assertNotNull(Arrays.join(new Object[0], null));
        assertNotNull(Arrays.join(null, new Object[0]));
        Object[] arr = new Object[] { System.out };
        assertEqual(Arrays.join(null, arr), arr);
        assertEqual(Arrays.join(arr, null), arr);
        Integer[] i1 = new Integer[] { Integer.MIN_VALUE, Integer.MAX_VALUE };
        Integer[] i2 = new Integer[] { Integer.valueOf(10), Integer.valueOf(100) };
        arr = Arrays.join(i1, i2);
        assertTrue(arr instanceof Integer[]);
        assertEquals(arr.length, 4);
        assertEquals(arr[0], Integer.MIN_VALUE);
        assertEquals(arr[1], Integer.MAX_VALUE);
        assertEquals(arr[2], Integer.valueOf(10));
        assertEquals(arr[3], Integer.valueOf(100));
    }

    private static void assertEqual(Object[] a, Object[] b) {
        assertEquals(a.length, b.length);
        for (int ii = 0; ii < a.length; ii++) {
            assertEquals(a[ii], b[ii]);
        }
    }
}
