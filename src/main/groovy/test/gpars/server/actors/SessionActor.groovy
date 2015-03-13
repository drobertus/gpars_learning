package test.gpars.server.actors

import groovy.util.logging.Log4j
import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DynamicDispatchActor
import test.gpars.server.SystemServices
import test.gpars.server.messages.AddMessage
import test.gpars.server.messages.SubtractMessage

@Slf4j
class SessionActor extends DynamicDispatchActor {

    ReaderActor readerActor
    WriterActor writerActor

    def receivedMessages =[]

    def systemServices = SystemServices.instance

    def theValue = 0.0

    SessionActor(Reader input, Writer output) {
        readerActor = new ReaderActor(input, this)
        writerActor = new WriterActor(output)
        writerActor.start()
        systemServices.addTaskToThreadPool(readerActor, "adding reader for session")
        //println('informing client that server is ready')
        writerActor.send('server started')
        log.info 'informing client that server is ready'
    }

    void onMessage(String message) {
        log.info  'Received string'
    }

    void onMessage(AddMessage message) {
        log.info 'doing Add'
        theValue += message.getValue()
        writerActor.send("add ${message.getValue()} new value ${theValue}")
    }

    void onMessage(SubtractMessage message) {
        println 'doing Subtract'
        theValue -= message.getValue()
        writerActor.send("subtracted ${message.getValue()} left with ${theValue}")
    }

    def getName() {
        return this.toString()
    }
    /**
     * This sets the state of the session
     */
//    @Override
//    protected void act() {
//        loop {
//            react {  theMsg ->
//                if (theMsg instanceof StandardMsg) {
//                    log.info "The msg to child [${key}] is ${theMsg.msgBody} "
//                    def theReply = new StandardMsg(UUID.randomUUID().toString())
//                    theReply.from = key
//                    theReply.msgType << 'ACK'
//
//                    reply theReply //"child reply to ${theMsg.id} "
//                }
//            }
//        }
//    }
}
