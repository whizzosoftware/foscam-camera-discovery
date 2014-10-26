/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

import org.junit.Test;
import static org.junit.Assert.*;

public class OrderTest {
    @Test
    public void testToBytes() {
        Order order = new Order((byte)1, "TEST".getBytes());
        byte[] b = order.toBytes();

        // assert the "camera operate protocol" header
        assertEquals('M', b[0]);
        assertEquals('O', b[1]);
        assertEquals('_', b[2]);
        assertEquals('I', b[3]);

        // assert all the reserve bytes
        assertEquals((byte)1, b[4]);
        for (int i=5; i < 15; i++) {
            assertEquals(0, b[i]);
        }

        // assert the text length
        assertEquals(4, b[15]);
        assertEquals(0, b[16]);
        assertEquals(0, b[17]);
        assertEquals(0, b[18]);

        // assert more reserve bytes
        for (int i=19; i < 23; i++) {
            assertEquals(0, b[i]);
        }

        assertEquals('T', b[23]);
        assertEquals('E', b[24]);
        assertEquals('S', b[25]);
        assertEquals('T', b[26]);
    }

    @Test
    public void testToBytesLong() {
        // build 1024 length byte array
        byte b[] = new byte[65535];
        for (int i=0; i < 65535; i++) {
            b[i] = '*';
        }

        Order order = new Order((byte)1, b);
        b = order.toBytes();

        // assert the "camera operate protocol" header
        assertEquals('M', b[0]);
        assertEquals('O', b[1]);
        assertEquals('_', b[2]);
        assertEquals('I', b[3]);

        // assert all the reserve bytes
        assertEquals((byte)1, b[4]);
        for (int i=5; i < 15; i++) {
            assertEquals(0, b[i]);
        }

        // assert the text length
        assertEquals((byte)0xFF, b[15]);
        assertEquals((byte)0xFF, b[16]);
        assertEquals((byte)0x00, b[17]);
        assertEquals((byte)0x00, b[18]);

        // assert more reserve bytes
        for (int i=19; i < 23; i++) {
            assertEquals(0, b[i]);
        }

        for (int i=23; i < 65535 + 23; i++) {
            assertEquals('*', b[i]);
        }
    }
}
