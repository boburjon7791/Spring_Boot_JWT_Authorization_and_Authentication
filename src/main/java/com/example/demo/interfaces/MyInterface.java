package com.example.demo.interfaces;

import com.example.demo.exception.IllegalParamException;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class MyInterface<T> {


    public T requireNonNullElseThrow(T storeName) throws NoSuchMethodException {
        if (Objects.isNull(storeName)) {
            throw new IllegalParamException();
        }
        try {
            String s = (String) storeName;
            if (s.isBlank()) {
                throw new IllegalParamException();
            }
        }catch (ClassCastException e){
//            e.printStackTrace();
            return storeName;
        }
        return storeName;
    }
}
