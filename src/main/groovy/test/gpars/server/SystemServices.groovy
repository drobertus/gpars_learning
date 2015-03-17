package test.gpars.server

import groovy.util.logging.Slf4j
import test.gpars.server.actors.GlobalActor
import test.gpars.server.actors.ClientConnector

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Slf4j
@Singleton
class SystemServices {

    private ExecutorService threadPool = Executors.newScheduledThreadPool(50)

    private ConcurrentHashMap<String, ClientConnector> connectedClients = [:]

    GlobalActor globalActor

    void addTaskToThreadPool(Runnable task, String info) {
        log.info ("added task ${info} to thread pool -> ${task}" )
        if(threadPool) {
            threadPool.submit(task)
        }
        else {
            throw new Exception ("Threadpool is DEAD!!!")
        }
    }

    void addSessionActor(String id, ClientConnector client) {
        connectedClients.put(id, client)
    }

    void removeConnectedClient(String id) {
        def client = connectedClients.get(id)
        if (client) {
            client.stop()
            client.shutdown()
            connectedClients.remove(id)
        }
    }

    void shutDown() {
        log.info 'shutting down system services'
        connectedClients.keySet().each {
            removeConnectedClient(it)
        }
        if (globalActor) {
            globalActor.stop()
        }
    }

    void setGlobalActor(GlobalActor globalActor) {
        this.globalActor = globalActor
        globalActor.start()
    }

    GlobalActor getGlobalActor() {
        return globalActor
    }
}
