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
package app.secuboid.generator;

import app.secuboid.generator.messages.MessagesGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

@Mojo(name = "generate-messages", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateMessagesMojo extends AbstractMojo {

    @Component
    BuildContext buildContext;

    @Parameter
    String messagesSource;

    @Parameter
    String messagesJavaTemplate;

    @Parameter
    String messagesJavaTarget;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generate: " + messagesJavaTarget);
        new MessagesGenerator(buildContext, messagesSource, messagesJavaTemplate, messagesJavaTarget).run();
    }
}
