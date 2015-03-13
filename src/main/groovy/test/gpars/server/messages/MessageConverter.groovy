package test.gpars.server.messages

/**
 * Created by David on 3/12/2015.
 */
class MessageConverter {

    static def convertStringToMessage(final String input) {
        def pieces = input.split(' ')
        if(pieces.length != 2)
            throw new Exception ("BLEAH!!!")

        def response
        switch (pieces[0]) {
            case 'add':
                response = new AddMessage(value: pieces[1].toInteger())
                break
            case 'subtract':
                response = new SubtractMessage(value: pieces[1].toInteger())
                break;
            default:
                response = "we don't recognize ${pieces[0]}"
        }
        return  response
    }
}
