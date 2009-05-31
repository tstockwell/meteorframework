package com.googlecode.meteorframework;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.googlecode.meteorframework.annotation.IsMethod;
import com.googlecode.meteorframework.annotation.Model;



/**
 * A version of the java.util.concurrent.Future that is more appropriate for 
 * Meteor (methods don't throw checked exceptions as in other Meteor APIs).
 * 
 * @author Ted Stockwell
 */
@Model public interface Receipt<V> extends Future<V> {

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     */
	@IsMethod
    V getResult();

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     */
	@IsMethod
    V getResult(long timeout, TimeUnit unit);
	
    /**
     * Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, has already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when <tt>cancel</tt> is called,
     * this task should never run.  If the task has already started,
     * then the <tt>mayInterruptIfRunning</tt> parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.
     *
     * <p>After this method returns, subsequent calls to {@link #isDone} will
     * always return <tt>true</tt>.  Subsequent calls to {@link #isCancelled}
     * will always return <tt>true</tt> if this method returned <tt>true</tt>.
     *
     * @param mayInterruptIfRunning <tt>true</tt> if the thread executing this
     * task should be interrupted; otherwise, in-progress tasks are allowed
     * to complete
     * @return <tt>false</tt> if the task could not be cancelled,
     * typically because it has already completed normally;
     * <tt>true</tt> otherwise
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed
     * normally.
     *
     * @return <tt>true</tt> if this task was cancelled before it completed
     */
    @IsMethod
    boolean isCancelled();

    /**
     * Returns <tt>true</tt> if this task completed.
     *
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * <tt>true</tt>.
     *
     * @return <tt>true</tt> if this task completed
     */
    @IsMethod
    boolean isDone();

}
