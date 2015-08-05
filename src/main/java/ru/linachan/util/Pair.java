package ru.linachan.util;

public class Pair<A, B> {
    public A key;
    public B value;

    public Pair(A a, B b) {
        this.key = a;
        this.value = b;
    }
}
