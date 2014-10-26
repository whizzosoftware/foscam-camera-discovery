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

public class ProtocolParserTest {
    @Test
    public void testFullOrder() {
        MockProtocolParserListener listener = new MockProtocolParserListener();
        ProtocolParser parser = new ProtocolParser(listener);
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

        parser.addBytes(b, 0, b.length);

        assertEquals(1, listener.getOrders().size());
        assertTrue(listener.getOrders().get(0) instanceof SearchResponse);
        SearchResponse sr = (SearchResponse)listener.getOrders().get(0);
        assertEquals("camera1", sr.getCameraId());
        assertEquals("My Camera", sr.getCameraName());
        assertEquals("192.168.0.150", sr.getAddress().getHostAddress());
    }
}
