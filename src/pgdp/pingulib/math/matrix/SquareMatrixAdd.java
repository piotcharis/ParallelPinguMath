package pgdp.pingulib.math.matrix;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Maximilian Anzinger
 */

public class SquareMatrixAdd {

    private static final int MIN_DIM = 1 << 1;

    /**
     * Sequential matrix addition of A and B
     *
     * @param A matrix
     * @param B matrix
     * @return result of the matrix addition in a new matrix
     */
    public static SquareMatrix addSequential(SquareMatrix A, SquareMatrix B) {
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
                C.set(i, j, A.get(i, j).add(B.get(i, j)));
            }
        }
        return C;
    }

    /**
     * Parallel matrix addition of A and B. If the dimensions are smaller or equal
     * than the default value <MIN_DIM>, the sequential matrix addition will be
     * applied.
     *
     * @param A matrix
     * @param B matrix
     * @return result of the matrix addition in a new matrix
     */
    public static SquareMatrix addParallel(SquareMatrix A, SquareMatrix B) {
        return addParallel(A, B, MIN_DIM);
    }

    /**
     * Parallel matrix addition of A and B. If <minDim> is smaller than the default
     * value <MIN_DIM>, the default value will be used. If the dimensions are
     * smaller or equal than <minDim>, the sequential matrix addition will be
     * applied.
     *
     * @param A      matrix
     * @param B      matrix
     * @param minDim dimension
     * @return result of the matrix addition in a new matrix
     */
    public static SquareMatrix addParallel(SquareMatrix A, SquareMatrix B, int minDim) {
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

        // Falls die Dimension schon kleiner gleich minDime ist, muss man keine parallele Addition durchführen.
        if (A.getDimension() <= minDime) {
            return addSequential(A, B);
        }

        // Array um das Ergebnis zu speichern.
        SquareMatrix[] result = new SquareMatrix[1];

        // Thread, um die Berechnung durchzuführen.
        AddComputeThread computeThread = new AddComputeThread(0, A, B, minDime, result);

        // Starten und Join des Threads
        computeThread.start();
        try {
            computeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Das Ergebnis wurde im Thread in Results gespeichert.
        return result[0];
    }

    private static class AddComputeThread extends ComputeThread {

        private static AtomicInteger computeThreadCounter = new AtomicInteger(0);

        /**
         * initialize compute thread for matrix addition.
         *
         * @param threadID index where result will be stored
         * @param A        first matrix
         * @param B        second matrix
         * @param minDim   threshold for sequential computation
         * @param results  array reference where the result will be stored
         */
        AddComputeThread(int threadID, SquareMatrix A, SquareMatrix B, int minDim, SquareMatrix[] results) {
            super(threadID, A, B, minDim, results);
            this.setName("AddComputeThread-" + computeThreadCounter.incrementAndGet());
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
                getResults()[getThreadID()] = addSequential(getA(), getB());
                return;
            }

            // Falls die Dimension größer als minDim ist, wird ein Array der Länge 4 erzeugt,
            // wo die Ergebnisse der 4 Quadranten der Matrix gespeichert werden sollen.
            SquareMatrix[] result = new SquareMatrix[4];

            // Es wird ein Thread für jedes Quadranten der Matrix erzeugt.
            AddComputeThread computeThread1 =
                    new AddComputeThread(0, getA().getQuadrant(1, 1), getB().getQuadrant(1, 1), getMinDim(),
                            result);
            AddComputeThread computeThread2 =
                    new AddComputeThread(1, getA().getQuadrant(1, 2), getB().getQuadrant(1, 2), getMinDim(),
                            result);
            AddComputeThread computeThread3 =
                    new AddComputeThread(2, getA().getQuadrant(2, 1), getB().getQuadrant(2, 1), getMinDim(),
                            result);
            AddComputeThread computeThread4 =
                    new AddComputeThread(3, getA().getQuadrant(2, 2), getB().getQuadrant(2, 2), getMinDim(),
                            result);

            // Alle Threads werden gestartet und joined.
            computeThread1.start();
            computeThread2.start();
            computeThread3.start();
            computeThread4.start();

            try {
                computeThread1.join();
                computeThread2.join();
                computeThread3.join();
                computeThread4.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Es wird eine neue Matrix aus den 4 Ergebnisses im result erzeugt und in der gegebenen
            // ID im gegebenen Array gespeichert.
            getResults()[getThreadID()] = new SquareMatrix(result[0], result[1], result[2], result[3]);
        }
    }
}
