package test.gpars.server

import groovy.util.logging.Slf4j
import test.gpars.server.actors.GlobalActor
import test.gpars.server.actors.ClientConnector


@Slf4j
class ThreadedSocketServer implements Runnable {

    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    def services = SystemServices.instance


    public ThreadedSocketServer(int port){
        this.serverPort = port;
        services.setGlobalActor(new GlobalActor())

    }

    public void run(){
        openServerSocket();
        while(! isStopped()){
            log.info "server is running!"
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                log.info "adding client to executor"
                def clientConn = new ClientConnector(clientSocket, services.globalActor)
                clientConn.start()
                services.addSessionActor(clientConn.name, clientConn)
                //services.addTaskToThreadPool(worker, "new client connection " + UUID.randomUUID().toString())
            } catch (IOException e) {
                if(isStopped()) {
                    log.info("Server Stopped.") ;
                    break;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
        }
        log.info("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
            services.shutDown()
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