(function() {
    function create() {
        function StringBuilder() {
            if (!(this instanceof StringBuilder)) {
                return new StringBuilder();
            }

            this.values = [];
            this.length = 0;
        }

        StringBuilder.prototype.append = function(value) {
            this.values.push(value);
            this.length += value.length;
        };
        StringBuilder.prototype.valueOf = function() {
            return this.toString();
        };
        StringBuilder.prototype.toString = function() {
            return this.values.join("");
        };

        return StringBuilder;
    }

    if (typeof define === "function" && define.amd) {
        define(function() {
            return create();
        });
    } else {
        window.StringBuilder = create();
    }
}());