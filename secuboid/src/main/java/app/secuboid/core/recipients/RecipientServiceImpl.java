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

import app.secuboid.api.exceptions.RecipientException;
import app.secuboid.api.recipients.RecipientExec;
import app.secuboid.api.recipients.RecipientResult;
import app.secuboid.api.recipients.RecipientService;
import app.secuboid.api.recipients.RecipientType;
import app.secuboid.api.registration.RecipientRegistered;
import app.secuboid.api.registration.RegistrationService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RecipientServiceImpl implements RecipientService {

    private final RegistrationService registrationService;

    private final Map<String, RecipientType> nameLowerToType;

    private final Map<RecipientType, RecipientRegistered> typeToAnnotation;
    private final Map<RecipientType, Map<String, RecipientExec>> typeToValueToRecipient;

    public RecipientServiceImpl(RegistrationService registrationService) {
        this.registrationService = registrationService;

        nameLowerToType = new HashMap<>();
        typeToAnnotation = new HashMap<>();
        typeToValueToRecipient = new HashMap<>();
    }

    @Override
    public void onEnable(boolean isServerBoot) {
        if (isServerBoot) {
            loadRecipientTypes();
        }

        loadRecipients();
    }

    public void grab(String name, String value, Consumer<RecipientResult> callback) {
//        String nameLower = name.toLowerCase();
//
//        // Pre-validation
//        RecipientResult result = grabInstanceWithResult(NON_EXISTING_ID, nameLower, value);
//        if (result.code() != SUCCESS || result.recipientExec() == null) {
//            if (callback != null) {
//                callback.accept(result);
//            } else {
//                log().log(WARNING, () -> format("Non success parameter Value [name=%s, value=%s, " +
//                        "result=%s]", name, value, result));
//            }
//            return;
//        }
//
//        RecipientExec recipientExec = result.recipientExec();
//        long id = recipientExec.id();
//
//        if (id != NON_EXISTING_ID) {
//            if (callback != null) {
//                callback.accept(result);
//            }
//            return;
//        }
//
//        RecipientType type = recipientExec.type();
//        RecipientRegistered info = type.info();
//        String shortName = info.shortName();
//        String valueTrans = recipientExec.getValue();
//        RecipientRow recipientRow = new RecipientRow(NON_EXISTING_ID, shortName, valueTrans);
//
//        getStorageManager().insert(recipientRow, r -> insertCallback(r, callback));
    }

    private void loadRecipientTypes() {
//        if (!nameLowerToType.isEmpty() || !typeToAnnotation.isEmpty()) {
//            return;
//        }
//
//        Map<RecipientExec, RecipientRegistered> recipientExecToRecipientRegistered = ((RegistrationServiceImpl) registrationService).getRecipientExecToRecipientRegistered();
//
//        recipientExecToRecipientRegistered.forEach((c, a) -> {
//            RecipientType type = new RecipientType(c, a);
//            typeToAnnotation.put(type, a);
//            nameLowerToType.put(a.shortName(), type);
//            nameLowerToType.put(a.name(), type);
//        });
    }

    private void loadRecipients() {
        typeToValueToRecipient.clear();

//        // TODO Set<RecipientRow> recipientRows = getStorageManager().selectAllSync(RecipientRow.class);
//        Set<RecipientRow> recipientRows = Collections.emptySet();
//
//        for (RecipientRow recipientRow : recipientRows) {
//            loadRecipientRow(recipientRow);
//        }
    }

//    private void insertCallback(RecipientRow recipientRow, Consumer<RecipientResult> callback) {
//        RecipientResult result = grabInstanceWithResult(recipientRow.id(),
//                recipientRow.shortName(), recipientRow.value());
//
//        if (callback != null) {
//            callback.accept(result);
//        }
//    }
//
//    private void loadRecipientRow(RecipientRow recipientRow) {
//        RecipientResult result = grabInstanceWithResult(recipientRow.id(),
//                recipientRow.shortName(), recipientRow.value());
//        RecipientResultCode code = result.code();
//        RecipientExec recipientExec = result.recipientExec();
//
//        if (recipientExec == null || code != SUCCESS) {
//            String msg = format("Unable to load the recipientExec [result=%s]", result);
//            log().log(SEVERE, () -> msg);
//        }
//    }

//    private RecipientResult grabInstanceWithResult(long id, String nameLower,
//                                                   String value) {
//        RecipientType type = nameLowerToType.get(nameLower);
//
//        if (type == null) {
//            return new RecipientResult(INVALID_PARAMETER, null);
//        }
//
//        RecipientRegistered annotation = typeToAnnotation.get(type);
//
//        boolean needsValue = annotation.needsValue();
//        if ((needsValue && value == null) || (!needsValue && value != null)) {
//            return new RecipientResult(INVALID_VALUE, null);
//        }
//
//        String modifiedValue;
//        if (!needsValue) {
//            modifiedValue = null;
//        } else {
//            modifiedValue = switch (annotation.characterCase()) {
//                case LOWERCASE -> value.toLowerCase();
//                case UPPERCASE -> value.toUpperCase();
//                case CASE_SENSITIVE -> value;
//            };
//        }
//
//        RecipientExec recipientExec;
//
//        if (id == NON_EXISTING_ID) {
//            recipientExec = typeToValueToRecipientGet(type, modifiedValue);
//            if (recipientExec != null) {
//                return new RecipientResult(SUCCESS, recipientExec);
//            }
//        }
//
//        try {
//            recipientExec = createInstance(type, id, modifiedValue);
//        } catch (RecipientException e) {
//            return new RecipientResult(INVALID_VALUE, null);
//        }
//
//        if (id != NON_EXISTING_ID) {
//            typeToValueToRecipientAdd(recipientExec);
//        }
//
//        return new RecipientResult(SUCCESS, recipientExec);
//    }

    @SuppressWarnings("java:S2139")
    private RecipientExec createInstance(RecipientType type, long id,
                                         String value) throws RecipientException {
//        Class<? extends RecipientExec> clazz = type.clazz();
//        try {
//            Method newInstance = clazz.getMethod("newInstance", RecipientType.class, long.class, String.class);
//            return (RecipientExec) newInstance.invoke(null, type, id, value);
//        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException |
//                 InvocationTargetException e) {
//            if (e instanceof InvocationTargetException invocationTargetException) {
//                Throwable cause = invocationTargetException.getCause();
//                if (cause instanceof RecipientException recipientException) {
//                    throw recipientException;
//                }
//            }
//            String msg = format("Unable to create the recipientExec instance: [clazz=%s, id=%s, value=%s]", clazz,
//                    id, value);
//            log().log(SEVERE, e, () -> msg);
//            throw new RecipientException(msg, e);
//        }

        // TODO not null
        return null;
    }

    private void typeToValueToRecipientAdd(RecipientExec recipientExec) {
        //typeToValueToRecipient.computeIfAbsent(recipientExec.type(), k -> new HashMap<>()).put(recipientExec
        // .getValue(), recipientExec);
    }

    private RecipientExec typeToValueToRecipientGet(RecipientType type,
                                                    String modifiedValue) {
        Map<String, RecipientExec> valueToRecipient = typeToValueToRecipient.get(type);
        if (valueToRecipient != null) {
            return valueToRecipient.get(modifiedValue);
        }

        return null;
    }
}
