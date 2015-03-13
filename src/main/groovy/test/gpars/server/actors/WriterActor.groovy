package test.gpars.server.actors

import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DefaultActor
import groovyx.gpars.actor.DynamicDispatchActor
import groovyx.gpars.actor.ReactiveActor
import test.gpars.server.messages.AddMessage

/**
 * This class is used to write back to the client and makes sure that
 * messages don't interfere with one another
 */
@Slf4j
class WriterActor extends DefaultActor  {

    Writer outputStream
    WriterActor(Writer os) {
        this.outputStream = os
        //this.send()
    }

    /**
     * This sets the state of the session
     */
    @Override
    protected void act() {
        loop {
            react { theMsg ->
                log.info 'writer actor got message ' + theMsg
                outputStream << theMsg + '\n'
                outputStream.flush()
            }
        }
    }


}
