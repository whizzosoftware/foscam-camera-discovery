/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.discovery;

/**
 * An interface for classes that can send a search request.
 *
 * @author Dan Noguerol
 */
public interface SearchRequestSender {
    /**
     * Send a search request.
     */
    void sendSearchRequest();
}
