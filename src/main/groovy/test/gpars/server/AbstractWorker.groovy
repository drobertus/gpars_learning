package test.gpars.server

import groovy.util.logging.Slf4j
import test.gpars.server.actors.SessionActor


@Slf4j
class AbstractWorker implements Runnable {

    Socket myClientSocket;
    boolean keepReading = true;
    boolean ServerOn = true

    Writer output
    def reader
    final Class<AbstractSocketHandler> handlerClass


    def services = SystemServices.instance

    AbstractWorker(Socket s, Class handlerClass) {
        myClientSocket = s;
        this.handlerClass = handlerClass
    }

    public void run() {
        // Obtain the input stream and the output stream for the socket
        // A good practice is to encapsulate them with a BufferedReader
        // and a PrintWriter as shown below.
        // Print out details of this connection
        log.info ("Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());

        reader = myClientSocket.getInputStream().newReader()
        output = myClientSocket.getOutputStream().newWriter()

        def connector = new SessionActor(reader, output)// handlerClass.newInstance([reader, output])
        log.debug "connector is =>${connector}"
        services.addConnectedClient(connector.getName(), connector)
        log.debug "abstract worker complete"
    }


}

