module.exports = {
    /*
    * @enum {string}
    * */
    ACTION: {
        MATCH: "match",
        CAPTURE: "capture"
    },
    /*
    * @enum {string}
    * */
    IMAGE_ROTATION: {
        ROTATE_NONE: 0,
        ROTATE_90_COUNTER_CLOCKWISE: 90,
        ROTATE_180: 180,
        ROTATE_90_CLOCKWISE: 270
    },
    /*
    * @enum {string}
    * */
    LAYOUT_PREFERENCE: {
        UPPER_THIRD: "UPPER_THIRD",
        FULL: "FULL"
    },
    /*
   * @enum {string}
   * */
    RETICLE_ORIENTATION: {
        LEFT: "LEFT",
        RIGHT: "RIGHT"
    },
    /*
    * @enum {number}
    * */
    FINGER_DETECT_MODE: {
        DEAD_FINGER: 0,
        LIVE_FINGER: 1
    }
};