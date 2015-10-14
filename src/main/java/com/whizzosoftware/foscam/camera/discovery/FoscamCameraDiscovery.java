/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.discovery;

import com.whizzosoftware.foscam.camera.protocol.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * A class that provides network discovery of Foscam cameras. It performs the following functions:
 *
 * 1. Send 3 discovery search requests spaced SEARCH_REQUEST_INITIAL_FREQUENCY_SECONDS apart. This is to reduce the
 *    possibility that a working camera misses a search request since UDP is unreliable.
 * 2. Send a discovery search request every SEARCH_REQUEST_FREQUENCY_SECONDS apart. This is to keep tabs on cameras
 *    as they come and go.
 * 3. Invoke the onCameraDiscovered method of the specified CameraDiscoveryListener whenever a search response is
 *    received. It is up to the listener to recognize and ignore duplicate responses.
 *
 * @author Dan Noguerol
 */
public class FoscamCameraDiscovery implements SearchRequestSender {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static int SEARCH_REQUEST_INITIAL_FREQUENCY_SECONDS = 2;
    private final static int SEARCH_REQUEST_FREQUENCY_SECONDS = 60;

    private DatagramChannel channel;
    private CameraDiscoveryListener listener;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private SearchRequestRunnable searchRequestRunnable;
    private ScheduledFuture searchFuture;

    /**
     * Constructor.
     *
     * @param listener the listener to invoke when cameras are discovered
     */
    public FoscamCameraDiscovery(CameraDiscoveryListener listener) {
        this.listener = listener;

        group = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap().
            group(group).
            channel(NioDatagramChannel.class).
            option(ChannelOption.SO_BROADCAST, true);
    }

    /**
     * Start the discovery process.
     *
     * @throws IOException on failure
     */
    public void start() throws IOException {
        searchRequestRunnable = new SearchRequestRunnable(this);

        // set up the inbound channel handler
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new DatagramToByteBufHandler()); // convert an incoming DatagramPacket into a ByteBuf
                pipeline.addLast(new OrderDecoder()); // convert an incoming ByteBuf into an Order
                pipeline.addLast(new InboundOrderHandler(listener)); // handle incoming Orders
            }
        });

        // bind to the address
        ChannelFuture cf = bootstrap.bind(new InetSocketAddress(0));
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    channel = (DatagramChannel)channelFuture.channel();

                    // perform initial search request
                    group.execute(searchRequestRunnable);

                    // schedule two quick follow-up search requests to make sure a camera didn't miss the first request
                    group.schedule(searchRequestRunnable, SEARCH_REQUEST_INITIAL_FREQUENCY_SECONDS, TimeUnit.SECONDS);
                    group.schedule(searchRequestRunnable, SEARCH_REQUEST_INITIAL_FREQUENCY_SECONDS * 2, TimeUnit.SECONDS);

                    // set up a recurring search request so we can keep track of cameras coming/going
                    searchFuture = group.scheduleAtFixedRate(
                        searchRequestRunnable,
                        SEARCH_REQUEST_INITIAL_FREQUENCY_SECONDS * 2 + SEARCH_REQUEST_FREQUENCY_SECONDS,
                        SEARCH_REQUEST_FREQUENCY_SECONDS,
                        TimeUnit.SECONDS
                    );
                } else {
                    logger.error("Bind attempt failed", channelFuture.cause());
                }
            }
        });
    }

    /**
     * Stop the discovery process.
     */
    public void stop() {
        searchFuture.cancel(true);
        channel.close();
    }

    /**
     * Send a new search request.
     */
    public void sendSearchRequest() {
        SearchRequest searchReq = new SearchRequest();
        logger.debug("Sending search request");
        channel.writeAndFlush(new DatagramPacket(searchReq.toByteBuf(), new InetSocketAddress("255.255.255.255", 10000)));
    }
}
