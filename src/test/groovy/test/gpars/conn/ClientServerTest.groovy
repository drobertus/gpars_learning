package test.gpars.conn

import spock.lang.Specification
import test.gpars.Client
import test.gpars.server.ThreadedSocketServer

import static org.junit.Assert.assertEquals

/**
 * Created by David on 3/11/2015.
 */
class ClientServerTest extends Specification {

    def serverPort = 6545
    ThreadedSocketServer server
    Client client
    Thread srvThread
    Thread clntThread

    def setup() {
        server = new ThreadedSocketServer(serverPort)
        srvThread = Thread.start {server.run()}
        client = new Client('localhost', serverPort)
        clntThread = Thread.start {client.run()}
    }

    //@Override
    def cleanup() {
        println 'cleanup called'

        client.shutdown()
        clntThread.interrupt()

        server.stop()
        srvThread.interrupt()


    }

    def "test client and server messaging"() {

        when:
            client.writeToServer('hello')
            sleep 2500
        then:

            assertEquals 'hello', server.receivedMessages[0]


    }

}