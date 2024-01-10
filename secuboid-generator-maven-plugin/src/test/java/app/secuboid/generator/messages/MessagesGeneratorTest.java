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
package app.secuboid.generator.messages;

import app.secuboid.generator.common.BufferedWriterArray;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesGeneratorTest {

    private static final String YAML_STR = """
            simple: blah
            double-var: blah
            main:
              sub1: blah {{tag-1}} blah {{tag-2}} blah
              sub2: blah
            one:
              two:
                three: blah
            """;

    private static final String JAVA_TEMPLATE_STR = """
            package package.name;

            public class MessagePaths {

            {{generatedConsts}}
            }
            """;

    private final MessagesGenerator messagesGenerator = new MessagesGenerator(null, null, null, null);

    @Test
    void when_generate_code_then_return_constants() throws MojoExecutionException, IOException {
        StringWriter swJavaTarget = new StringWriter();
        StringWriter[] swJavaTargets = new StringWriter[]{swJavaTarget};

        try (
                InputStream isSource = new ByteArrayInputStream(YAML_STR.getBytes());

                StringReader srJavaTemplate = new StringReader(JAVA_TEMPLATE_STR);
                BufferedReader brJavaTemplate = new BufferedReader(srJavaTemplate);

                BufferedWriterArray bufferedWriterArray = new BufferedWriterArray(swJavaTargets)
        ) {
            messagesGenerator.generate(isSource, brJavaTemplate, bufferedWriterArray);
        }

        String output = swJavaTarget.toString();

        assertTrue(output.contains("public static MessagePath simple() {"));
        assertTrue(output
                .contains("    return MessagePath.newInstance(\"simple\", new String[] {}, new Object[] {});"));
        assertTrue(output.contains("public static MessagePath doubleVar() {"));
        assertTrue(output.contains(
                "    return MessagePath.newInstance(\"double-var\", new String[] {}, new Object[] {});"));
        assertTrue(output.contains("public static MessagePath mainSub1(Object tag1, Object tag2) {"));
        assertTrue(output.contains(
                "    return MessagePath.newInstance(\"main.sub1\", new String[] { \"{{tag-1}}\", \"{{tag-2}}\" }, new Object[] { tag1, tag2 });"));
        assertTrue(output.contains("public static MessagePath mainSub2() {"));
        assertTrue(output.contains(
                "    return MessagePath.newInstance(\"main.sub2\", new String[] {}, new Object[] {});"));
        assertTrue(output.contains("public static MessagePath oneTwoThree() {"));
        assertTrue(output.contains(
                "    return MessagePath.newInstance(\"one.two.three\", new String[] {}, new Object[] {});"));
    }
}
