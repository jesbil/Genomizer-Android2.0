package se.umu.cs.pvt151.com;
/**
 * Enum with Http headers.
 *
 */
public enum RESTMethod {
	GET{public String toString(){return "GET";}},
	PUT{public String toString(){return "PUT";}},
	POST{public String toString(){return "POST";}},
	DELETE{public String toString(){return "DELETE";}};
}