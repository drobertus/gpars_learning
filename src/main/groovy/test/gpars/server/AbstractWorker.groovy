package test.gpars.server

import groovy.util.logging.Log


@Log
class AbstractWorker implements Runnable {

    Socket myClientSocket;
    boolean keepReading = true;
    boolean ServerOn = true

    Writer output
    def reader
    def messagesReceived

    AbstractWorker(Socket s, def messages) {
        myClientSocket = s;
        messagesReceived = messages
    }

    public void writeToClient(String msg) {
        output << msg +"\n"
        output.flush()
        println('server wrote to client '+ msg)
    }

    public void run() {
        // Obtain the input stream and the output stream for the socket
        // A good practice is to encapsulate them with a BufferedReader
        // and a PrintWriter as shown below.
        // Print out details of this connection
        println("Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());

        try {
            reader = myClientSocket.getInputStream().newReader()
            output = myClientSocket.getOutputStream().newWriter()


            writeToClient('welcome, client')
            // At this point, we can read for input and reply with appropriate output.

            // Run in a loop until m_bRunThread is set to false
            while(keepReading) {
               // println(" server listening to a client")
                // read incoming stream
                def clientCommand = reader.readLine()
                if(clientCommand) {
                    println("Client Says :" + clientCommand);
                    messagesReceived.add clientCommand

                    if (clientCommand.equalsIgnoreCase("quit")) {
                        // Special command. Quit this thread
                        keepReading = false;
                        print("Stopping client thread for client : ");
                    } else if (clientCommand.equalsIgnoreCase("end")) {
                        // Special command. Quit this thread and Stop the Server
                        keepReading = false;
                        print("Stopping client thread for client : ");
                        ServerOn = false;
                    } else {
                        // Process it
                        // out.println("Server Says : " + clientCommand);
                        // out.flush();
                    }
                    if (!ServerOn) {
                        // Special command. Quit this thread
                        print("Server has already stopped");
                        // out.println("Server has already stopped");
                        // out.flush();
                        keepReading = false;

                    }

                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // Clean up
            try
            {
                keepReading = false
                reader.close();
                output.close();
                myClientSocket.close();
                println("...Stopped");
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }


}

