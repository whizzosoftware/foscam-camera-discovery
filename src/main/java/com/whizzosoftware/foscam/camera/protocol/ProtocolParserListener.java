/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

/**
 * A listener for Order messages from the parser.
 *
 * @author Dan Noguerol
 */
public interface ProtocolParserListener {
    /**
     * Callback when an Order is received.
     *
     * @param order the received Order object
     */
    public void onOrder(Order order);
}
