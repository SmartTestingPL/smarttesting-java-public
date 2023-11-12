package pl.smarttesting;

public abstract class PyramidTests {
}

@RestController
class UserController {
	private final UserRepository repo;

	UserController(UserRepository repo) {
		this.repo = repo;
	}

	@GetMapping("/user/{id}")
	User user(@PathVariable String id) {
		return repo.findUserById(id);
	}
}

@Repository
class UserRepository {
	private final DatabaseAccessor accessor;

	UserRepository(DatabaseAccessor accessor) {
		this.accessor = accessor;
	}

	User findUserById(String id) {
		return accessor.executeSql("...");
	}
}

class User {

}

class DatabaseAccessor {

	User executeSql(String s) {
		return null;
	}
}

@interface RestController {

}

@interface Repository {

}

@interface GetMapping {
	String value();
}

@interface PathVariable {

}