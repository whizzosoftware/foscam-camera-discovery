/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.foscam.camera.protocol;

import java.util.ArrayList;
import java.util.List;

public class MockProtocolParserListener implements ProtocolParserListener {
    private List<Order> orders = new ArrayList<Order>();

    @Override
    public void onOrder(Order order) {
        orders.add(order);
    }

    public void clear() {
        orders.clear();
    }

    public List<Order> getOrders() {
        return orders;
    }
}
