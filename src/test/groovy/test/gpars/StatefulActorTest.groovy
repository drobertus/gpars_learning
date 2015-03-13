package test.gpars

import spock.lang.Specification

import java.util.concurrent.TimeUnit

import static org.junit.Assert.assertEquals

/**
 * Created by David on 9/21/2014.
 */
class StatefulActorTest extends Specification {

    void "test stateful actor" () {
        setup:
        def act1 = new StatefulActor()

        act1.start()
        when:
            def msg1 = new StandardMsg(UUID.randomUUID().toString())
            msg1.to = 'conference7'
            msg1.msgBody = '{"start"}'
        act1 << msg1 //'the << method'

        //act1.send('the send() method')


        //act1.sendAndContinue 'Message send and cont', {reply -> println "I received reply: $reply"}
        //act1.sendAndWait('bleah! send and wait', 10, TimeUnit.MILLISECONDS)
        sleep 200
        then:

        //println ' the msgs= ' + act1.receivedMsgs
        assertEquals 1, act1.receivedMsgs.size()



        act1.tearDown()

    }
}
