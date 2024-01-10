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
import app.secuboid.generator.common.CommonGenerator;
import lombok.Getter;
import org.apache.maven.plugin.MojoExecutionException;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Getter
public class FlagsGenerator extends CommonGenerator {

    private static final String TAG_GENERATED_FLAGS = "{{generatedFlags}}";

    private final List<String> languages;

    private final List<FlagRecord> flagRecords = new ArrayList<>();

    private FlagsGenerator(BuildContext buildContext, String source, String template, String[] targets, List<String> languages) {
        super(buildContext, source, template, targets);
        this.languages = languages;
    }

    public static FlagsGenerator newFlagsGenerator(BuildContext buildContext, String source, String javaTemplate,
                                                   String target, Map<String, String> languageToTarget) {
        List<String> languages = new ArrayList<>();
        List<String> targetList = new ArrayList<>();
        targetList.add(target);

        languageToTarget.forEach((k, v) -> {
            languages.add(k);
            targetList.add(v);
        });

        String[] targets = targetList.toArray(new String[0]);

        return new FlagsGenerator(buildContext, source, javaTemplate, targets, languages);
    }

    @Override
    protected void generate(InputStream isSource, BufferedReader brJavaTemplate,
                            BufferedWriterArray bufferedWriterArray) throws MojoExecutionException, IOException {
        BufferedWriter[] bwTargets = bufferedWriterArray.getBwTargets();
        BufferedWriter bwJavaTarget = bwTargets[0];
        Yaml yaml = new Yaml();
        Map<String, Object> node = yaml.load(isSource);
        loopRecord(node);

        String line;
        while ((line = brJavaTemplate.readLine()) != null) {
            if (line.contains(TAG_GENERATED_FLAGS)) {
                generateFlags(bwJavaTarget);
            } else {
                bwJavaTarget.append(line);
                bwJavaTarget.newLine();
            }
        }

        for (int i = 0; i < languages.size(); i++) {
            String language = languages.get(0);
            BufferedWriter bwLangTarget = bwTargets[i + 1];
            generateLanguageFlags(language, bwLangTarget);
        }
    }

    private void loopRecord(Map<String, Object> node) throws MojoExecutionException {
        for (Map.Entry<String, Object> entry : node.entrySet()) {
            String flagName = entry.getKey();
            Object value = entry.getValue();
            generateRecord(flagName, value);
        }
    }

    @SuppressWarnings("unchecked")
    private void generateRecord(String flagName, Object value) throws MojoExecutionException {
        if (!(value instanceof Map)) {
            throw new MojoExecutionException(String.format("The root key should be a map [flagName=%s]", flagName));
        }

        Map<String, Object> flagNode = (Map<String, Object>) value;

        Map<String, String> langToDescription = ((Map<String, Object>) flagNode.get("description"))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().toString()));

        boolean needSource = getOrDefault(flagNode, "need-source", true);
        boolean needTarget = getOrDefault(flagNode, "need-target", false);
        boolean needMetadata = getOrDefault(flagNode, "need-metadata", false);
        boolean isHidden = getOrDefault(flagNode, "hidden", false);

        FlagRecord flagRecord = new FlagRecord(flagName, langToDescription, needSource, needTarget, needMetadata,
                isHidden);
        flagRecords.add(flagRecord);
    }

    private void generateFlags(BufferedWriter bwJavaTarget) throws IOException {
        boolean isFirst = true;
        for (FlagRecord flagRecord : flagRecords) {
            if (!isFirst) {
                bwJavaTarget.newLine();
            }
            generateFlag(bwJavaTarget, flagRecord);
            isFirst = false;
        }
    }

    private void generateFlag(BufferedWriter bwJavaTarget, FlagRecord flagRecord) throws IOException {
        String nameUpper = toConstant("FLAG_" + flagRecord.getName());

        bwJavaTarget
                .append("    public static final FlagType ")
                .append(nameUpper)
                .append(" = FlagType.newInstance(\"")
                .append(flagRecord.getName())
                .append("\", \"")
                .append(flagRecord.getLangToDescription().get("en"))
                .append("\", ")
                .append(Boolean.toString(flagRecord.isNeedSource()))
                .append(", ")
                .append(Boolean.toString(flagRecord.isNeedTarget()))
                .append(", ")
                .append(Boolean.toString(flagRecord.isNeedMetadata()))
                .append(", ")
                .append(Boolean.toString(flagRecord.isHidden()))
                .append(");");

        bwJavaTarget.newLine();
    }

    private void generateLanguageFlags(String language, BufferedWriter bwLangTarget) throws IOException {
        for (FlagRecord flagRecord : flagRecords) {
            String name = flagRecord.getName();
            String description = flagRecord.getLangToDescription().get(language);
            if (description != null) {
                generateLanguageFlag(bwLangTarget, name, description);
            }
        }
    }

    private void generateLanguageFlag(BufferedWriter bwLangTarget, String name, String description) throws IOException {
        bwLangTarget
                .append(name)
                .append(": ")
                .append(description);

        bwLangTarget.newLine();
    }

    public boolean getOrDefault(Map<String, Object> flagNode, String key, boolean defaultValue) {
        Object valueObj = flagNode.get(key);

        if (valueObj instanceof Boolean valueBool) {
            return valueBool;
        }

        return defaultValue;
    }
}
