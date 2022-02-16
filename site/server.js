const express = require('express');
const path = require('path');

const app = express()
const port = 3000

var db = require('./database')

app.set('view engine', "ejs");
app.set("views", path.join(__dirname, "views"));


app.get('/', (req, res, next) => {

  console.log('Entering app..!');
  res.render('index');

});

//route for magic page
app.get("/magic", function (req, res) {
  res.render("magic");
});


app.get('/stations', (req, res, next) => {

  var sql = "select * from stations";
  var params = [];

  db.all(sql, params, (err, rows) => {

    if (err) {
      console.log(err.message);
      res.status(400).json({ "error": err.message });
      return;
    }

    res.json({
      "message": "success",
      "data": rows
    })

  });

});

app.get('/station/:id/:active', (req, res, next) => {

  
  var sql = "SELECT * FROM stations Where stationId = ? AND Active = ?";

  //var params = [{"id": req.params.id, "active": req.params.active}];
  var params = [req.params.id, req.params.active];

  console.log(params);

  db.get(sql, params, (err, row) => {

    if (err) {
      console.log(err.message);
      res.status(400).json({ "error": err.message });
      return;
    }


    if (res){
      res.json
        (
          { "message": "success", "data": row }
        );
    } else{
      res.json({"message": "empty"});
    }

    
    


  });

});

app.use(function (req, res) {
  res.status(404);
});

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})