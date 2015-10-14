/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

import io.netty.buffer.ByteBuf;
import org.junit.Test;
import static org.junit.Assert.*;

public class OrderTest {
    @Test
    public void testToBytes() {
        Order order = new Order((byte)1, "TEST".getBytes());
        ByteBuf b = order.toByteBuf();

        assertEquals(27, b.readableBytes());

        // assert the "camera operate protocol" header
        assertEquals('M', b.getByte(0));
        assertEquals('O', b.getByte(1));
        assertEquals('_', b.getByte(2));
        assertEquals('I', b.getByte(3));

        // assert all the reserve bytes
        assertEquals((byte)1, b.getByte(4));
        for (int i=5; i < 15; i++) {
            assertEquals(0, b.getByte(i));
        }

        // assert the text length
        assertEquals(4, b.getByte(15));
        assertEquals(0, b.getByte(16));
        assertEquals(0, b.getByte(17));
        assertEquals(0, b.getByte(18));

        // assert more reserve bytes
        for (int i=19; i < 23; i++) {
            assertEquals(0, b.getByte(i));
        }

        assertEquals('T', b.getByte(23));
        assertEquals('E', b.getByte(24));
        assertEquals('S', b.getByte(25));
        assertEquals('T', b.getByte(26));
    }

    @Test
    public void testToBytesLong() {
        // build 1024 length byte array
        byte b[] = new byte[65535];
        for (int i=0; i < 65535; i++) {
            b[i] = '*';
        }

        Order order = new Order((byte)1, b);
        ByteBuf buf = order.toByteBuf();

        // assert the "camera operate protocol" header
        assertEquals('M', buf.getByte(0));
        assertEquals('O', buf.getByte(1));
        assertEquals('_', buf.getByte(2));
        assertEquals('I', buf.getByte(3));

        // assert all the reserve bytes
        assertEquals((byte)1, buf.getByte(4));
        for (int i=5; i < 15; i++) {
            assertEquals(0, buf.getByte(i));
        }

        // assert the text length
        assertEquals((byte)0xFF, buf.getByte(15));
        assertEquals((byte)0xFF, buf.getByte(16));
        assertEquals((byte)0x00, buf.getByte(17));
        assertEquals((byte)0x00, buf.getByte(18));

        // assert more reserve bytes
        for (int i=19; i < 23; i++) {
            assertEquals(0, buf.getByte(i));
        }

        for (int i=23; i < 65535 + 23; i++) {
            assertEquals('*', buf.getByte(i));
        }
    }
}
