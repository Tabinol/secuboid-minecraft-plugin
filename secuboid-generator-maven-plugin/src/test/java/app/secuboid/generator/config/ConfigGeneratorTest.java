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
package app.secuboid.generator.config;

import app.secuboid.generator.common.BufferedWriterArray;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigGeneratorTest {

    private static final String YAML_STR = """
            integer-config: 123
            double-config: 2.3
            boolean-config: true
            string-config: bonjour "tous"
            string-list: ["un", "deux", "trois"]
            main:
              sub: yes
            """;

    private static final String JAVA_TEMPLATE_STR = """
            package package.name;

            public class ConfigService {

            {{generatedVars}}

                public run() {
            {{generatedLoads}}
                }
            }
            """;

    private final ConfigGenerator configGenerator = new ConfigGenerator(null, null, null, null);

    @Test
    void when_generate_code_then_return_target_with_all_types() throws MojoExecutionException, IOException {
        StringWriter swJavaTarget = new StringWriter();
        StringWriter[] swJavaTargets = new StringWriter[]{swJavaTarget};

        try (
                InputStream isSource = new ByteArrayInputStream(YAML_STR.getBytes());

                StringReader srJavaTemplate = new StringReader(JAVA_TEMPLATE_STR);
                BufferedReader brJavaTemplate = new BufferedReader(srJavaTemplate);

                BufferedWriterArray bufferedWriterArray = new BufferedWriterArray(swJavaTargets)
        ) {
            configGenerator.generate(isSource, brJavaTemplate, bufferedWriterArray);
        }

        String output = swJavaTarget.toString();

        assertTrue(output.contains("private int integerConfig;"));
        assertTrue(output.contains("private double doubleConfig;"));
        assertTrue(output.contains("private boolean booleanConfig;"));
        assertTrue(output.contains("private String stringConfig;"));
        assertTrue(output.contains("private List<String> stringList;"));

        assertTrue(output.contains("doubleConfig = fileConfiguration.getDouble(\"double-config\");"));
        assertTrue(output.contains("fileConfiguration.addDefault(\"boolean-config\", true);"));
        assertTrue(output.contains("booleanConfig = fileConfiguration.getBoolean(\"boolean-config\");"));
        assertTrue(output.contains(
                "fileConfiguration.addDefault(\"string-config\", \"bonjour \\\"tous\\\"\");"));
        assertTrue(output.contains("stringConfig = fileConfiguration.getString(\"string-config\");"));
        assertTrue(output.contains(
                "fileConfiguration.addDefault(\"string-list\", Arrays.asList(\"un\", \"deux\", \"trois\"));"));
        assertTrue(output.contains("stringList = fileConfiguration.getStringList(\"string-list\");"));
        assertTrue(output.contains("fileConfiguration.addDefault(\"main.sub\", true);"));
        assertTrue(output.contains("mainSub = fileConfiguration.getBoolean(\"main.sub\");"));
    }
}
