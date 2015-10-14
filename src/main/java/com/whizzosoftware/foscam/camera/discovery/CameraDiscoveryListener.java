/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.discovery;

import java.net.InetAddress;

/**
 * A listener for Foscam camera discovery events.
 *
 * @author Dan Noguerol
 */
public interface CameraDiscoveryListener {
    /**
     * Callback when a camera is discovered.
     *
     * @param cameraId the ID of the camera that was discovered
     * @param cameraName the name of the camera that was discovered
     * @param address the source address of the discovered camera
     */
    void onCameraDiscovered(String cameraId, String cameraName, InetAddress address);
}
