/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.wink.internal.discovery;

import static org.openhab.binding.wink.WinkBindingConstants.BINDING_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.wink.client.IWinkDevice;
import org.openhab.binding.wink.client.WinkClient;
import org.openhab.binding.wink.handler.WinkHub2Handler;
import org.openhab.binding.wink.internal.WinkHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WinkDeviceDiscoveryService extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(WinkDeviceDiscoveryService.class);
    private WinkHub2Handler hubHandler;

    public WinkDeviceDiscoveryService(WinkHub2Handler hubHandler) throws IllegalArgumentException {
        super(WinkHandlerFactory.DISCOVERABLE_DEVICE_TYPES_UIDS, 10);

        this.hubHandler = hubHandler;
    }

    private ScheduledFuture<?> scanTask;

    @Override
    protected void startScan() {
        if (this.scanTask == null || this.scanTask.isDone()) {
            this.scanTask = scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    List<IWinkDevice> devices = WinkClient.getInstance().listDevices();
                    ThingUID bridgeThingId = hubHandler.getThing().getBridgeUID();
                    for (IWinkDevice device : devices) {
                        ThingUID thingId = new ThingUID(
                                new ThingTypeUID(BINDING_ID, device.getDeviceType().getDeviceType()), device.getId());
                        Map<String, Object> props = new HashMap<String, Object>();
                        props.put("uuid", device.getId());

                        DiscoveryResult result = DiscoveryResultBuilder.create(thingId).withLabel(device.getName())
                                .withProperties(props).withBridge(bridgeThingId).build();
                        thingDiscovered(result);
                        logger.debug("Discovered Thing: {}", thingId);
                    }
                }
            }, 0, TimeUnit.SECONDS);
        }
    }

}
