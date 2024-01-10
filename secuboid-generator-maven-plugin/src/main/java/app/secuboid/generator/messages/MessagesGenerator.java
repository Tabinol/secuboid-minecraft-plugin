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
import app.secuboid.generator.common.CommonGenerator;
import lombok.Getter;
import org.apache.maven.plugin.MojoExecutionException;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class MessagesGenerator extends CommonGenerator {

    private static final char YAML_KEY_DELEMITER = '.';
    private static final String TAG_GENERATED_CONSTS = "{{generatedConsts}}";

    private final List<MessageRecord> messageRecords = new ArrayList<>();

    public MessagesGenerator(BuildContext buildContext, String source, String template, String target) {
        super(buildContext, source, template, new String[]{target});
    }

    @Override
    protected void generate(InputStream isSource, BufferedReader brJavaTemplate,
                            BufferedWriterArray bufferedWriterArray) throws MojoExecutionException, IOException {
        BufferedWriter bwJavaTarget = bufferedWriterArray.getBwTargets()[0];
        Yaml yaml = new Yaml();
        Map<String, Object> node = yaml.load(isSource);
        loopRecord("", node);

        String line;
        while ((line = brJavaTemplate.readLine()) != null) {
            if (line.contains(TAG_GENERATED_CONSTS)) {
                generateMethods(bwJavaTarget);
            } else {
                bwJavaTarget.append(line);
                bwJavaTarget.newLine();
            }
        }

    }

    private void loopRecord(String pathParent, Map<String, Object> node) throws MojoExecutionException {
        for (Map.Entry<String, Object> entry : node.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            generateRecord(pathParent, key, value);
        }
    }

    @SuppressWarnings("unchecked")
    private void generateRecord(String pathParent, String key, Object value) throws MojoExecutionException {
        String path = pathParent + key;

        if (value instanceof Map) {
            loopRecord(path + YAML_KEY_DELEMITER, (Map<String, Object>) value);
            return;
        }

        String format = Objects.toString(value);
        String methodName = toCamelCase(path);
        List<String> tags = extractTags(format);

        List<String> parameters = tags
                .stream()
                .map(s -> s.replaceAll("(\\{\\{|}})", ""))
                .map(this::toCamelCase)
                .toList();

        messageRecords.add(new MessageRecord(methodName, parameters, path, tags));
    }

    private List<String> extractTags(String format) {
        List<String> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{\\{[^}]*}}");
        Matcher matcher = pattern.matcher(format);

        while (matcher.find()) {
            tags.add(matcher.group());
        }

        return tags;
    }

    private void generateMethods(BufferedWriter bwJavaTarget) throws IOException {
        Iterator<MessageRecord> it = messageRecords.iterator();

        while (it.hasNext()) {
            MessageRecord messageRecord = it.next();
            List<String> parameters = messageRecord.getParameters();
            List<String> tags = messageRecord.getTags();

            bwJavaTarget
                    .append("    public static MessagePath ")
                    .append(messageRecord.getMethodName())
                    .append("(")
                    .append(generateParameters(parameters, true))
                    .append(") {");

            bwJavaTarget.newLine();

            bwJavaTarget
                    .append("        return MessagePath.newInstance(\"")
                    .append(messageRecord.getPath())
                    .append("\", new String[] {")
                    .append(spaceIfNotEmpty(tags))
                    .append(generateTags(tags))
                    .append(spaceIfNotEmpty(tags))
                    .append("}, new Object[] {")
                    .append(spaceIfNotEmpty(parameters))
                    .append(generateParameters(parameters, false))
                    .append(spaceIfNotEmpty(parameters))
                    .append("});");

            bwJavaTarget.newLine();

            bwJavaTarget.append("    }");

            bwJavaTarget.newLine();

            if (it.hasNext()) {
                bwJavaTarget.newLine();
            }
        }
    }

    private String generateParameters(List<String> parameters, boolean withType) {
        StringBuilder sb = new StringBuilder();

        Iterator<String> it = parameters.iterator();
        while (it.hasNext()) {
            String parameter = it.next();

            if (withType) {
                sb.append("Object ");
            }

            sb.append(parameter);

            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    private String generateTags(List<String> tags) {
        StringBuilder sb = new StringBuilder();

        Iterator<String> it = tags.iterator();
        while (it.hasNext()) {
            String parameter = it.next();
            sb
                    .append('\"')
                    .append(parameter)
                    .append('\"');

            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
