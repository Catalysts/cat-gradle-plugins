var webjarMapping = require('./mapping.json');

module.exports = function (url, prev, done) {
    var splitted = url.split('/');
    if (splitted.length > 1 && splitted[0].startsWith("~webjars")) {
        var mappedPath = webjarMapping[splitted[1]];

        if (!mappedPath) {
            done({file: url});
        }
        splitted.splice(0, 2);
        return { file: mappedPath + '/' + splitted.join('/') };
    }

    done({file: url});
};