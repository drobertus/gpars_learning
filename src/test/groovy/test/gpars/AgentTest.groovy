package test.gpars

import groovyx.gpars.agent.Agent
import spock.lang.Specification

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import static org.junit.Assert.assertEquals

class AgentTest extends Specification {

    def "test the agent behaviour"() {
        setup:
        def agent = new Agent<Integer>(0)
        def clientCount = 5
        def msgCount = 5
        def executor = Executors.newScheduledThreadPool(20)
        
        when:
        clientCount.times { client ->
            msgCount.times { msg ->
                executor.execute( {
                    agent.send( {agent.updateValue(agent.instantVal + 1)})
                } )
            }
        }
        def timer = new CountdownTimer(1000)
        println ('current val 1 = ' + agent.instantVal)
        while(agent.instantVal < (clientCount * msgCount) && !timer.hasExpired()) {
            sleep 50
            println ('current val = ' + agent.instantVal)
        }
        then:
        assertEquals (((clientCount) * (msgCount)), agent.instantVal)
    }
}