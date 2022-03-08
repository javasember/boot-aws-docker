package com.gys.lambda;

import java.util.function.Function;

import org.springframework.stereotype.Component;

@Component
public class TestFunction implements Function<String, String> {

    @Override
    public String apply(String t) {
        return t.toUpperCase();
    }
    
}
