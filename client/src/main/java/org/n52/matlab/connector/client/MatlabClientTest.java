/*
 * Copyright (C) 2012-2015 by it's authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.n52.matlab.connector.client;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import org.n52.matlab.connector.MatlabException;
import org.n52.matlab.connector.MatlabRequest;
import org.n52.matlab.connector.MatlabResult;

import org.n52.matlab.connector.value.MatlabScalar;
import org.n52.matlab.connector.value.MatlabType;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MatlabClientTest {
    private static final int THEADS = 10;
    private static final int REQUESTS_PER_THREAD = 40;
    private static final int REQUESTS = THEADS * REQUESTS_PER_THREAD;
    private final CountDownLatch latch = new CountDownLatch(THEADS);
    private final double[] results = new double[REQUESTS];
    private MatlabClient client;

    public void run() throws Exception {
        client = MatlabClient.create(URI.create("ws://localhost:7000"));
//        client = MatlabClient.create(new File("/home/auti/Source/matlab-wps/src/main/resources"));
        startThreads();
        latch.await();
        checkResults();
        client.close();
    }

    private void checkResults() {
        for (int i = 0; i < results.length; ++i) {
            double expected = expectedResult(i);
            if (results[i] != expected) {
                System.out.printf("Expected %f instead of %f at index %d\n",
                                  expected, results[i], i);
                System.exit(1);
            }
        }
    }

    private int expectedResult(int idx) {
        return idx + idx;
    }

    private MatlabRequest buildRequest(int idx) {
        return new MatlabRequest("add")
                .addParameter(new MatlabScalar(idx))
                .addParameter(new MatlabScalar(idx))
                .addResult("result", MatlabType.SCALAR);
    }

    private void startThreads() {
        for (int threads = 0; threads < THEADS; ++threads) {

            int offset = threads * REQUESTS_PER_THREAD;
            System.out.printf("Starting Thraed for %d-%d\n",
                              threads, offset+REQUESTS_PER_THREAD-1);
            RunnableImpl runnable = new RunnableImpl(offset);
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    public static void main(String[] args) throws Exception {
        new MatlabClientTest().run();
    }

    private class RunnableImpl implements Runnable {
        private final int offset;

        private RunnableImpl(int offset) {
            this.offset = offset;
        }

        @Override
        public void run() {
            try {
                for (int j = offset; j < offset + REQUESTS_PER_THREAD; ++j) {
                    MatlabRequest request = buildRequest(j);
                    MatlabResult response = client.execSync(request);
                    System.out.printf("%s -> %s\n", request, response);
                    results[j] = response.getResult("result").asScalar().value();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (MatlabException ex) {
                throw new RuntimeException(ex);
            } finally {
                latch.countDown();
            }
        }
    }
}
