/* MIT License

* Copyright (c) 2018 John Kiragu

* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:

* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*
 */
package io.github.kigsmtua.milau.client;

import java.util.HashMap;
import redis.clients.jedis.Jedis;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kigsmtua.milau.Config;
import io.github.kigsmtua.milau.task.Task;

/**
 * Common logic for Client implementations.
 * @author john.kiragu
 */
public abstract class AbstractClient implements Client {

    protected AbstractClient(Config config){
        
    }

    /**
     * Actually enqueue the serialized job.
     *
     * @param queue
     *            the queue to add the Job to
     * @param msg
     *            the serialized Job
     * @param future
     *            when the job will be executed
     * @throws Exception
     *             in case something goes wrong
     */
    protected abstract void doEnqueue(String queue, String msg,
            long future) throws Exception;

    @Override
    public void enqueue(String queue, Task task, long future) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            doEnqueue(queue, mapper.writeValueAsString(task), future);
        } catch (Exception e) {
             //dosomething dosomething
        }
    }

     /**
     * Helper method that encapsulates the minimum logic for adding a job to a
     * queue.
     * 
     * @param jedis
     *            the connection to Redis
     * @param queue
     *            the queue name
     * @param future
     *            timestamp when the time should be execute
     * @param jobJson
     *            serialized class to be picked from the queue
     */
    public static void doEnqueue(final Jedis jedis, final String queue, 
            final long future, final String jobJson) {
        HashMap<String, Double> scores = new HashMap<>();
        scores.put(jobJson, Double.valueOf(future));
        jedis.zadd(queue, scores);
    }

}
