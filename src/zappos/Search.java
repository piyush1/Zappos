package zappos;

import java.util.ArrayList;

/**
 * Class to store the response from the making a Search API call
 * 
 * @author Piyush Bathwal
 * 
 */
public class Search {

	public int statusCode;
	public ArrayList<Product> results;

	public Search() {
		results = new ArrayList<Product>();
	}

	/**
	 * @return the results
	 */
	public ArrayList<Product> getResults() {
		return results;
	}

	/**
	 * @param results
	 *            the results to set
	 */
	public void setResults(ArrayList<Product> results) {
		this.results = results;
	}

	/**
	 * @return the statuscode
	 */
	public int getStatuscode() {
		return statusCode;
	}

	/**
	 * @param statuscode
	 *            the statuscode to set
	 */
	public void setStatuscode(int statusCode) {
		this.statusCode = statusCode;
	}

}
