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

package app.secuboid.core.utilities;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NameUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "land",
            "-minus",
            "minus-"
    })
    void when_invalid_then_return_false(String arg) {
        assertFalse(NameUtil.validateName(arg));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "élève",
            "abatoire",
            "endroit-01"
    })
    void when_valid_then_return_true(String arg) {
        assertTrue(NameUtil.validateName(arg));
    }
}