package ru.mipt.ca.factorial;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactorialTest {

    @Test
    public void testNegative() throws InterruptedException {
        Assertions.assertEquals(
                1,
                new Factorial(3, -2).compute()
        );
    }

    @Test
    public void testNegativeWorkers() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> new Factorial(-1, 1)
        );
    }

    @Test
    public void testNormalCase() throws InterruptedException {
        Assertions.assertEquals(
                factorial(10),
                new Factorial(3, 10).compute()
        );
    }

    private static long factorial(final int arg) {
        long result = 1;
        for (int i = 1; i <= arg; i++) {
            result *= i;
        }
        return result;
    }
}
