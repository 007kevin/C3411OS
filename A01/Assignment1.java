import java.util.ArrayList;

class InvalidOperandException extends Exception {
  InvalidOperandException(){}
  InvalidOperandException(String msg){
    super(msg);
  }
}

class Worker implements Runnable {
  double[][] A,B,C;
  int r,c;
  Worker(double[][] A, double[][] B, double[][] C, int r, int c){
    this.A = A;
    this.B = B;
    this.C = C;
    this.r = r;
    this.c = c;
  }

  @Override
  public void run(){
    int v = 0;
    int n = A[0].length;
    for (int i = 0; i < n; ++i){
      v+=A[r][i]*B[i][c];
    }
    C[r][c] = v;
  }
}

class Matrix {
  private double[][] M;
  Matrix(double[][] M){
    this.M = M;
  }

  public Matrix multiply(Matrix A) throws InvalidOperandException {
    if (this.M[0].length != A.M.length)
      throw new InvalidOperandException("matrix dimensions do not match");
    double[][] C = new double[this.M.length][A.M[0].length];
    ArrayList<Thread> tl = new ArrayList<Thread>();
    for (int i = 0; i < C.length; ++i){
      for (int j = 0; j < C[0].length; ++j){
        int n = tl.size();
        tl.add(new Thread(new Worker(this.M,A.M,C,i,j),
                          "Thread " + String.valueOf(n)));
        tl.get(n).start();
      }
    }
    try {
      for (Thread t : tl)
        t.join();
    }
    catch (InterruptedException ie){
      System.out.println(ie.getMessage());
    }
    return new Matrix(C);
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < this.M.length; ++i){
      s.append("[");
      for (int j = 0; j < this.M[0].length; ++j){
        s.append(String.valueOf(M[i][j])+"\t");
      }
      s.deleteCharAt(s.length()-1);
      s.append("]\n");
    }
    s.deleteCharAt(s.length()-1);    
    return s.toString();
  }
  
}

class Assignment1 {
  public static void main(String args[]){
    double[][] m1 = {{1,2,3,4,5},
                     {6,7,8,9,10}};
 
    double[][] m2 = {{1,2},
                     {3,4},
                     {5,6},
                     {7,8},
                     {9,10}};
    Matrix A = new Matrix(m1);
    Matrix B = new Matrix(m2);

    try {
      Matrix C = A.multiply(B);
      System.out.println("Matrix A");
      System.out.println(A);
      System.out.println("\n\t+\n");
      System.out.println("Matrix B");      
      System.out.println(B);
      System.out.println("\n\t=\n");
      System.out.println("Matrix C");      
      System.out.println(C);
    }
    catch (Exception e){
      System.out.println("Error: " + e.getMessage());
    }
  }
}
