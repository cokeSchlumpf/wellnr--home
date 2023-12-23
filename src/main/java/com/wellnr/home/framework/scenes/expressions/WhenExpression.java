package com.wellnr.home.framework.scenes.expressions;

import com.wellnr.home.framework.scenes.Device;
import com.wellnr.home.framework.scenes.DeviceExpression;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * This class is used to repesent the result of
 */
@AllArgsConstructor(staticName = "apply")
public class WhenExpression implements DeviceExpression {

    boolean condition;

    ConcatExpression whenTrue;

    public static WhenExpression apply(boolean condition, DeviceExpression ...expressions) {
        var whenTrue = ConcatExpression.apply(List.of(expressions));
        return apply(condition, whenTrue);
    }

    @Override
    public List<Device> getDevices() {
        if (condition) {
            return whenTrue.getDevices();
        } else {
            return List.of();
        }
    }

    public OtherwiseExpression otherwise(DeviceExpression ...defaultExpressions) {
        return OtherwiseExpression.apply(condition, whenTrue, ConcatExpression.apply(defaultExpressions));
    }

}
