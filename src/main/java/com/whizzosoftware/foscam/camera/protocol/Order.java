/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

/**
 * The Foscam documentation calls each message in their discovery protocol an "Order". So we'll call it that too.
 *
 * @author Dan Noguerol
 */
public class Order {
    private byte operationCode;
    private byte[] text;

    public Order(byte operationCode, byte[] text) {
        this.operationCode = operationCode;
        this.text = text;
    }

    public byte[] toBytes() {
        byte[] b = new byte[23 + text.length];

        // add "camera operate protocol"
        b[0] = 'M';
        b[1] = 'O';
        b[2] = '_';
        b[3] = 'I';

        // add "operation code"
        b[4] = operationCode;
        b[5] = 0;

        // add reserve (INT8)
        b[6] = 0;

        // add reserve (BINARY_STREAM[8])
        b[7] = 0;
        b[8] = 0;
        b[9] = 0;
        b[10] = 0;
        b[11] = 0;
        b[12] = 0;
        b[13] = 0;
        b[14] = 0;

        // add text length
        b[15] = (byte)(text.length & 0xFF);
        b[16] = (byte)((text.length >> 8) & 0xFF);
        b[17] = (byte)((text.length >> 16) & 0xFF);
        b[18] = (byte)((text.length >> 24) & 0xFF);

        // add reserve (INT32)
        b[19] = 0;
        b[20] = 0;
        b[21] = 0;
        b[22] = 0;

        System.arraycopy(text, 0, b, 23, text.length);

        return b;
    }
}
