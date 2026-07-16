package com.tangwenjun.dragonbarrelroll.math;

import java.util.Map;

public interface Expression {
    double eval(Map<String, Double> vars);
}
