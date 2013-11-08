public class DeadlineTimer implements Runnable {

    Thread mainThread;

    public DeadlineTimer(Thread t){
        mainThread = t;
        try{
            mainThread.checkAccess();        
        }catch(SecurityException e){ System.out.println("Denied!"); }
    }


    public void run() {
        try{
            Thread.sleep(10000);
            mainThread.interrupt();
        }catch(InterruptedException e){ }
    }

}
