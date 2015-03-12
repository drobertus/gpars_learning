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
            toAppend.msgBody = toAppend.msgBody + ' this is appended'
            reply toAppend //.toString() + ' this is appended!'
        }
    }
}

def decryptor = actor {
    loop {
        react { message ->
            println 'decrypt: ' + message
            if (message instanceof String) {
                appender.send message.reverse()
                // nextThing ->
                println 'got appended: ' + it
                reply it //message.reverse()
                //}

            } else if( message instanceof StandardMsg) {
                if (message.msgType.contains('reverse')) {

                    message.msgBody = message.msgBody.reverse()

                    if (message.msgType.contains('append')) {
                        appender.send message
                    }

                    reply message
                }
            }
            else stop()
        }
    }
}



def console = actor {

    def msgToProcess = new StandardMsg()
    msgToProcess.msgBody = 'lellarap si yvoorG'
    msgToProcess.msgType = ['reverse', 'append']

    //what we want to do it loop through the actions
    //and execute them in order
    msgToProcess.msgType.each { action ->
        //def call =

    }

    decryptor.send msgToProcess
    react {
        println 'Decrypted message: ' + it.msgBody
        decryptor.send false
    }
}

[decryptor, console]*.join()