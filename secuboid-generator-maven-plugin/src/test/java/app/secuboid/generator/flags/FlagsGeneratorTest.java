/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
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

package app.secuboid.generator.flags;

import app.secuboid.generator.common.BufferedWriterArray;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlagsGeneratorTest {

    private static final String YAML_STR = """
            action-test:
              type: action
              need-source: false
              description:
                en: Action test
                fr: Action test
            source-action-test:
              type: source-action
              description:
                en: Source action test
                fr: Source action test
            source-action-target-test:
              type: source-action-target
              need-target: true
              description:
                en: Source action target test
                fr: Source action target test
            metadata-test:
              type: metadata
              hidden: true
              need-metadata: true
              value-class: String.class
              from-string: v -> v
              description:
                en: Metadata test
                fr: Metadata test
            """;

    private static final String JAVA_TEMPLATE_STR = "{{generatedFlags}}";

    private final Map<String, String> languageToTarget = singletonMap("en", null);

    private final FlagsGenerator flagsGenerator = FlagsGenerator.newFlagsGenerator(null, null, null, null, languageToTarget);

    @Test
    void when_generate_code_then_return_target_with_all_types() throws MojoExecutionException, IOException {
        StringWriter swJavaTarget = new StringWriter();
        StringWriter swLangTarget = new StringWriter();
        StringWriter[] swJavaTargets = new StringWriter[]{swJavaTarget, swLangTarget};

        try (
                InputStream isSource = new ByteArrayInputStream(YAML_STR.getBytes());

                StringReader srJavaTemplate = new StringReader(JAVA_TEMPLATE_STR);
                BufferedReader brJavaTemplate = new BufferedReader(srJavaTemplate);

                BufferedWriterArray bufferedWriterArray = new BufferedWriterArray(swJavaTargets)
        ) {
            flagsGenerator.generate(isSource, brJavaTemplate, bufferedWriterArray);
        }

        String output = swJavaTarget.toString();

        assertTrue(output.contains(
                "public static final FlagType FLAG_ACTION_TEST = FlagType.newInstance(\"action-test\", \"Action test\", false, false, false, false);"));
        assertTrue(output.contains(
                "public static final FlagType FLAG_SOURCE_ACTION_TEST = FlagType.newInstance(\"source-action-test\", \"Source action test\", true, false, false, false);"));
        assertTrue(output.contains(
                "public static final FlagType FLAG_SOURCE_ACTION_TARGET_TEST = FlagType.newInstance(\"source-action-target-test\", \"Source action target test\", true, true, false, false);"));
        assertTrue(output.contains(
                "public static final FlagType FLAG_METADATA_TEST = FlagType.newInstance(\"metadata-test\", \"Metadata test\", true, false, true, true);"));

        String langOutput = swLangTarget.toString();

        assertTrue(langOutput.contains("action-test: Action test"));
    }
}
