function Onyx() {
}

Onyx.prototype.exec = function (params, successCallback, errorCallback) {
    console.log("params: " + JSON.stringify(params));
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