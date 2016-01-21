var gulp = require('gulp');
gulp.util = require('gulp-util');

var SystemJsBuilder = require('systemjs-builder');

var systemJsBuilderOptions = {
    baseURL: '../wpc-ui/src/main/resources/static',
    options: {
        minify: false,
        sourceMaps: true
    }
};

gulp.task('default', function() {
    var systemJsBuilder = new SystemJsBuilder(systemJsBuilderOptions.baseURL);

    systemJsBuilder.config({
        meta: {}
    });

    return systemJsBuilder
        .bundle('../wpc-ui/src/main/resources/static/js/**/*.js', '../wpc-ui/build/wpc-ui-bundle.js', systemJsBuilderOptions)
        .then(function (result) {
            gulp.util.log('Sucessfully created bundles for modules', result.modules);
        });
});