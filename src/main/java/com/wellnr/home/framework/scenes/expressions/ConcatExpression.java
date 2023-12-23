package com.wellnr.home.framework.scenes.expressions;

import com.wellnr.home.framework.scenes.Device;
import com.wellnr.home.framework.scenes.DeviceExpression;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConcatExpression implements DeviceExpression {

    List<DeviceExpression> expressions;

    public static ConcatExpression apply(List<DeviceExpression> expressions) {
        return new ConcatExpression(List.copyOf(expressions));
    }

    public static ConcatExpression apply(DeviceExpression... expressions) {
        return apply(List.of(expressions));
    }

    @Override
    public List<Device> getDevices() {
        var result = new ArrayList<Device>();

        for (DeviceExpression expression : expressions) {
            result.addAll(expression.getDevices());
        }

        return result;
    }
}
