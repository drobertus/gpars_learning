package test.gpars.server


abstract class AbstractSocketHandler implements Runnable {

    InputStream inputStream
    OutputStream outputStream
    String uniqueName

    AbstractSocketHandler(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream
        this.outputStream = outputStream
        uniqueName = UUID.randomUUID().toString()
    }

    abstract void writeBytes (byte[] data)

    abstract void writeString (String message)

    void shutdown() {
        outputStream.close()
        inputStream.close()
    }

    String getName() {
        uniqueName
    }
}
