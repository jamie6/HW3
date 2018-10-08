/*
2)	MATRIX MULTIPLICATION
Write a method matmult(float A, float B, float C, int m, int n, int p) 
that multiplies the m x n matrix A by the n x p matrix B to give the m x p matrix C.  
To make the program execute faster in a multiprocessor environment, 
use multiple threads to speed up the execution. Vary the number of threads 
using 1, 2, 4, 8. Also vary the size of the matrices above so that you can get a 
feel of how increasing threads will help to a limit.

b)	EXAMPLE OUTPUT
Time with 1 thread: 15sec
Time with 2 thread: 8 sec
Then vary matrix size…indicate size and print out times.

 */
/**
 *
 * @author Jamie
 */
public class MatrixMultiplication
{
    public static void main(String[] args)
    {
        long start, end;
        for ( int i = 0; i < 4; i++ )
        {
            //A is mxn
            //B is nxp
            //C is mxp
            int m = 500+(100*i);
            int n = 1000+(100*i);
            int p = 1000+(100*i);
            float[][] A = generateRandomMatrix(m, n);
            float[][] B = generateRandomMatrix(n, p);
            float[][] C = new float[m][p];
            
            System.out.println("Matrix Dimensions: m = " + m + ", n = " + n + ", p = " + p );
            for ( int k = 1; k <= 4; k++ )
            {
                start = System.currentTimeMillis();
                matmult(A, B, C, m, n, p, k);
                end = System.currentTimeMillis();
                System.out.println("Time with " + k + " thread(s): " + (end-start) + "ms");
            }
        }
    }
    
    public static void matmult(float[][] A, float[][] B, float[][] C, int m, int n, int p, int threadCount)
    {
        //A is mxn
        //B is nxp
        //C is mxp
        int partitionSize;
        if ( m % threadCount != 0 )
        {
            partitionSize = (int)(Math.floor((double)m/threadCount)+1);
        }
        else
        {
            partitionSize = m/threadCount;
            Thread[] threads = new Thread[threadCount];
            for ( int i = 0; i < threadCount; i++ )
            {
                threads[i] = new Thread(new MatMult(A, B, C, m, n, p, i*partitionSize, (i+1)*partitionSize-1));
                threads[i].start();
            }
            for ( int count = 0; ; count = 0 )
            {
                for ( int i = 0; i < threads.length; i++ )
                {
                    if ( !threads[i].isAlive() ) count ++;
                }
                if ( count == threads.length ) break; // break when none of the threads are still alive
            }
        }
    }
    
    public static class MatMult implements Runnable
    {
        float[][] A, B, C;
        int m, n, p, start, end;
        public MatMult(float[][] A, float[][] B, float[][] C, int m, int n, int p, int start, int end)
        {
            this.A = A;
            this.B = B;
            this.C = C;
            this.m = m;
            this.n = n;
            this.p = p;
            this.start = start;
            this.end = end;
        }
        public void run()
        {
            matmultHelper();
        }
        public void matmultHelper()
        {
            for ( int i = start; i < end; i++ )
            {
                for ( int j = 0; j < p; j++ )
                {
                    for ( int k = 0; k < n; k++ )
                    {
                        C[i][j] += A[i][k] * B[k][j];
                    }
                }
            }
        }
    }
    
    public static float[][] generateRandomMatrix(int m, int n)
    {
        java.util.Random rand = new java.util.Random();
        float[][] R = new float[m][n];
        for ( int i = 0; i < R.length; i++ )
        {
            for ( int j = 0; j < R[i].length; j++ )
            {
                R[i][j] = rand.nextFloat();
            }
        }
        return R;
    }
    
    public static void printMatrix(float[][] R)
    {
        for ( int i = 0; i < R.length; i++ )
        {
            for ( int j = 0; j < R[i].length; j++ )
            {
                System.out.print(R[i][j] + " " );
            }
            System.out.println();
        }
    }
}
