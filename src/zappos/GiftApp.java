package zappos;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;

import com.google.gson.*;

/**
 * @author Piyush Bathwal
 * 
 */
public class GiftApp {

	static double difference = 0;
	static ArrayList<ArrayList<Integer>> gift_list = new ArrayList<ArrayList<Integer>>();

	public static void main(String[] args) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("What product do you want ? ");
		String product = in.readLine();
		System.out.println("Enter the number of gifts N");
		int N = Integer.parseInt(in.readLine());
		System.out.println("Enter the amount");
		double X = Double.parseDouble(in.readLine());

		String urlRequest = "http://api.zappos.com/Search?term=" + product
				+ "&limit=100&key=a73121520492f88dc3d33daf2103d7574f1a3166";

		Gson gson = new Gson();
		String response = getHttpResponse(urlRequest);
		Search data = gson.fromJson(response.toString(), Search.class);

		/* Uncomment this block to print console output to some file instead 
		PrintStream out = new PrintStream(new FileOutputStream("src/zappos/output.txt"));
		System.setOut(out);
		*/

		int results_size = data.results.size();
		double[] prices = new double[results_size];
		for (int i = 0; i < results_size; i++) {
			prices[i] = Double.parseDouble(data.results.get(i).getPrice().replaceAll("[, $]", ""));
		}

		// Get at least 10 results by incrementing the tolerance level. Similar to iterative deepening.
		int increment = 0;
		do {
			difference = increment * .1;
			GiftApp.products_solve(X, N, prices);
			increment++;
		} while (gift_list.size() < 10);

		System.out.println(gift_list.size() + " Gift items with difference from actual amount " + difference);
		GiftApp.printGiftList(data, prices);
	}

	/**
	 * Function that takes the resultant gift list and print the list of products suggested
	 * 
	 * @param data
	 *            the structure containing the product info
	 * @param prices
	 *            the prices of the product
	 */
	public static void printGiftList(Search data, double[] prices) {
		for (ArrayList<Integer> item : gift_list) {
			double gift_cost = 0;
			for (int i = 0; i < item.size(); i++) {
				gift_cost += prices[item.get(i)];
				System.out.println(data.results.get(item.get(i)).toString());
			}
			System.out.println("Gift Cost: " + gift_cost);
		}
	}

	/**
	 * Returns the constructs a string from the data received from making the GET request.
	 * 
	 * @param urlRequest
	 *            the url to be called
	 * @return json response string
	 * @throws IOException
	 */
	public static String getHttpResponse(String urlRequest) throws IOException {
		URL zappos = new URL(urlRequest);
		HttpURLConnection httpconnection = (HttpURLConnection) zappos.openConnection();

		if (httpconnection.getResponseCode() != 200)
			throw new IOException(httpconnection.getResponseMessage());

		BufferedReader http_in = new BufferedReader(new InputStreamReader(httpconnection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String line;

		while ((line = http_in.readLine()) != null) {
			response.append(line);
		}
		http_in.close();
		httpconnection.disconnect();
		return response.toString();
	}

	/**
	 * Function that constructs a string response from the sample data instead of making an API call every time
	 * 
	 * @return the string containing the json sample data
	 */
	public static String getSampleData() {
		String line = null;
		StringBuilder response = new StringBuilder();
		Path file = Paths.get("src/zappos/SampleDataBoots.json");

		try {
			InputStream in1 = Files.newInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in1));

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

		} catch (IOException x) {
			System.err.println(x);
		}
		return response.toString();
	}

	/**
	 * Helper function that calls the recursive function to get the gift_list results
	 * 
	 * @param sum
	 *            the max amount that can be spent
	 * @param k
	 *            the number of items to be selected
	 * @param prices
	 *            the prices of the products
	 */
	public static void products_solve(double sum, int k, double[] prices) {
		ArrayList<Integer> product_indexes = new ArrayList<Integer>();
		for (int i = 0; i < prices.length; i++)
			product_indexes.add(i);
		solve_subset_sum(sum, 0, new ArrayList<Integer>(), product_indexes, prices, 0, k);
	}

	/**
	 * Recursively solve the subset_sum and return the indexes of product that meet the criteria
	 * 
	 * @param sum
	 *            the max amount that can be spent
	 * @param current_sum
	 *            the amount already spent
	 * @param included
	 *            list of products included in the suggestion list
	 * @param excluded
	 *            list of products excluded in the suggestion list
	 * @param prices
	 *            the prices of the products
	 * @param start_index
	 *            the start position
	 * @param k
	 *            the number of items to be selected
	 */
	public static void solve_subset_sum(double sum, double current_sum, ArrayList<Integer> included,
			ArrayList<Integer> excluded, double[] prices, int start_index, int k) {
		for (int index = start_index; index < excluded.size(); index++) {
			double next_value = prices[excluded.get(index)];
			if (k < 1)
				return;
			else if (k == 1) {
				if (Math.abs(current_sum + next_value - sum) <= difference) {
					ArrayList<Integer> new_result = new ArrayList<Integer>(included);
					new_result.add(excluded.get(index));
					gift_list.add(new_result);
				}
			} else if (current_sum + next_value < sum) {
				ArrayList<Integer> next_included = new ArrayList<Integer>(included);
				next_included.add(excluded.get(index));
				ArrayList<Integer> next_not_included = new ArrayList<Integer>(excluded);
				next_not_included.remove(excluded.get(index));
				solve_subset_sum(sum, current_sum + next_value, next_included, next_not_included, prices,
						start_index++, k - 1);
			}
		}
	}
}