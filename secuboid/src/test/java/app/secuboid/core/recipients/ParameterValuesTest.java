/*
 *  Secuboid: LandService and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package app.secuboid.core.recipients;

class RecipientExecServiceTest {

//    private final AtomicLong atomicIndexId = new AtomicLong();
//
//    private RecipientService recipientService;
//
//    @BeforeEach
//    void beforeEach() {
//        RecipientServiceImpl recipientsImpl = spy(new RecipientServiceImpl());
//
//        StorageManager storageManager = mock(StorageManager.class);
//        doAnswer(this::storageInsertAnswer).when(storageManager).insert(any(), any());
//        doReturn(storageManager).when(recipientsImpl).getStorageManager();
//
//        PluginLoader pluginLoader = mock(PluginLoader.class);
//        RecipientRegistered recipientRegistered =
//                RecipientExecPlayer.class.getAnnotation(RecipientRegistered.class);
//        when(pluginLoader.getClassToAnnotation(RecipientRegistered.class, RecipientExec.class))
//                .thenReturn(Collections.singletonMap(RecipientExecPlayer.class, recipientRegistered));
//
//        recipientsImpl.init(pluginLoader);
//        recipientsImpl.load();
//
//        recipientService = recipientsImpl;
//    }
//
//    @Test
//    void when_grab_invalid_parameter_send_error_code() {
//        String value = UUID.randomUUID().toString();
//        AtomicReference<RecipientResult> atomicResult = new AtomicReference<>(null);
//        Consumer<RecipientResult> callback = atomicResult::set;
//
//        recipientService.grab("INVALID", value, callback);
//        await().atMost(Duration.ofSeconds(10)).until(() -> atomicResult.get() != null);
//
//        RecipientResult result = atomicResult.get();
//
//        assertNotNull(result);
//        assertEquals(INVALID_PARAMETER, result.code());
//    }
//
//    @Test
//    void when_grab_new_value_add_it() {
//        String value = UUID.randomUUID().toString();
//        AtomicReference<RecipientResult> atomicResult = new AtomicReference<>(null);
//        Consumer<RecipientResult> callback = atomicResult::set;
//
//        recipientService.grab(PLAYER, value, callback);
//        await().atMost(Duration.ofSeconds(10)).until(() -> atomicResult.get() != null);
//
//        RecipientResult result = atomicResult.get();
//
//        assertNotNull(result);
//        assertEquals(SUCCESS, result.code());
//        RecipientExec recipientExec = result.recipientExec();
//        assertNotNull(recipientExec);
//        assertEquals(1, recipientExec.id());
//    }
//
//    @Test
//    void when_grab_twice_same_value_add_one_and_get_the_second() {
//        String value = UUID.randomUUID().toString();
//        AtomicReference<RecipientResult> atomicResult = new AtomicReference<>(null);
//        Consumer<RecipientResult> callback = atomicResult::set;
//
//        recipientService.grab(PLAYER, value, callback);
//        await().atMost(Duration.ofSeconds(10)).until(() -> atomicResult.get() != null);
//
//        atomicResult.set(null);
//        recipientService.grab(PLAYER, value, callback);
//        await().atMost(Duration.ofSeconds(10)).until(() -> atomicResult.get() != null);
//
//        RecipientResult result = atomicResult.get();
//
//        assertNotNull(result);
//        assertEquals(SUCCESS, result.code());
//        RecipientExec recipientExec = result.recipientExec();
//        assertNotNull(recipientExec);
//        assertEquals(1, recipientExec.id());
//    }
//
//    private Object storageInsertAnswer(InvocationOnMock invocation) {
//        RecipientRow previousRow = invocation.getArgument(0);
//        Consumer<RecipientRow> callback = invocation.getArgument(1);
//
//        RecipientRow row = new RecipientRow(atomicIndexId.incrementAndGet(), PLAYER, previousRow.value());
//        callback.accept(row);
//
//        return null;
//    }
}
