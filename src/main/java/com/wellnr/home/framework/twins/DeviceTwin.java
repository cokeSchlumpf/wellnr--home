package com.wellnr.home.framework.twins;

import com.wellnr.common.Registration;
import com.wellnr.common.TypedObservable;
import com.wellnr.common.TypedObservableSupport;
import com.wellnr.common.TypedObserver;
import com.wellnr.home.framework.scenes.Device;

import java.util.Optional;

public abstract class DeviceTwin<VALUE, DEVICE extends Device, TWIN extends DeviceTwin<VALUE, DEVICE, TWIN>> implements TypedObservable<DeviceStateChangedEvent<VALUE, TWIN>> {

    private final TypedObservableSupport<DeviceStateChangedEvent<VALUE, DEVICE, TWIN>> observableSupport = new TypedObservableSupport<>();

    /**
     * Returns the unique name of a device.
     *
     * @return The name of the device.
     */
    public abstract String getDeviceName();

    /**
     * Returns the current state of the device.
     * The state may be empty if the digital twin is not initialized or connected.
     *
     * @return The current state of the device.
     */
    public abstract Optional<VALUE> getDeviceState();

    /**
     * Adds an observer to track state changes of the device.
     *
     * @param observer The observer to handle value changed events.
     * @return A registration object that can be used to remove the observer.
     */
    public Registration addObserver(TypedObserver<DeviceStateChangedEvent<VALUE, TWIN>> observer) {
        return observableSupport.addObserver(observer);
    }

    /**
     * Utility method to inform about state changes of the device.
     *
     * @param event The event/ new value to be handled.
     */
    @SuppressWarnings("unchecked")
    protected void fireEvent(VALUE event, boolean fromDevice) {
        observableSupport.fireEvent(DeviceStateChangedEvent.apply((TWIN) this, event, fromDevice));
    }

    public abstract void update(DEVICE device);

}
