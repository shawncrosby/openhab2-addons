package org.openhab.binding.wink.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.wink.client.IWinkClient;
import org.openhab.binding.wink.client.IWinkDevice;
import org.openhab.binding.wink.client.WinkClient;
import org.openhab.binding.wink.client.WinkSupportedDevice;
import org.openhab.binding.wink.internal.discovery.WinkDeviceDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WinkHub2BridgeHandler extends BaseBridgeHandler {

    private IWinkClient client = WinkClient.getInstance();
    private final static Logger logger = LoggerFactory.getLogger(WinkHub2BridgeHandler.class);

    public WinkHub2BridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        WinkDeviceDiscoveryService discovery = new WinkDeviceDiscoveryService(this);

        this.bundleContext.registerService(DiscoveryService.class, discovery, null);

        this.scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                // TODO
                // connect();
            }
        }, 0, TimeUnit.SECONDS);
        super.initialize();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub
        logger.debug("Initializing Bridge handler for winkhub");
    }

    public void setDesiredState(IWinkDevice device, Map<String, String> updatedState) {
        logger.debug("Setting device state: {}", updatedState);
        client.updateDeviceState(device, updatedState);
    }

    public void switchOnDevice(IWinkDevice device) {
        logger.debug("Switching on Device {}", device);
        Map<String, String> updatedState = new HashMap<String, String>();
        updatedState.put("powered", "true");
        this.setDesiredState(device, updatedState);
    }

    public void switchOffDevice(IWinkDevice device) {
        Map<String, String> updatedState = new HashMap<String, String>();
        updatedState.put("powered", "false");
        this.setDesiredState(device, updatedState);
    }

    public void lockDevice(IWinkDevice device) {
        Map<String, String> updatedState = new HashMap<String, String>();
        updatedState.put("locked", "true");
        this.setDesiredState(device, updatedState);
    }

    public void unLockDevice(IWinkDevice device) {
        Map<String, String> updatedState = new HashMap<String, String>();
        updatedState.put("locked", "false");
        this.setDesiredState(device, updatedState);
    }

    public void setDeviceDimmerLevel(IWinkDevice device, int level) {
        Map<String, String> updatedState = new HashMap<String, String>();
        if (level > 0) {
            updatedState.put("powered", "true");
            updatedState.put("brightness", String.valueOf(level));
        } else {
            updatedState.put("powered", "false");
        }
        this.setDesiredState(device, updatedState);
    }

    public IWinkDevice getDevice(WinkSupportedDevice deviceType, String uuid) {
        logger.debug("Getting device through handler {}", uuid);
        return client.getDevice(deviceType, uuid);
    }

}
