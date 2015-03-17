package test.gpars


class CountdownTimer {
    final def countdown
    def endTime
    CountdownTimer(long time){
        countdown = time
    }

    void start(){
        endTime = System.currentTimeMillis() + countdown
    }

    boolean hasExpired() {
        if(!endTime) {
            start()
        }
        return System.currentTimeMillis() > endTime
    }
}
