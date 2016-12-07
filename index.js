var express = require('express');

var bodyParser = require('body-parser');

var app = express()

var fishvar = []
var lengthvar = []
var weightvar = []
var lakevar = []

    app.set("port", 4322);

app.use(bodyParser.urlencoded({   // support encoded bodies
	    extended: true
		}));
app.use(bodyParser.json());  // support json encoded bodies

// ----------------------------------------
// GET
// ----------------------------------------
app.get('/userData', function(req, res) {

	// Prepare output in JSON format
	var dataToReturn = {
	    fish : fishvar,
	    length : lengthvar,
	    weight : weightvar,
	    lake : lakevar,
	};
    
	console.log('/userData GET URI accessed');
	res.send(JSON.stringify(dataToReturn));
    });

// ----------------------------------------
// POST
// ----------------------------------------
var countUserDataPOST = 0
    app.post('/userData', function (req, res) {

	    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
	    // 400
	    if (!req.body) return res.sendStatus(400)

			       countUserDataPOST++;

	    var fish = req.body.fish;
	    fishvar = fish;
	    var length = req.body.length;
	    lengthvar = length;
	    var weight = req.body.weight;
	    weightvar = weight;
	    var lake = req.body.lake;
	    lakevar = lake;

	    console.log('/userData POST, count=', countUserDataPOST, ', jsonData=', req.body);
	    console.log('   fish=', fish);

	    res.json(req.body);
	})

    // ----------------------------------------
    // PUT
    // ----------------------------------------
var countUserDataPUT = 0
    app.put('/userData', function (req, res) {

	    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
	    // 400
	    if (!req.body) return res.sendStatus(400)

			       countUserDataPUT++;

	    var fish = req.body.fish;
	       

	    console.log('/userData PUT, count=', countUserDataPUT, ', jsonData=', req.body);
	    console.log('   fish=', fish)
	    var jsonResponse = { 
		id: 'fish', 
		status: 'updated'
	    };
	    res.json(jsonResponse);
	})

    // ----------------------------------------
    // DELETE
    // ----------------------------------------
var countUserDataDELETE = 0
    app.delete('/userData', function (req, res) {

	    // If for some reason, the JSON isn't parsed, return a HTTP ERROR
	    // 400
	    if (!req.body) return res.sendStatus(400)

			       countUserDataDELETE++;

	    var fish = req.body.fish;

	    console.log('/userData DELETE, count=', countUserDataDELETE, ', jsonData=', req.body);
	    console.log('   deleting fish=', fish, ' from the server.');

	    var jsonResponse = { 
		id: '321', 
		status: 'deleted'
	    };
	    res.json(jsonResponse);
	})

    // 
    // respond with basic webpage HTML when a GET request is made to the homepage path /
    // 
    app.get('/', function(req, res) {
	    res.send('<HTML><HEAD></HEAD><BODY><H1>hello world</H1></BODY></HTML>');
	        
	});

// ERROR Conditions
// ----------------
// page not found - 404
app.use(function(req, res, next) {
	res.status(404).send('Sorry cant find that!');
    });

// page not found - 404
app.use(function(err, req, res, next) {
	console.error(err.stack);
	res.status(500).send('Internal Server Error message - very strange request came in and we do not know how to handle it!!!');
    });

app.listen(app.get("port"), function () {
	console.log('CS4531 Node Example: Node app listening on port: ', app.get("port"));
    });