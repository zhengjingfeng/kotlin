/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// ~/.jsvu/v8 js/js.engines/src/org/jetbrains/kotlin/js/engine/repl.js
// TODO create polyfills for node.js. What for?

(function repl() {
    var { saveState, restoreState } = (function () {
        let state = null;
        function saveState() {
            state = new Map();
            for (var k in this) {
                state.set(k, this[k]);
            }
        }
        function restoreState() {
            for (var k in this) {
                if (state.get(k) !== this[k]) {
                    this[k] = void 0;
                }
            }
            state = null;
        }

        return { saveState, restoreState }
    })()
    // print('>>> STARTED');
    // noinspection InfiniteLoopJS
    while (true) {
        let code = readline().replace(/\\n/g, '\n');

        try {
            print(eval(code));
        }
        catch(e) {
            printErr(e.stack);
            // print('\nCODE:\n' + code);
        }

        print('<END>');
    }
})();