package com.ntf.grpc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class SwapTest {

    public void swap(int num1, int num2) {
        int temp = num1;
        num1 = num2;
        num2 = temp;
    }

    @Test
    public void testSwap() {
        int a = 10;
        int b = 20;
        swap(a, b);
        System.out.println("a = " + a);
        System.out.println("b = " + b);

        ArrayList<Integer> list = new ArrayList<>();
        Arrays.sort(new int[]{1,5,-2});
    }
}
