package test.gpars.server.actors

import groovyx.gpars.actor.DefaultActor
import test.gpars.StandardMsg

/**
 * Created by David on 3/11/2015.
 */
class SessionActor extends DefaultActor {

    ReaderActor readerActor
    WriterActor writerActor

    SessionActor(InputStream input, OutputStream output) {
        readerActor = new ReaderActor(input)
        writerActor = new WriterActor(output)
    }

    /**
     * This sets the state of the session
     */
    @Override
    protected void act() {
        loop {
            react {  theMsg ->
                if (theMsg instanceof StandardMsg) {
                    log.info "The msg to child [${key}] is ${theMsg.msgBody} "
                    def theReply = new StandardMsg(UUID.randomUUID().toString())
                    theReply.from = key
                    theReply.msgType << 'ACK'

                    reply theReply //"child reply to ${theMsg.id} "
                }
            }
        }
    }
}
