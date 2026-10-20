/*
 * Copyright 2016-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.spring.gradle.convention

import org.gradle.api.JavaVersion;
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.PluginManager
import org.springframework.gradle.maven.SpringMavenPlugin;

/**
 * @author Rob Winch
 */
class SpringModulePlugin extends AbstractSpringJavaPlugin {

	@Override
	void additionalPlugins(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply(JavaLibraryPlugin.class)
		def coreModules = project.rootProject.findProperty("coreModules")
		if (coreModules == null || coreModules.contains(project)) {
			pluginManager.apply(SpringMavenPlugin.class);
			project.tasks.register("deployArtifacts") {
				group = 'publishing'
				description = 'Publishes the library to the Atlassian third-party Maven repository.'
				dependsOn 'publishArtifacts'
			}
		}
		pluginManager.apply("io.spring.convention.jacoco");

		project.java.sourceCompatibility = JavaVersion.VERSION_1_8;
		project.java.targetCompatibility = JavaVersion.VERSION_1_8;
	}

}
