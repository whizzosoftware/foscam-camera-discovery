/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

/**
 * A camera search request.
 *
 * From the documentation: "monitor user broadcast the command to the internet, to obtain the basic network
 * information of the camera connected in Lan."
 *
 * @author Dan Noguerol
 */
public class SearchRequest extends Order {
    /**
     * Constructor.
     */
    public SearchRequest() {
        super(
            (byte)0, // operation code == 0
            new byte[] {
                0, // reserve INT8
                0, // reserve INT8
                0, // reserve INT8
                1  // reserve INT8
            }
        );
    }
}
