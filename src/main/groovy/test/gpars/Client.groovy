package test.gpars

import groovy.util.logging.Log
import groovy.util.logging.Log4j
import groovy.util.logging.Slf4j

@Slf4j
class Client implements Runnable {

    Socket conn
    Writer output
    def input
    def listen = true
    def canWriteToServer = false
    def status

    Client(String host, int port) {
        conn = new Socket(host, port)
        output = conn.outputStream.newWriter()
        input = conn.getInputStream().newReader()
        //Thread.start {doRead()}
    }

    void writeToServer(String msg) {
        if(canWriteToServer) {
            output.write(msg + "\n")// .write(msg.getBytes())
            output.flush()

            log.info "client wrote ${msg}"
        }
        else {
            log.info "not ready to send ${msg}"
        }
    }

    void run() {
        log.info "client now listening"
        while(listen) {
            try {
                def msg = input.readLine()
                if (msg) {
                    if('server started'.equals(msg)){
                        canWriteToServer = true
                    }
                    status = msg
                    log.info(" client received -> ${msg}")
                }
            }catch(Exception e) {
                log.info "client dying!"
            }
        }
    }

    void shutdown() {
        listen = false
        //input.close()
        output.close()
        conn.close()
    }


}
