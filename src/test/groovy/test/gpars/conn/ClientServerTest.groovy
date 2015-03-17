package test.gpars.conn

import groovy.util.logging.Log
import groovy.util.logging.Slf4j
import spock.lang.Specification
import test.gpars.Client
import test.gpars.server.SystemServices
import test.gpars.server.ThreadedSocketServer

import java.util.concurrent.atomic.AtomicInteger

import static org.junit.Assert.assertEquals

@Slf4j
class ClientServerTest extends Specification {

    def serverPort = 6545
    ThreadedSocketServer server
    List<Client> clients = []
    //Client client
    Thread srvThread
    List<Thread> clntThreads = []

    def "test client and server messaging"() {

        setup:
            def client = createClient()
            def toAdd = 50
        when:
            client.writeToServer('add ' + toAdd)
            final def expectedString = 'Session value => ' + toAdd +'.0 global=> ' + toAdd
            def timer = new CountdownTimer(5000)
            while(!expectedString.equals(client.status) && ! timer.hasExpired()) {
                sleep 50
                log.debug 'waiting'
            }
//            client.received.each{
//                println 'client msg = ' + it
//            }
        then:

            assertEquals expectedString, client.status

    }


    def "test multiple clients talking to the same server"() {

        setup:
            List<Client> clients = []
            def clientCount = 5
            def loopCount = 5
            def dataSets = []
            def expectedTotal = new AtomicInteger(0)
            def srvs = SystemServices.instance
            def global = srvs.globalActor

        clientCount.times { client ->
                clients << createClient()
                def aSet = []
                loopCount.times { loop ->
                    aSet << clientCount
                    expectedTotal.getAndAdd(loop)
                }
                dataSets << aSet
            }

            println "expected total => ${expectedTotal.get()}"

        when: "we get all the clients sending numbers"
            def sendStart = System.currentTimeMillis()
            loopCount.times { loop ->
                clientCount.times { client ->
                   // srvs.addTaskToThreadPool({
                        clients[client].writeToServer("add ${loop}".toString())
                    //}, "${client}-${loop}")
                }
            }
            println( "sendTime took -> ${System.currentTimeMillis() - sendStart}")

            def timer = new CountdownTimer(4000)
            def start = System.currentTimeMillis()
            while(!timer.hasExpired() && global.globalValue.intValue() < expectedTotal.get()) {
                sleep 50
            }
            log.info("Execution time after sends took ${System.currentTimeMillis() - start} ms")


        then: 'we get the expected result'
            assertEquals expectedTotal.get(), global.globalValue.toInteger()
            clients.each {
                println "finalStatus => ${it.status}"
            }
    }

    def setup() {
        server = new ThreadedSocketServer(serverPort)
        //srvThread =
        Thread.start {server.run()}


    }

    Client createClient() {
        def client = new Client('localhost', serverPort)
        def clntThread = Thread.start {client.run()}
        this.clntThreads <<clntThread
        clients << client

        while(!client.canWriteToServer) {
            sleep 500
            log.info "client can not write yet"
        }
        return client
    }

    def cleanup() {
        log.info 'cleanup called'
        clients.each {
            it.shutdown()
        }
        clntThreads.each {
            it.interrupt()
        }

        server.stop()
//        srvThread.interrupt()
    }


    class CountdownTimer {
        final def countdown
        def endTime
        CountdownTimer(long time){
            countdown = time
        }

        void start(){
            endTime = System.currentTimeMillis() + countdown
        }

        boolean hasExpired() {
            if(!endTime) {
                start()
            }
            return System.currentTimeMillis() > endTime
        }

    }

}