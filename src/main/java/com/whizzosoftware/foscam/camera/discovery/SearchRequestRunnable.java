/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.discovery;

/**
 * A Runnable that sends a search request via a SearchRequestSender instance.
 *
 * @author Dan Noguerol
 */
public class SearchRequestRunnable implements Runnable {
    private SearchRequestSender sender;

    /**
     * Constructor.
     *
     * @param sender the SearchRequestSender to use for sending
     */
    public SearchRequestRunnable(SearchRequestSender sender) {
        this.sender = sender;
    }

    @Override
    public void run() {
        sender.sendSearchRequest();
    }
}
