package xyz.deftu.lib.utils

import java.util.concurrent.*

class Multithreader(
    poolSize: Int
) {

    companion object {

        /**
         * @return The default `Multithreader` instance, using a core pool size of 20.
         */
        val defaultInstance: Multithreader = Multithreader(25)

    }

    /**
     * @return The thread executor created for this `Multithreader`.
     */
    val executor: ThreadPoolExecutor =
        ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())

    /**
     * @return The executor service created for this `Multithreader`.
     */
    val scheduledExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(poolSize)

    /**
     * @param runnable The runnable to run asynchronously.
     */
    fun runAsync(runnable: Runnable) {
        executor.execute(runnable)
    }

    /**
     * @param runnable The runnable code to be ran.
     * @param delay    The delay before running.
     * @param timeUnit The time unit of the delay.
     */
    fun schedule(runnable: Runnable, delay: Long, timeUnit: TimeUnit): ScheduledFuture<*> {
        return scheduledExecutor.schedule(runnable, delay, timeUnit)
    }

    /**
     * @param runnable   The runnable code to be ran.
     * @param startDelay The initial delay before running the first time.
     * @param delay      The delay before running.
     * @param timeUnit   The time unit of the delay.
     */
    fun schedule(runnable: Runnable, startDelay: Long, delay: Long, timeUnit: TimeUnit): ScheduledFuture<*> {
        return scheduledExecutor.scheduleAtFixedRate(runnable, startDelay, delay, timeUnit)
    }

    /**
     * @param runnable The code to submit.
     */
    fun submit(runnable: Runnable): Future<*> {
        return executor.submit(runnable)
    }

}
