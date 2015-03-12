import test.gpars.StandardMsg

import static groovyx.gpars.actor.Actors.actor
/**
 * A demo showing two cooperating actors. The decryptor decrypts received messages
 * and replies them back.  The console actor sends a message to decrypt, prints out
 * the reply and terminates both actors.  The main thread waits on both actors to
 * finish using the join() method to prevent premature exit, since both actors use
 * the default actor group, which uses a daemon thread pool.
 * @author Dierk Koenig, Vaclav Pech
 */
def appender = actor {
    loop {
        react { toAppend ->
            println 'append:' + toAppend
            reply toAppend.toString() + ' this is appended!'
        }
    }
}

def decryptor = actor {
    loop {
        react { message
            ->
            println 'decrypt:' + message
            if (message instanceof String) {
                appender.send message.reverse()
                // nextThing ->
                    println 'got appended: ' + it
                    reply it //message.reverse()
                //}

            }
            else stop()
        }
    }
}



def console = actor {

    def msgToProcess = new StandardMsg()

    decryptor.send 'lellarap si yvoorG'
    react {
        println 'Decrypted message: ' + it
        decryptor.send false
    }
}

[decryptor, console]*.join()