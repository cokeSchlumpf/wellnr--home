package com.wellnr.home.framework.scenes;

import com.wellnr.home.framework.scenes.expressions.WhenExpression;

public final class Expressions {

    private Expressions() {}

    public static WhenExpression when(boolean condition, DeviceExpression... devices) {
        return WhenExpression.apply(condition, devices);
    }

}
