var webjarMapping = require('./mapping.json');

module.exports = function(url, prev, done) {
    console.log(url, prev);
    var splitted = url.split('/');
    if(splitted.length > 1 && splitted[0].startsWith("webjars-")) {
        splitted[0] = 'webjars/' + webjarMapping[splitted[0]];
        return { file: splitted.join('/') }
    }

    done({ file: url });
};