/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.discovery;

import com.whizzosoftware.foscam.camera.protocol.Order;
import com.whizzosoftware.foscam.camera.protocol.SearchResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * A handler responsible for receiving inbound order objects and invoking the appropriate callback methods
 * on a CameraDiscoveryListener.
 *
 * @author Dan Noguerol
 */
public class InboundOrderHandler extends SimpleChannelInboundHandler<Order> {
    private CameraDiscoveryListener listener;

    public InboundOrderHandler(CameraDiscoveryListener listener) {
        this.listener = listener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Order order) throws Exception {
        if (listener != null) {
            if (order instanceof SearchResponse) {
                SearchResponse sr = (SearchResponse)order;
                listener.onCameraDiscovered(sr.getCameraId(), sr.getCameraName(), sr.getAddress());
            }
        }
    }
}
