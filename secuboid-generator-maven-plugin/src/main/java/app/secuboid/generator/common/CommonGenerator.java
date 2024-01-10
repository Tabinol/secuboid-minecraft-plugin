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
package app.secuboid.generator.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.text.CaseUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public abstract class CommonGenerator {

    public static final char YAML_KEY_DELIMITER = '.';

    private final BuildContext buildContext;
    private final String source;
    private final String template;
    private final String[] targets;

    public void run() throws MojoExecutionException {
        File fileSource = new File(source);
        File fileTemplate = new File(template);

        if (!buildContext.hasDelta(fileSource) && !buildContext.hasDelta(fileTemplate)) {
            return;
        }

        if (!fileSource.isFile()) {
            throw new MojoExecutionException(
                    String.format("Wrong path for yaml file [source=%s]", source));
        }

        if (!fileTemplate.isFile()) {
            throw new MojoExecutionException(
                    String.format("Wrong path for template file [template=%s]", template));
        }

        if (targets.length == 0 || Arrays.stream(targets).allMatch(String::isEmpty)) {
            throw new MojoExecutionException("Parameter target needed");
        }

        createPaths(targets);

        try (
                InputStream isSource = new FileInputStream(source);

                FileReader frTemplate = new FileReader(fileTemplate);
                BufferedReader brTemplate = new BufferedReader(frTemplate);

                FileWriterArray fileWriterArray = new FileWriterArray(targets);
                BufferedWriterArray bufferedWriterArray = new BufferedWriterArray(fileWriterArray.getFwTargets());
        ) {
            generate(isSource, brTemplate, bufferedWriterArray);

            for (File fileTarget : fileWriterArray.getFileTargets()) {
                buildContext.refresh(fileTarget);
            }

        } catch (IOException e) {
            throw new MojoExecutionException(
                    String.format(
                            "Unable to read/write the files [source=%s, template=%s, targets=%s]", source, template,
                            Arrays.toString(targets)),
                    e);
        }
    }

    protected abstract void generate(InputStream isSource, BufferedReader brtemplate,
                                     BufferedWriterArray bufferedWriterArray) throws MojoExecutionException, IOException;

    protected String getQuoteString(String valueStr) {
        return "\"" + valueStr.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }

    protected String toConstant(String valueStr) {
        return valueStr.toUpperCase().replaceAll("[\\.|-]", "_");
    }

    protected String toCamelCase(String valueStr) {
        return CaseUtils.toCamelCase(valueStr.replace('-', YAML_KEY_DELIMITER), false, YAML_KEY_DELIMITER);
    }

    protected String spaceIfNotEmpty(List<?> list) {
        return list.isEmpty() ? "" : " ";
    }

    private void createPaths(String[] targets) throws MojoExecutionException {
        for (String target : targets) {
            createPath(target);
        }
    }

    private void createPath(String target) throws MojoExecutionException {
        Path pathTarget = Paths.get(target);

        try {
            Files.deleteIfExists(pathTarget);
            Files.createDirectories(pathTarget.getParent());
        } catch (IOException e) {
            throw new MojoExecutionException(
                    String.format("Unable to create or overwrite the file [target=%s]", target), e);
        }

    }
}
