/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.wink.handler;

import static org.openhab.binding.wink.WinkBindingConstants.CHANNEL_LIGHTLEVEL;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.wink.client.IWinkDevice;
import org.openhab.binding.wink.client.WinkSupportedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: The {@link LightBulbHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Sebastien Marchand - Initial contribution
 */
public class LightBulbHandler extends WinkBaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(LightBulbHandler.class);

    public LightBulbHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_LIGHTLEVEL)) {
            if (command instanceof Number) {
                logger.debug("Setting brightness {}", command);
                int level = ((Number) command).intValue();
                setLightLevel(level);
            } else if (command.equals(OnOffType.ON)) {
                logger.debug("Setting full power");
                setLightLevel(100);
            } else if (command.equals(OnOffType.OFF)) {
                logger.debug("Turning off light");
                setLightLevel(0);
            } else if (command instanceof RefreshType) {
                logger.debug("Refreshing state");
                updateDeviceState(getDevice());
            }
        }
    }

    private void setLightLevel(int level) {
        if (level > 0) {
            bridgeHandler.switchOnDevice(getDevice());
            bridgeHandler.setDeviceDimmerLevel(getDevice(), level);
        } else {
            bridgeHandler.switchOffDevice(getDevice());
        }

    }

    @Override
    protected WinkSupportedDevice getDeviceType() {
        return WinkSupportedDevice.DIMMABLE_LIGHT;
    }

    @Override
    protected void updateDeviceState(IWinkDevice device) {

        final String desired_brightness = device.getDesiredState().get("brightness");
        logger.debug("New Desired Brightness: {}", desired_brightness);
        final String current_brightness = device.getCurrentState().get("brightness");
        logger.debug("Current Brightness: {}", current_brightness);
        if (desired_brightness != null && desired_brightness.equals(current_brightness)) {
            Float brightness = Float.valueOf(current_brightness) * 100;
            logger.debug("New brightness state: {}", brightness);
            updateState(CHANNEL_LIGHTLEVEL, new PercentType(brightness.intValue()));
        }
    }

}
