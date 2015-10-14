/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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

    public ByteBuf toByteBuf() {
        ByteBuf b = Unpooled.buffer(23 + text.length);

        // add "camera operate protocol"
        b.writeByte('M');
        b.writeByte('O');
        b.writeByte('_');
        b.writeByte('I');

        // add "operation code"
        b.writeByte(operationCode);
        b.writeByte(0);

        // add reserve (INT8)
        b.writeByte(0);

        // add reserve (BINARY_STREAM[8])
        for (int i=7; i <= 14; i++) {
            b.writeByte(0);
        }

        // add text length
        b.writeByte((byte) (text.length & 0xFF));
        b.writeByte((byte) ((text.length >> 8) & 0xFF));
        b.writeByte((byte) ((text.length >> 16) & 0xFF));
        b.writeByte((byte) ((text.length >> 24) & 0xFF));

        // add reserve (INT32)
        for (int i=19; i <= 22; i++) {
            b.writeByte(0);
        }

        b.writeBytes(text);

        return b;
    }
}
