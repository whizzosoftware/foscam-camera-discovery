/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.ArrayDeque;

/**
 * A parser for the Foscam camera discovery protocol.
 *
 * @author Dan Noguerol
 */
public class ProtocolParser {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ProtocolParserListener listener;
    private ArrayDeque<Byte> byteBuffer = new ArrayDeque<Byte>();

    public ProtocolParser(ProtocolParserListener listener) {
        this.listener = listener;
    }

    public void addBytes(byte[] bytes, int offset, int length) {
        logger.debug("Received {} bytes", length);
        for (int i=offset; i < offset + length; i++) {
            byteBuffer.add(bytes[i]);
        }

        while (byteBuffer.size() > 0) {
            if (!processBuffer()) {
                break;
            }
        }
    }

    /**
     * Process the internal buffer
     *
     * @return boolean indicating whether buffer is incomplete
     */
    protected boolean processBuffer() {
        Byte b;
        if (byteBuffer.size() > 0) {
            do {
                b = byteBuffer.pop();
            } while (b != null && b != 'M');

            if (b != null) {
                logger.trace("Found possible start of order");
                b = byteBuffer.pop();
                if (b != null && b == 'O') {
                    b = byteBuffer.pop();
                    if (b != null && b == '_') {
                        b = byteBuffer.pop();
                        if (b != null && b == 'I') {
                            Integer operationCode = popINT16();
                            if (operationCode != null) {
                                if (popBytes(9)) {
                                    Integer length = popINT32();
                                    if (length != null && popBytes(4)) {
                                        byte[] text = popBytesToArray(length);
                                        if (text != null) {
                                            switch (operationCode) {
                                                case 1:
                                                    try {
                                                        listener.onOrder(new SearchResponse(text, 0, length));
                                                    } catch (UnknownHostException e) {
                                                        logger.error("Error processing search response", e);
                                                    }
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private Integer popINT8() {
        if (byteBuffer.size() > 0) {
            return (int)byteBuffer.pop();
        } else {
            return null;
        }
    }

    private Integer popINT16() {
        if (byteBuffer.size() > 1) {
            byte b[] = new byte[2];
            b[0] = byteBuffer.pop();
            b[1] = byteBuffer.pop();
            return (int)java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort();
        } else {
            return null;
        }
    }

    private Integer popINT32() {
        if (byteBuffer.size() > 3) {
            byte b[] = new byte[4];
            b[0] = byteBuffer.pop();
            b[1] = byteBuffer.pop();
            b[2] = byteBuffer.pop();
            b[3] = byteBuffer.pop();
            return java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
        } else {
            return null;
        }
    }

    /**
     * Pop a specified number of bytes from the buffer
     *
     * @param num the number of bytes to pop
     *
     * @return whether the bytes were successfully popped
     */
    private boolean popBytes(int num) {
        if (byteBuffer.size() > num) {
            for (int i=0; i < num; i++) {
                byteBuffer.pop();
            }
            return true;
        } else {
            return false;
        }
    }

    private byte[] popBytesToArray(int num) {
        if (byteBuffer.size() >= num) {
            byte[] b = new byte[num];
            for (int i=0; i < num; i++) {
                b[i] = byteBuffer.pop();
            }
            return b;
        } else {
            return null;
        }
    }
}
