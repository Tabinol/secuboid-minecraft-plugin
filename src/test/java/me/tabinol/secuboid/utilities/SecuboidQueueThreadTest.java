/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.secuboid.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import me.tabinol.secuboid.Secuboid;

/**
 * SecuboidQueueThreadTest
 */
public final class SecuboidQueueThreadTest {

    private static class TestQueueThread extends SecuboidQueueThread<Long> {

        TestQueueThread(final Secuboid secuboid) {
            super(secuboid, "Test queue thread");
        }

        @Override
        protected void doElement(final Long sleepTimeMillis) throws InterruptedException {
            if (sleepTimeMillis != 0l) {
                sleep(sleepTimeMillis);
            }
        }
    }

    private TestQueueThread testQueueThread;

    @Before
    public void init() {
        final Secuboid secuboid = mock(Secuboid.class);
        final Logger logger = mock(Logger.class);
        when(secuboid.getLogger()).thenReturn(logger);
        testQueueThread = spy(new TestQueueThread(secuboid));
    }

    @Test
    public void testStop() {
        testQueueThread.start();
        assertTrue(testQueueThread.isAlive());
        testQueueThread.stopNextRun();
        assertFalse(testQueueThread.isAlive());
    }

    @Test
    public void testWaitingStop() {
        testQueueThread.start();
        testQueueThread.addElement(Duration.ofSeconds(2).toMillis());
        assertTrue(testQueueThread.isAlive());
        testQueueThread.stopNextRun();
        assertFalse(testQueueThread.isAlive());
    }

    @Test
    public void testInterruptStop() {
        testQueueThread.start();
        testQueueThread.addElement(Duration.ofMinutes(1).toMillis());
        assertTrue(testQueueThread.isAlive());
        testQueueThread.stopNextRun();
        assertFalse(testQueueThread.isAlive());
    }

    @Test
    public void testDeathStop() {
        testQueueThread.stopNextRun();
        assertFalse(testQueueThread.isAlive());
    }

    @Test
    public void addElementQueueActive() throws InterruptedException {
        final long element1 = 0l;
        testQueueThread.start();
        testQueueThread.addElement(element1);
        testQueueThread.stopNextRun();
        verify(testQueueThread, times(1)).doElement(element1);
    }

    @Test
    public void addElementQueueInactive() throws InterruptedException {
        final long element1 = 0l;
        testQueueThread.start();
        testQueueThread.isQueueActive = false;
        testQueueThread.addElement(element1);
        testQueueThread.stopNextRun();
        verify(testQueueThread, times(0)).doElement(element1);
    }
}