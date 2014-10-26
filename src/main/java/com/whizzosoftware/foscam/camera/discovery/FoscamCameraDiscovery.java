/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.discovery;

import com.whizzosoftware.foscam.camera.model.FoscamCamera;
import com.whizzosoftware.foscam.camera.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

/**
 * A class that provides network discovery of Foscam cameras.
 *
 * @author Dan Noguerol
 */
public class FoscamCameraDiscovery implements Runnable, ProtocolParserListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DatagramSocket socket;
    private FoscamCameraDiscoveryListener listener;
    private ProtocolParser parser;
    private Thread discoveryThread;

    public FoscamCameraDiscovery(FoscamCameraDiscoveryListener listener) throws SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);

        this.listener = listener;

        parser = new ProtocolParser(this);
    }

    /**
     * Start the discovery process.
     *
     * @throws IOException on failure
     */
    public void start() throws IOException {
        discoveryThread = new Thread(this);
        discoveryThread.setName("Foscam Camera Discovery");
        discoveryThread.start();

        sendSearchRequest();
    }

    /**
     * Stop the discovery process.
     */
    public void stop() {
        discoveryThread.interrupt();
        socket.close();
    }

    /**
     * Send a new search request.
     *
     * @throws IOException on failure
     */
    public void sendSearchRequest() throws IOException {
        SearchRequest searchReq = new SearchRequest();
        byte[] b = searchReq.toBytes();
        socket.send(new DatagramPacket(b, 0, b.length, InetAddress.getByName("255.255.255.255"), 10000));
    }

    @Override
    public void run() {
        logger.debug("Starting discovery thread");

        while (!Thread.currentThread().isInterrupted()) {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            logger.trace("Waiting for data");
            try {
                socket.receive(packet);
                logger.trace("Received {} bytes with offset {}", packet.getLength(), packet.getOffset());
                parser.addBytes(packet.getData(), packet.getOffset(), packet.getLength());
            } catch (SocketException e) {
                if (!socket.isClosed()) {
                    logger.error("Error receiving from socket", e);
                }
                break;
            } catch (IOException ioe) {
                logger.error("Error reading from socket", ioe);
            } catch (Exception e) {
                logger.error("Error processing camera discovery", e);
            }
        }

        logger.debug("Discovery thread exiting");
    }

    @Override
    public void onOrder(Order order) {
        if (order instanceof SearchResponse) {
            SearchResponse sr = (SearchResponse)order;
            if (listener != null) {
                listener.onCameraDiscovered(new FoscamCamera(sr.getCameraId(), sr.getCameraName(), sr.getAddress()));
            }
        }
    }
}
