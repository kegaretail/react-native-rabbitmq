try {
    var fs = require('fs');
    var glob = require('glob');
    var PACKAGE_JSON = process.cwd() + '/package.json';
    var package = JSON.parse(fs.readFileSync(PACKAGE_JSON));
    var APP_NAME = package.name;
    var PACKAGE_GRADLE = process.cwd() + '/node_modules/react-native-rabbitmq/android/build.gradle'
    var VERSION = checkVersion();

    if (VERSION < 0.40) {
        glob('**/RCTReactNativeRabbitMq.h',{}, function(err, files) {
            if(Array.isArray(files)) {
                var target = process.cwd() + '/' + files[0];
                console.log('\033[92mPatching .. \033[97m' + target);
                var data = fs.readFileSync(target);
                fs.writeFileSync(target, String(data).replace(/\/\/#define OLDER_IMPORT/, '#define OLDER_IMPORT'));
                console.log('done.')
            }
        })
    }

    function checkVersion() {
        console.log('react-native-rabbitmq checking app version ..');
        return parseFloat(/\d\.\d+(?=\.)/.exec(package.dependencies['react-native']));
    }

} catch(err) {
  console.log(
    '\033[95mreact-native-rabbitmq\033[97m link \033[91mFAILED \033[97m\nCould not automatically link package :'+
    err.stack)
}
