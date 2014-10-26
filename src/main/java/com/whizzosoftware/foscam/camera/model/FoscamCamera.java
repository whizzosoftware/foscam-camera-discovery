/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.model;

import java.net.InetAddress;

/**
 * A model object representing a camera.
 *
 * @author Dan Noguerol
 */
public class FoscamCamera {
    private String id;
    private String name;
    private InetAddress address;

    public FoscamCamera(String id, String name, InetAddress address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }
}
