package com.example.panacea;

import java.util.Date;
public class Pixels<T> {
    final Date timestamp;
    final T reading;
    Pixels(Date timestamp, T reading) {
        this.timestamp = timestamp;
        this.reading = reading;
    }
}