package org.springframework.gradle.maven;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.credentials.PasswordCredentials;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;

public class SpringMavenPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply(MavenPublishPlugin.class);
		pluginManager.apply(SpringSigningPlugin.class);
		pluginManager.apply(MavenPublishingConventionsPlugin.class);
		pluginManager.apply(PublishAllJavaComponentsPlugin.class);
		pluginManager.apply(PublishLocalPlugin.class);
		pluginManager.apply(PublishArtifactsPlugin.class);
		PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
		publishing.getRepositories().maven(repository -> {
			repository.setName("atlassianMaven");
			repository.setUrl("https://packages.atlassian.com/maven/3rdparty");
			repository.credentials(PasswordCredentials.class);
		});
		project.getTasks().withType(PublishToMavenRepository.class).configureEach(task -> {
			task.doFirst(ignored -> {
				if ("atlassianMaven".equals(task.getRepository().getName())
						&& !project.getVersion().toString().matches(".+-atlassian-[0-9]+(-SNAPSHOT)?")) {
					throw new GradleException("Atlassian publication requires an -atlassian-N version suffix");
				}
			});
		});
	}
}
