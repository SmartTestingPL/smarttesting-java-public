package pl.smarttesting.bik.score.cost;

public interface RestClient {

	<T> T get(String url, Class<T> returnType);
}
