package cc.catalysts.gradle.sass.task

import cc.catalysts.gradle.GradleHelper
import cc.catalysts.gradle.sass.SassExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.util.PatternSet

class ExtractWebjars extends Copy {
    public ExtractWebjars() {
        this.group = 'Cat-Boot SASS'
        this.description = 'Extracts all \'sass\', \'scss\' and \'css\' files from your webjars'

        PatternSet patternSet = new PatternSet()
        patternSet.include('**/*.sass', '**/*.scss', '**/*.css')

        project.afterEvaluate({
            from project.getConfigurations().findAll {
                GradleHelper.canBeResolved(it)
            }.collect({ configuration ->
                return configuration.files({
                    return it.group?.startsWith('org.webjars')
                })
            }).flatten().collect({
                return project.zipTree(it).matching(patternSet)
            });

            into(new File(SassExtension.get(project).nodeModulesDir, 'extracted'))
        })
    }
}
