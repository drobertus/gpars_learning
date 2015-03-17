package test.gpars.server.actors

import groovy.util.logging.Slf4j
import groovyx.gpars.actor.DefaultActor
import groovyx.gpars.actor.DynamicDispatchActor
import test.gpars.server.messages.AddMessage
import test.gpars.server.messages.SubtractMessage

@Slf4j
class GlobalActor extends DefaultActor {

    def globalValue = 0.0

    @Override
    protected void act() {
        loop {
            react{ msg ->
                log.info "acting on message ${msg}"

                switch (msg.class) {
                    case SubtractMessage:
                        break
                    case AddMessage:
                        globalValue += msg.getValue()
                        def theSender = getSender()
                        //log.info ("theSender = ${theSender}" )
                        if (theSender) {
                            reply(globalValue.intValue())
                            log.info("the sender sent!")
                        }else {
                            println ("can not reply!")
                        }
                        break

                    default:
                        log.info "got something we don't recognize"
                }

            }
        }
    }

}
