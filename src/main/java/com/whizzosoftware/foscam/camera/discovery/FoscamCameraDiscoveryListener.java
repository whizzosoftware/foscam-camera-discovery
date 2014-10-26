/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.discovery;

import com.whizzosoftware.foscam.camera.model.FoscamCamera;

/**
 * A listener for Foscam camera discovery events.
 *
 * @author Dan Noguerol
 */
public interface FoscamCameraDiscoveryListener {
    /**
     * Callback when a camera is discovered.
     *
     * @param camera the camera that was discovered
     */
    public void onCameraDiscovered(FoscamCamera camera);
}
