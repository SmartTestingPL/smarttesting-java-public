import org.springframework.cloud.contract.spec.Contract

Contract.make {
	request {
		method POST()
		url "/fraudCheck"
		headers {
			contentType applicationJson()
		}
		body('''
				{
				  "uuid" : "cc8aa8ff-40ff-426f-bc71-5bb7ea644108",
				  "person" : {
					"name" : "Fraudeusz",
					"surname" : "Fraudowski",
					"dateOfBirth" : "01-01-1980",
					"gender" : "MALE",
					"nationalIdentificationNumber" : "2345678901"
				  }
				}
				''')
	}
	response {
		status UNAUTHORIZED()
	}

}