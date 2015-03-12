package test.gpars

import groovy.util.logging.Log
import groovyx.gpars.actor.DefaultActor

/**
 * Created by David on 9/21/2014.
 */
@Log
class StatefulActor extends DefaultActor {

    def receivedMsgs = []
    Map<String, ChildActor> kids = [:]

    @Override
    protected void act() {
        loop {
            react {  theMsg ->
                receivedMsgs.add(theMsg)
                if (theMsg instanceof StandardMsg) {
                    log.info ("**** Got message ${theMsg.id}")
                    if(!theMsg.to) {
                        log.warning("Received a message with no addressee")
                    }
                    def child = kids.get(theMsg.to)
                    if (!child) {
                        child = new ChildActor(theMsg.to)
                        kids.put(theMsg.to, child)
                        child.start()
                    }
                    child.sendAndContinue(theMsg, {reply -> log.info "got a reply ${reply}"})
                }


                if (theMsg.from && theMsg.from.startsWith('child reply')) {
                    log.info ('**** got a reply from my child')
                }
            }
        }

    }

    void tearDown() {
        this.stop()

        this.kids.values().each {
            it.stop()
        }
    }

}
