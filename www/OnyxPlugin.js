function Onyx() {
}

Onyx.prototype.exec = function (params, successCallback, errorCallback) {
    cordova.exec(
        successCallback,
        errorCallback,
        "OnyxPlugin",  // Java Class
        params.action, // action
        [ // Array of arguments to pass to the Java class
            params
        ]
    );
};

Onyx = new Onyx();
module.exports = Onyx;