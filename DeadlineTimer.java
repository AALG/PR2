public class DeadlineTimer implements Runnable {

    Thread mainThread;
    long deadline;

    public DeadlineTimer(Thread t, long deadline){
        this.deadline = deadline;
        mainThread = t;


    }


    public void run() {
        try{
            long currentTime = System.currentTimeMillis();
            Thread.sleep(deadline - currentTime);
            mainThread.interrupt();
        }catch(InterruptedException e){ }
    }

}
