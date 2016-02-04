'use strict';

var path = require('path');
var commandLineArgs = require('command-line-args');

var cli = commandLineArgs([
    {name: 'project.version', type: String},
    {name: 'source.dir', type: String},
    {name: 'include.path', type: String},
    {name: 'destination.dir', type: String},
    {name: 'bundle.name', type: String},
    {name: 'no-color', type: Boolean},
    {name: 'color', type: Boolean},
    {name: 'cwd', type: String}
]);

var options = cli.parse();

var SystemJsBuilder = require('systemjs-builder');

var systemJsBuilderOptions = {
    minify: false,
    sourceMaps: false
};


function buildBundle(options) {
    var sourceDir = path.relative(process.cwd(), options['source.dir']);
    var systemJsBuilder = new SystemJsBuilder(sourceDir);

    systemJsBuilder.config({
        meta: {}
    });

    var sourcePath = path.relative(process.cwd(), options['source.dir'] + '/' + options['include.path']);
    var bundleFile = path.relative(process.cwd(), options['destination.dir'] + '/' + options['bundle.name'] + '.js');
    return systemJsBuilder
        .bundle('[./' + sourcePath + ']',
            bundleFile,
            systemJsBuilderOptions)
        .then(function (result) {
            console.log('Sucessfully created bundles for modules:');
            result.modules.forEach(function (module) {
                console.log(module);
            });
            console.log('Bundle file was saved to', bundleFile);
        });
}

buildBundle(options)
    .then(null, console.error.bind(console));