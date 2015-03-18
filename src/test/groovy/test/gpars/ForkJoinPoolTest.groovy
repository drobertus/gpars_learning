package test.gpars

import groovyx.gpars.GParsExecutorsPool
import groovyx.gpars.GParsPool

//import jsr166y.ForkJoinPool
import org.junit.Test

import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadPoolExecutor

import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.assertEquals
//import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by David on 9/21/2014.
 */
class ForkJoinPoolTest {


    @Test
    void testLargeArraySumming() {
        int theSize = 100000
        def intArray = new ArrayList<Integer>(theSize)

        Random rand = new Random()
        int max = 10
        //def randomIntegerList = []
        (0..theSize-1).each { theVal ->
            intArray << rand.nextInt(max+1)
        }

        def start = System.currentTimeMillis()
        //long theSum = 0
        final AtomicInteger theSum = new AtomicInteger(0)
        intArray.each {
            theSum.getAndAdd(it) // += it
        }
        def end = System.currentTimeMillis()
        println("time elapsed direct= ${end-start} ms" )


        final AtomicInteger result = new AtomicInteger(0)
        //long result = 0;
        start = System.currentTimeMillis()
        withPool( 20 ) {
            intArray.eachParallel{ result.getAndAdd(it)}
        }
        end = System.currentTimeMillis()
        println("time elapsed parallel= ${end-start} ms" )

        assertEquals theSum.longValue(), result.longValue()
//
//        ForkJoinPool.withPool {
//            final AtomicInteger result = new AtomicInteger(0)
//            start = System.currentTimeMillis()
//            intArray.eachParallel {result.addAndGet(it)}
//            end = System.currentTimeMillis()
//            `println("time elapsed parallel= ${end-start} ms" )
//        }



    }
}
