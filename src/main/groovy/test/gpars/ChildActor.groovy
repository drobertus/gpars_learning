package test.gpars

import groovy.util.logging.Log
import groovyx.gpars.actor.DefaultActor

/**
 * Created by David on 2/24/2015.
 */
@Log
class ChildActor extends DefaultActor {

    def key
    ChildActor(String key) {
        log.info "new child with ${key}"
        this.key = key
    }

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
