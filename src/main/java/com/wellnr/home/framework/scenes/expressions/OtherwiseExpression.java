package com.wellnr.home.framework.scenes.expressions;

import com.wellnr.home.framework.scenes.Device;
import com.wellnr.home.framework.scenes.DeviceExpression;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * This class is used to repesent the result of
 */
@AllArgsConstructor(staticName = "apply")
public class OtherwiseExpression implements DeviceExpression {

    boolean condition;

    ConcatExpression whenTrue;

    ConcatExpression whenFalse;

    public static OtherwiseExpression apply(boolean condition, DeviceExpression ...expressions) {
        var whenTrue = ConcatExpression.apply(List.of(expressions));
        var whenFalse = ConcatExpression.apply(List.of());

        return apply(condition, whenTrue, whenFalse);
    }

    @Override
    public List<Device> getDevices() {
        if (condition) {
            return whenTrue.getDevices();
        } else {
            return List.of();
        }
    }

}
