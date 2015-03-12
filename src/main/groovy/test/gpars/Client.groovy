package test.gpars

/**
 * Created by David on 3/11/2015.
 */
class Client implements Runnable {

    Socket conn
    Writer output
    def input
    def listen = true

    Client(String host, int port) {
        conn = new Socket(host, port)
        output = conn.outputStream.newWriter()
        input = conn.getInputStream().newReader()
        //Thread.start {doRead()}
    }

    void writeToServer(String msg) {

        output.write(msg + "\n")// .write(msg.getBytes())

        output.flush()

        println "client wrote ${msg}"
    }

    void run() {
        println "client now listening"
        while(listen) {
            try {
                def msg = input.readLine()
                if (msg) {
                    println(" received -> ${msg}")
                }
            }catch(Exception e) {
                println "client dying!"
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
