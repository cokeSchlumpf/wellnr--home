package com.wellnr.home.framework;

import com.wellnr.common.Registration;
import com.wellnr.common.TypedObservable;
import com.wellnr.common.TypedObservableSupport;
import com.wellnr.common.TypedObserver;

import java.util.Optional;

public abstract class Device<T, D extends Device<T, D>> implements TypedObservable<DeviceStateChangedEvent<T, D>> {

    private final TypedObservableSupport<DeviceStateChangedEvent<T, D>> observableSupport = new TypedObservableSupport<>();

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
    public abstract Optional<T> getDeviceState();

    /**
     * Adds an observer to track state changes of the device.
     *
     * @param observer The observer to handle value changed events.
     * @return A registration object that can be used to remove the observer.
     */
    public Registration addObserver(TypedObserver<DeviceStateChangedEvent<T, D>> observer) {
        return observableSupport.addObserver(observer);
    }

    /**
     * Utili method to inform about state changes of the device.
     *
     * @param event The event/ new value to be handled.
     */
    @SuppressWarnings("unchecked")
    protected void fireEvent(T event, boolean fromDevice) {
        observableSupport.fireEvent(DeviceStateChangedEvent.apply((D) this, event, fromDevice));
    }

}
