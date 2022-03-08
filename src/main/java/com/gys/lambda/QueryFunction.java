package com.gys.lambda;

import java.util.function.Function;

import org.springframework.stereotype.Component;

@Component
public class QueryFunction implements Function<String, Person> {
    
    @Override
    public Person apply(String t) {
        return new Person(0l, t, "test@email.com");
    }
}