package com.wellnr.home.framework.scenes;

public interface Switch<T extends Switch<T>> extends Device {

    T turnedOn();

    T turnedOff();

    boolean isOn();

}
