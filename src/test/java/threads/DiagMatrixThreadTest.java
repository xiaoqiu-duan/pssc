package threads;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GYZ
 * @DESCRIPTION Test of the speedup of building diagonal matrix
 * The actual speedup is depending on your hardware configuration
 * You can set threads' number according to your Core and CPU
 * @create 2018-01-23 18:40
 * @create 2018-01-29 12:36
 **/
public class DiagMatrixThreadTest {

  /**
   *  Similar matrix (the input matrix of building diagonal matrix)
   */
  private static double[][] W;
  /**
   * The length of Similar Matrix
   */
  private static int LEN = 10000;

  // init Similar matrix randomly
  static {
    Random random = new Random();
    W = new double[LEN][LEN];
    for(int i = 0; i < LEN; i++){
      for(int j = 0; j <= i; j++){
        W[i][j] = random.nextDouble() * 100;
        W[j][i] = W[i][j];
      }
    }
  }

  private static long parallelCompDiagMatrix (int thNum) throws  IOException, InterruptedException{
    System.out.println("Starting parallel computing");
    long t1 = System.currentTimeMillis();
    double[] D = new double[LEN];
    // Threads pool
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    CountDownLatch threadSignal = new CountDownLatch(thNum);
    // Starting calculating
    for(int i = 0; i < thNum; i++){
      cachedThreadPool.execute(new DiagMatrixThread(D, W, (i * LEN) / thNum,
          (i + 1) * LEN / thNum, threadSignal));
    }
    threadSignal.await();
    long t2 = System.currentTimeMillis();
    // Ending multi threading calculating
    System.out.println("Ending multi threading calculating");
    cachedThreadPool.shutdown();
    return t2 - t1;
  }

  private static long serialCompDiagMatrix() throws IOException, InterruptedException{
    System.out.println("Starting serial computing");
    long t1 = System.currentTimeMillis();
    double[] D = new double[LEN];
    CountDownLatch singleSignal = new CountDownLatch(1);
    Thread th = new DiagMatrixThread(D, W, 0, LEN, singleSignal);
    th.start();
    singleSignal.await();
    long t2 = System.currentTimeMillis();
    System.out.println("Ending serial computing");
    return t2 - t1;
  }


  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("-----START TEST-----");
    long pTime = parallelCompDiagMatrix(4);
    long sTime = serialCompDiagMatrix();
    System.out.println("Speedup = " + 1.0 *  sTime / pTime );
    System.out.println("-----END TEST-------");
  }

}
