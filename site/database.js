var sqlite3 = require('sqlite3');
var md5 = require('md5');

const DBSOURCE = "../db/Temperature.db";

let db = new sqlite3.Database(DBSOURCE, (err) => {
    if(err){
        console.log(err.message);
        throw err;
    } else{
        console.log('Connected to the database');

    }


});

module.exports = db;