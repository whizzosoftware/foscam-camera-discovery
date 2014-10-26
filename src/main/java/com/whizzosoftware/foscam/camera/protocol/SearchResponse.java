/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A camera search response.
 *
 * From the documentation: "when the camera receives search req command, it should broadcast this command to report
 * its network configuration and device information".
 *
 * @author Dan Noguerol
 */
public class SearchResponse extends Order {
    private String cameraId;
    private String cameraName;
    private InetAddress address;

    /**
     * Constructor.
     *
     * @param data the byte data that was read
     * @param offset the starting offset into the byte data
     * @param length the length of the data
     */
    public SearchResponse(byte[] data, int offset, int length) throws UnknownHostException {
        super(
            (byte)1, // operation code == 1
            data
        );

        cameraId = new String(data, offset, 13).trim();
        cameraName = new String(data, offset + 13, 21).trim();

        byte[] address = new byte[4];
        address[0] = data[offset + 34];
        address[1] = data[offset + 35];
        address[2] = data[offset + 36];
        address[3] = data[offset + 37];
        this.address = InetAddress.getByAddress(address);
    }

    public String getCameraId() {
        return cameraId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String toString() {
        return getCameraId() + " (" + getCameraName() + ")";
    }
}
