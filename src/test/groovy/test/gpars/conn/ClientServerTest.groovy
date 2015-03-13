package test.gpars.conn

import groovy.util.logging.Log
import groovy.util.logging.Slf4j
import spock.lang.Specification
import test.gpars.Client
import test.gpars.server.ThreadedSocketServer

import static org.junit.Assert.assertEquals

@Slf4j
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

        while(!client.canWriteToServer) {
            sleep 500
            log.info "client can not write yet"
        }
    }

    //@Override
    def cleanup() {
        log.info 'cleanup called'

        client.shutdown()
        clntThread.interrupt()

        server.stop()
        srvThread.interrupt()
    }

    def "test client and server messaging"() {

        when:
            client.writeToServer('add 50')
            sleep 500
        then:
            assertEquals 'add 50 new value 50.0', client.status
//            assertEquals 'hello', server.receivedMessages[0]


    }

}