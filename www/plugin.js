var exec = require('cordova/exec');

var PLUGIN_NAME = 'PaxPrinter';

var PaxPrinter = {
    echo: function (phrase, cb) {
        exec(cb, null, PLUGIN_NAME, 'echo', [phrase]);
    },
    getDate: function (cb) {
        exec(cb, null, PLUGIN_NAME, 'getDate', []);
    },
    print: function (text) {
        return new Promise(function (resolve, reject) {
            //cordova.exec(resolve, reject, "Printer", "getPrinterVersion", []);
            exec(resolve, reject, PLUGIN_NAME, 'print', [text]);
        });
    },
    printImage: function (text) {
        return new Promise(function (resolve, reject) {
            //cordova.exec(resolve, reject, "Printer", "getPrinterVersion", []);
            exec(resolve, reject, PLUGIN_NAME, 'printImage', [text]);
        });
    },
    paxPrint: function (text) {
        return new Promise(function (resolve, reject) {
            //cordova.exec(resolve, reject, "Printer", "getPrinterVersion", []);
            exec(resolve, reject, PLUGIN_NAME, 'paxPrint', [text]);
        });
    },
    getSN: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, 'getSN', []);
        });
    },
    readCard: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, 'readCard', []);
        });
    },
    cardDetails: function () {
      return new Promise(function (resolve, reject) {
          exec(resolve, reject, PLUGIN_NAME, 'cardDetails', []);
      });
  },
  readNfc: function () {
    return new Promise(function (resolve, reject) {
        exec(resolve, reject, PLUGIN_NAME, 'readNfc', []);
    });
},
    scanQR: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, 'scanQR', []);
        });
    },
    mbsCardRead: function () {
      return new Promise(function (resolve, reject) {
          exec(resolve, reject, PLUGIN_NAME, 'mbsCardRead', []);
      });
  }
};

module.exports = PaxPrinter;
