package test.gpars.server.actors

import groovy.util.logging.Slf4j
import test.gpars.server.messages.MessageConverter

/**
 * This class reads off the wire
 * then fires appropriate messages back to the SessionActor
 */
@Slf4j
class ReaderActor implements Runnable {

    final Reader reader
    final SessionActor parent
    def keepReading = true

    ReaderActor(Reader inputStream, SessionActor parent) {
        reader = inputStream
        this.parent = parent
    }

    @Override
    void run() {
        while(keepReading) {

            def aLine = reader.readLine()
            if(aLine) {
                log.info 'reader got ' + aLine
                def toSend = MessageConverter.convertStringToMessage(aLine)
                log.debug "sending ${toSend.class.toString()}"
                parent.onMessage(toSend)
            }
        }
    }
}
