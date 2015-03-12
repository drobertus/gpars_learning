package test.gpars.server

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ThreadedSocketServer implements Runnable {

    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    //protected Thread       runningThread = null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

    def receivedMessages = []

    public ThreadedSocketServer(int port){
        this.serverPort = port;
    }

    public void run(){
        //synchronized(this){
        //     this.runningThread = Thread.currentThread();
        //}
        openServerSocket();
        while(! isStopped()){
            println "server is running!"
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    break;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            println "adding client to executor"
            this.threadPool.execute(
                new AbstractWorker(clientSocket, receivedMessages))
            //"Thread Pooled Server"));
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
            threadPool.shutdown()
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port ${serverPort}", e);
        }
    }
}