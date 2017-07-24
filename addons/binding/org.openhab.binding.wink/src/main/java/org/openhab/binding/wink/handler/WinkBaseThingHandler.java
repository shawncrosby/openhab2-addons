package org.openhab.binding.wink.handler;

import java.util.Arrays;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.openhab.binding.wink.client.IWinkDevice;
import org.openhab.binding.wink.client.JsonWinkDevice;
import org.openhab.binding.wink.client.WinkSupportedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

public abstract class WinkBaseThingHandler extends BaseThingHandler {
    public WinkBaseThingHandler(Thing thing) {
        super(thing);
    }

    private static final Logger logger = LoggerFactory.getLogger(WinkBaseThingHandler.class);

    protected WinkHub2BridgeHandler bridgeHandler;
    protected PubNub pubnub;

    @Override
    public void initialize() {
        logger.debug("Initializing Device {}", getThing());
        bridgeHandler = (WinkHub2BridgeHandler) getBridge().getHandler();
        if (getThing().getConfiguration().get("uuid") == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "UUID must be specified in Config");
        } else {
            if (getDevice().getCurrentState().get("connection").equals("true")) {
                updateStatus(ThingStatus.ONLINE);
                registerToPubNub();
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Device Not Connected");
            }
        }
        super.initialize();
    }

    /**
     * Subclasses must define the correct wink supported device type
     *
     * @return Enum from WinkSupportedDevice for this device
     */
    protected abstract WinkSupportedDevice getDeviceType();

    protected IWinkDevice getDevice() {
        return bridgeHandler.getDevice(getDeviceType(), getThing().getConfiguration().get("uuid").toString());
    }

    /**
     * Subclasses must define the properties of the current device state that are relevant.
     *
     * @param device
     */
    protected abstract void updateDeviceState(IWinkDevice device);

    protected void registerToPubNub() {
        logger.debug("Doing the PubNub registration for :\n{}", thing.getLabel());

        IWinkDevice device = getDevice();

        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(device.getPubNubSubscriberKey());

        this.pubnub = new PubNub(pnConfiguration);
        this.pubnub.addListener(new SubscribeCallback() {
            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                JsonParser parser = new JsonParser();
                JsonObject jsonMessage = parser.parse(message.getMessage().getAsString()).getAsJsonObject();
                IWinkDevice device = new JsonWinkDevice(jsonMessage);
                logger.debug("Received update from device: {}", device);
                updateDeviceState(device);
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            }

            @Override
            public void status(PubNub arg0, PNStatus status) {
                if (status.isError()) {
                    logger.error("PubNub communication error: {}", status.getStatusCode());
                } else {
                    logger.trace("PubNub status: no error.");
                }
            }
        });

        this.pubnub.subscribe().channels(Arrays.asList(device.getPubNubChannel())).execute();
    }

}
