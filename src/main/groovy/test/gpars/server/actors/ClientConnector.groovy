package test.gpars.server.actors

import groovy.util.logging.Slf4j
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DynamicDispatchActor
import test.gpars.server.SystemServices
import test.gpars.server.messages.AddMessage
import test.gpars.server.messages.SubtractMessage

@Slf4j
class ClientConnector extends DynamicDispatchActor {

    ClientConnectionReader readerActor
    ClientConnectionWriter writerActor
    GlobalActor global
    private socket
    def receivedMessages = []

    def systemServices = SystemServices.instance
    def name
    def theValue = 0.0


    ClientConnector(Socket theClientConnection, Actor global) {
        socket = theClientConnection
        def reader = theClientConnection.getInputStream().newReader()
        def output = theClientConnection.getOutputStream().newWriter()

       // def connector = new ClientConnector(reader, output)
        // log.debug "connector is =>${connector}"
        name = socket.getRemoteSocketAddress().toString()
        //systemServices.addSessionActor(name, this)
        this.global = global
        //global = systemServices.getGlobalActor()
        readerActor = new ClientConnectionReader(reader, this)
        writerActor = new ClientConnectionWriter(output)
        writerActor.start()
        systemServices.addTaskToThreadPool(readerActor, "adding reader for session ${UUID.randomUUID().toString()}" )
        //println('informing client that server is ready')
        writerActor.send('server started')
       // log.info 'informing client that server is ready'
    }

    /**
     * This is used primarily by the GlobalActor as a reply destination
     * @param message
     */
    void onMessage(Integer message) {
        //log.info  'session got message ' + message
        writerActor.send(message)
    }

    void onMessage(AddMessage message) {
      // log.info 'doing Add'
        theValue += message.getValue()
        sendMessages(message)
    }

    void onMessage(SubtractMessage message) {
        println 'doing Subtract'
        theValue -= message.getValue()
        sendMessages(message)
    }

    private sendMessages(def msg) {
       // println(" session actor sending ${msg.class}")
        global.send(msg, {
            log.info('session sent global msg, not back to client!' + it)
            writerActor.send("Session value => ${theValue} global=> ${it}".toString() )
        })

    }

    def shutdown() {
        writerActor.stop()
        readerActor.keepReading = false
    }
}
