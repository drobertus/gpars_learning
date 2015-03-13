package test.gpars.server

import groovy.util.logging.Log
import groovy.util.logging.Log4j
import groovy.util.logging.Slf4j

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Slf4j
@Singleton
class SystemServices {

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private Map<String, AbstractSocketHandler> connectedClients = [:]

    void addTaskToThreadPool(Runnable task, String info) {
        log.info ("added task ${info} to thread pool" )
        threadPool.submit(task)
    }

    void addConnectedClient(String id, AbstractSocketHandler client) {
        connectedClients.put(id, client)
    }

    void removeConnectedClient(String id) {
        def client = connectedClients.get(id)
        if (client) {
            client.shutdown()
            connectedClients.remove(id)
        }
    }

    void shutDown() {
        this.threadPool.shutdown();
        connectedClients.keySet().each {
            removeConnectedClient(it)
        }
    }
}
