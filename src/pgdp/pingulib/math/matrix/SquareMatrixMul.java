package pgdp.pingulib.math.matrix;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Maximilian Anzinger
 */

public class SquareMatrixMul {

    private static final int MIN_DIM = 1 << 1;

    /**
     * Sequential matrix multiplication of A and B
     *
     * @param A matrix
     * @param B matrix
     * @return result of the matrix multiplication in a new matrix
     */
    public static SquareMatrix mulSequential(SquareMatrix A, SquareMatrix B) {
        if (A == null) {
            throw new IllegalArgumentException("A can not be null");
        }
        if (B == null) {
            throw new IllegalArgumentException("B can not be null");
        }
        if (A.getDimension() != B.getDimension()) {
            throw new IllegalArgumentException("A and B have different dimensions");
        }

        SquareMatrix C = new SquareMatrix(A.getDimension());

        for (int i = 1; i <= A.getDimension(); i++) {
            for (int j = 1; j <= A.getDimension(); j++) {
                for (int g = 1; g <= A.getDimension(); g++) {
                    C.set(i, j, C.get(i, j).add(A.get(i, g).multiply(B.get(g, j))));
                }
            }
        }

        return C;
    }

    /**
     * Parallel matrix multiplication of A and B. If the dimensions are smaller or
     * equal than the default value <MIN_DIM>, the sequential matrix multiplication
     * will be applied.
     *
     * @param A matrix
     * @param B matrix
     * @return result of the matrix multiplication in a new matrix
     */
    public static SquareMatrix mulParallel(SquareMatrix A, SquareMatrix B) {
        return mulParallel(A, B, MIN_DIM);
    }

    /**
     * Parallel matrix multiplication of A and B. If <minDim> is smaller than the
     * default value <MIN_DIM>, the default value will be used. If the dimensions
     * are smaller or equal than <minDim>, the sequential matrix multiplication will
     * be applied.
     *
     * @param A      matrix
     * @param B      matrix
     * @param minDim dimension
     * @return result of the matrix multiplication in a new matrix
     */
    public static SquareMatrix mulParallel(SquareMatrix A, SquareMatrix B, int minDim) {
        // Checks und Exceptions
        if (A == null) {
            throw new IllegalArgumentException("A can not be null");
        }
        if (B == null) {
            throw new IllegalArgumentException("B can not be null");
        }
        if (A.getDimension() != B.getDimension()) {
            throw new IllegalArgumentException("A and B have different dimensions");
        }

        // Wenn minDim kleiner als MIN_DIM ist, wird MIN_DIM genommen.
        int minDime = Math.max(minDim, MIN_DIM);

        // Falls die Dimension schon kleiner gleich minDime ist, muss man keine parallele Multiplikation durchführen.
        if (A.getDimension() <= minDime) {
            return mulSequential(A, B);
        }

        // Array um das Ergebnis zu speichern.
        SquareMatrix[] result = new SquareMatrix[1];

        // Thread, um die Berechnung durchzuführen.
        MulComputeThread computeThread = new MulComputeThread(0, A, B, minDime, result);

        // Starten und Join des Threads
        computeThread.start();
        try {
            computeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    private static class MulComputeThread extends ComputeThread {

        private static AtomicInteger computeThreadCounter = new AtomicInteger(0);

        /**
         * initialize compute thread for matrix multiplication.
         *
         * @param threadID index where result will be stored
         * @param A        first matrix
         * @param B        second matrix
         * @param minDim   threshold for sequential computation
         * @param results  array reference where the result will be stored
         */
        MulComputeThread(int threadID, SquareMatrix A, SquareMatrix B, int minDim, SquareMatrix[] results) {
            super(threadID, A, B, minDim, results);
            this.setName("MulComputeThread-" + computeThreadCounter.incrementAndGet());
        }

        /**
         * resets the thread counter
         */
        public static synchronized void resetThreadCount() {
            computeThreadCounter = new AtomicInteger(0);
        }

        /**
         * retrieves the current thread count
         *
         * @return thread count
         */
        public static AtomicInteger getThreadCount() {
            return computeThreadCounter;
        }

        @Override
        public void run() {
            // Base Case für die Rekursion.
            if (getA().getDimension() <= getMinDim()) {
                getResults()[getThreadID()] = mulSequential(getA(), getB());
                return;
            }

            // Falls die Dimension größer als minDim ist, wird ein Array der Länge 8 erzeugt,
            // wo die je 2 Ergebnisse der 4 Quadranten der Matrix gespeichert werden sollen.
            SquareMatrix[] result = new SquareMatrix[8];

            // Es wird ein Thread für jede Multiplikation der gegebenen Formel erzeugt.
            MulComputeThread computeThread1 =
                    new MulComputeThread(0, getA().getQuadrant(1, 1), getB().getQuadrant(1, 1), getMinDim(), result);
            MulComputeThread computeThread2 =
                    new MulComputeThread(1, getA().getQuadrant(1, 1), getB().getQuadrant(1, 2), getMinDim(), result);
            MulComputeThread computeThread3 =
                    new MulComputeThread(2, getA().getQuadrant(1, 2), getB().getQuadrant(2, 1), getMinDim(), result);
            MulComputeThread computeThread4 =
                    new MulComputeThread(3, getA().getQuadrant(1, 2), getB().getQuadrant(2, 2), getMinDim(), result);
            MulComputeThread computeThread5 =
                    new MulComputeThread(4, getA().getQuadrant(2, 1), getB().getQuadrant(1, 1), getMinDim(), result);
            MulComputeThread computeThread6 =
                    new MulComputeThread(5, getA().getQuadrant(2, 1), getB().getQuadrant(1, 2), getMinDim(), result);
            MulComputeThread computeThread7 =
                    new MulComputeThread(6, getA().getQuadrant(2, 2), getB().getQuadrant(2, 1), getMinDim(), result);
            MulComputeThread computeThread8 =
                    new MulComputeThread(7, getA().getQuadrant(2, 2), getB().getQuadrant(2, 2), getMinDim(), result);

            // Alle Threads werden gestartet und joined.
            computeThread1.start();
            computeThread2.start();
            computeThread3.start();
            computeThread4.start();
            computeThread5.start();
            computeThread6.start();
            computeThread7.start();
            computeThread8.start();

            try {
                computeThread1.join();
                computeThread2.join();
                computeThread3.join();
                computeThread4.join();
                computeThread5.join();
                computeThread6.join();
                computeThread7.join();
                computeThread8.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Die Ergebnisse im Result werden der Formel nach mittels addParallel addiert und daraus eine Matrix
            // gebildet, die im gegebenen ID der gegebenen result Array gespeichert wird.
            getResults()[getThreadID()] =
                    new SquareMatrix(SquareMatrixAdd.addParallel(result[0], result[2], getMinDim()),
                            SquareMatrixAdd.addParallel(result[1], result[3], getMinDim()),
                            SquareMatrixAdd.addParallel(result[4], result[6], getMinDim()),
                            SquareMatrixAdd.addParallel(result[5], result[7], getMinDim()));
        }
    }
}
