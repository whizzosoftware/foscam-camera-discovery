/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.List;

/**
 * An inbound handler responsible for reading a DatagramPacket and converting it into a Foscam Order
 * object.
 *
 * @author Dan Noguerol
 */
public class OrderDecoder extends ByteToMessageDecoder {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        Byte b;
        if (buf.isReadable()) {
            do {
                b = buf.readByte();
            } while (b != 'M' && buf.isReadable());

            if (buf.readableBytes() > 0) {
                logger.trace("Found possible start of order");
                b = buf.readByte();
                if (b == 'O' && buf.isReadable()) {
                    b = buf.readByte();
                    if (b == '_' && buf.isReadable()) {
                        b = buf.readByte();
                        if (b == 'I' && buf.isReadable()) {
                            Integer operationCode = popINT16(buf);
                            if (operationCode != null) {
                                if (popBytes(buf, 9)) {
                                    Integer length = popINT32(buf);
                                    if (length != null && popBytes(buf, 4)) {
                                        if (buf.readableBytes() >= length) {
                                            byte[] text = new byte[length];
                                            buf.readBytes(text);
                                            switch (operationCode) {
                                                case 1:
                                                    try {
                                                        list.add(new SearchResponse(text, 0, length));
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
    }

    /**
     * Pops two bytes from the buffer and returns them as a little-endian integer.
     *
     * @param buf the buffer to read
     *
     * @return an Integer (or null if there aren't at least two bytes available in the buffer)
     */
    private Integer popINT16(ByteBuf buf) {
        if (buf.readableBytes() >= 2) {
            byte b[] = new byte[2];
            buf.readBytes(b, 0, 2);
            return (int)java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort();
        } else {
            return null;
        }
    }

    /**
     * Pops four bytes from the buffer and returns them as a little-endian integer.
     *
     * @param buf the buffer to read
     *
     * @return an Integer (or null if there aren't at least four bytes available in the buffer)
     */
    private Integer popINT32(ByteBuf buf) {
        if (buf.readableBytes() >= 4) {
            byte b[] = new byte[4];
            b[0] = buf.readByte();
            b[1] = buf.readByte();
            b[2] = buf.readByte();
            b[3] = buf.readByte();
            return java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
        } else {
            return null;
        }
    }

    /**
     * Pop a specified number of bytes from the buffer
     *
     * @param buf the byte buffer to read from
     * @param num the number of bytes to pop (read & discard)
     *
     * @return whether the bytes were successfully popped
     */
    private boolean popBytes(ByteBuf buf, int num) {
        if (buf.readableBytes() > num) {
            buf.readBytes(num);
            return true;
        } else {
            return false;
        }
    }
}
