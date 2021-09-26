package ru.mipt.ca.factorial;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Factorial {

    private static final int NUM_WORKERS = Runtime.getRuntime().availableProcessors();

    private final int factorialArg;
    private final AtomicInteger batchesStarted = new AtomicInteger();
    private final long[] intermediateResult;
    private final AtomicInteger batchesFinishedCounter;
    private long result;
    private boolean ready;

    Factorial(final int numWorkers, final int factorialArg) {
        if (numWorkers < 1) {
            throw new IllegalArgumentException();
        }
        this.factorialArg = factorialArg;
        this.intermediateResult = new long[numWorkers];
        this.batchesFinishedCounter = new AtomicInteger();
    }


    public static void main(String[] args) throws InterruptedException {
        if (args.length == 1) {
            System.out.println(new Factorial(Integer.parseInt(args[0]), NUM_WORKERS).compute());
        } else if (args.length == 2) {
            System.out.println(
                    new Factorial(Integer.parseInt(args[0]), Integer.parseInt(args[1])).compute()
            );
        } else {
            throw new IllegalArgumentException(
                    "Bad command line args passed. Format - <argument for factorial> [<num workers>]."
            );
        }
    }

    long compute() throws InterruptedException {
        if (factorialArg <= 1) {
            return 1;
        }
        for (int i = 0; i < getNumWorkers() - 1; i++) {
            new Thread(this::runFactorialComputation).start();
        }
        runFactorialComputation();
        synchronized (this) {
            while (!ready) {
                this.wait();
            }
        }
        return result;
    }

    private void runFactorialComputation() {
        for (int i = 0; i < getNumWorkers(); i++) {
            final int nextBatch = batchesStarted.incrementAndGet();
            if (nextBatch > getNumWorkers()) {
                return;
            }
            computePartialFactorial(nextBatch);
            if (!(batchesFinishedCounter.incrementAndGet() == getNumWorkers())) {
                continue;
            }
            result = Arrays.stream(intermediateResult).reduce(1, Long::sum);
            synchronized (this) {
                ready = true;
                this.notify();
            }
        }
    }

    private int getNumWorkers() {
        return intermediateResult.length;
    }

    private void computePartialFactorial(final int batchNumber) {
        long result = 1;
        int counter = batchNumber;
        while (counter <= factorialArg) {
            result *= counter;
            counter += getNumWorkers();
        }
        intermediateResult[batchNumber - 1] = result;
    }
}
