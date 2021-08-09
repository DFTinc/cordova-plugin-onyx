class Onyx {
  static async exec(params) {
    return new Promise((resolve, reject) => {
      cordova.exec(
        resolve,
        reject,
        "OnyxPlugin",  // Java Class
        params.action, // action
        [ // Array of arguments to pass to the Java class
          params
        ]
      );
    });
  }
}

module.exports = Onyx;
