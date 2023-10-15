var request = require('request');

request.post(
    'http://localhost:9876/fraudCheck',
    {
        json: {
            "uuid": "cc8aa8ff-40ff-426f-bc71-5bb7ea644108",
            "person": {
                "name": "Fraudeusz",
                "surname": "Fraudowski",
                "dateOfBirth": "01-01-1980",
                "gender": "MALE",
                "nationalIdentificationNumber": "2345678901"
            }
        }
    },
    function (error, response, body) {
        console.log(response === undefined ? "undefined" : response.statusCode)
    }
);