package org.springframework.gradle.maven;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class PublishArtifactsPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getTasks().register("publishArtifacts", new Action<Task>() {
			@Override
			public void execute(Task publishArtifacts) {
				publishArtifacts.setGroup("Publishing");
				publishArtifacts.setDescription("Publish the artifacts to the Atlassian third-party Maven repository");
				publishArtifacts.dependsOn("publishMavenJavaPublicationToAtlassianMavenRepository");
			}
		});
	}
}
