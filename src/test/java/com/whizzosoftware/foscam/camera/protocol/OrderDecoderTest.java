/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

import io.netty.buffer.Unpooled;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class OrderDecoderTest {
    @Test
    public void testValidDatagram() throws Exception {
        byte[] b = new byte[] {
            'M', 'O', '_', 'I', // camera operate protocol
            1, 0, // operation code
            0, // reserve
            0, 0, 0, 0, 0, 0, 0, 0, // reserve
            0x26, 0, 0, 0, // text length
            0, 0, 0, 0, // reserve
            'c', 'a', 'm', 'e', 'r', 'a', '1', ' ', ' ', ' ', ' ', ' ', ' ', // camera ID
            'M', 'y', ' ', 'C', 'a', 'm', 'e', 'r', 'a', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // camera name
            (byte)192, (byte)168, (byte)0, (byte)150
        };

        List<Object> orders = new ArrayList<>();
        OrderDecoder decoder = new OrderDecoder();

        decoder.decode(null, Unpooled.copiedBuffer(b), orders);

        assertEquals(1, orders.size());
        assertTrue(orders.get(0) instanceof SearchResponse);
    }
}
