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
            Thread.sleep(1400);
            mainThread.interrupt();
        }catch(InterruptedException e){ }
    }

}
