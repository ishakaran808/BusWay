import java.util.*;
import java.io.*;

public class Graph_M {
	public class Vertex {
		HashMap<String, Integer> nbrs = new HashMap<>();
	}

	static HashMap<String, Vertex> vtces;

	public Graph_M() {
		vtces = new HashMap<>();
	}

	public int numVetex() {
		return this.vtces.size();
	}

	public boolean containsVertex(String vname) {
		return this.vtces.containsKey(vname);
	}

	public void addVertex(String vname) {
		Vertex vtx = new Vertex();
		vtces.put(vname, vtx);
	}

	public void removeVertex(String vname) {
		Vertex vtx = vtces.get(vname);
		ArrayList<String> keys = new ArrayList<>(vtx.nbrs.keySet());

		for (String key : keys) {
			Vertex nbrVtx = vtces.get(key);
			nbrVtx.nbrs.remove(vname);
		}

		vtces.remove(vname);
	}

	public int numEdges() {
		ArrayList<String> keys = new ArrayList<>(vtces.keySet());
		int count = 0;

		for (String key : keys) {
			Vertex vtx = vtces.get(key);
			count = count + vtx.nbrs.size();
		}

		return count / 2;
	}

	public boolean containsEdge(String vname1, String vname2) {
		Vertex vtx1 = vtces.get(vname1);
		Vertex vtx2 = vtces.get(vname2);

		if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
			return false;
		}

		return true;
	}

	public void addEdge(String vname1, String vname2, int value) {
		Vertex vtx1 = vtces.get(vname1);
		Vertex vtx2 = vtces.get(vname2);

		if (vtx1 == null || vtx2 == null || vtx1.nbrs.containsKey(vname2)) {
			return;
		}

		vtx1.nbrs.put(vname2, value);
		vtx2.nbrs.put(vname1, value);
	}

	public void removeEdge(String vname1, String vname2) {
		Vertex vtx1 = vtces.get(vname1);
		Vertex vtx2 = vtces.get(vname2);

		if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
			return;
		}

		vtx1.nbrs.remove(vname2);
		vtx2.nbrs.remove(vname1);
	}

	public void display_Stations() {
		System.out.println("\n***********************************************************************\n");
		ArrayList<String> keys = new ArrayList<>(vtces.keySet());
		int i = 1;
		for (String key : keys) {
			System.out.println(i + ". " + key);
			i++;
		}
		System.out.println("\n***********************************************************************\n");
	}

	public boolean hasPath(String vname1, String vname2, HashMap<String, Boolean> processed) {
		if (containsEdge(vname1, vname2)) {
			return true;
		}

		processed.put(vname1, true);

		Vertex vtx = vtces.get(vname1);
		ArrayList<String> nbrs = new ArrayList<>(vtx.nbrs.keySet());

		for (String nbr : nbrs) {

			if (!processed.containsKey(nbr))
				if (hasPath(nbr, vname2, processed))
					return true;
		}

		return false;
	}

	private class DijkstraPair implements Comparable<DijkstraPair> {
		String vname;
		String psf;
		int cost;

		@Override
		public int compareTo(DijkstraPair o) {
			return o.cost - this.cost;
		}
	}

	public int dijkstra(String src, String des, boolean nan) {
		int val = 0;
		ArrayList<String> ans = new ArrayList<>();
		HashMap<String, DijkstraPair> map = new HashMap<>();

		Heap<DijkstraPair> heap = new Heap<>();

		for (String key : vtces.keySet()) {
			DijkstraPair np = new DijkstraPair();
			np.vname = key;
			np.cost = Integer.MAX_VALUE;

			if (key.equals(src)) {
				np.cost = 0;
				np.psf = key;
			}

			heap.add(np);
			map.put(key, np);
		}

		while (!heap.isEmpty()) {
			DijkstraPair rp = heap.remove();

			if (rp.vname.equals(des)) {
				val = rp.cost;
				break;
			}

			map.remove(rp.vname);

			ans.add(rp.vname);

			Vertex v = vtces.get(rp.vname);
			for (String nbr : v.nbrs.keySet()) {
				if (map.containsKey(nbr)) {
					int oc = map.get(nbr).cost;
					Vertex k = vtces.get(rp.vname);
					int nc;
					if (nan)
						nc = rp.cost + 120 + 40 * k.nbrs.get(nbr);
					else
						nc = rp.cost + k.nbrs.get(nbr);

					if (nc < oc) {
						DijkstraPair gp = map.get(nbr);
						gp.psf = rp.psf + nbr;
						gp.cost = nc;

						heap.updatePriority(gp);
					}
				}
			}
		}
		return val;
	}

	private class Pair {
		String vname;
		String psf;
		int min_dis;
		int min_time;
	}

	public String Get_Minimum_Distance(String src, String dst) {
		int min = Integer.MAX_VALUE;
		// int time = 0;
		String ans = "";
		HashMap<String, Boolean> processed = new HashMap<>();
		LinkedList<Pair> stack = new LinkedList<>();

		// create a new pair
		Pair sp = new Pair();
		sp.vname = src;
		sp.psf = src + " ";
		sp.min_dis = 0;
		sp.min_time = 0;

		// put the new pair in stack
		stack.addFirst(sp);

		// while stack is not empty keep on doing the work
		while (!stack.isEmpty()) {
			// remove a pair from stack
			Pair rp = stack.removeFirst();

			if (processed.containsKey(rp.vname)) {
				continue;
			}

			// processed put
			processed.put(rp.vname, true);

			// if there exists a direct edge b/w removed pair and destination vertex
			if (rp.vname.equals(dst)) {
				int temp = rp.min_dis;
				if (temp < min) {
					ans = rp.psf;
					min = temp;
				}
				continue;
			}

			Vertex rpvtx = vtces.get(rp.vname);
			ArrayList<String> nbrs = new ArrayList<>(rpvtx.nbrs.keySet());

			for (String nbr : nbrs) {
				// process only unprocessed nbrs
				if (!processed.containsKey(nbr)) {

					// make a new pair of nbr and put in queue
					Pair np = new Pair();
					np.vname = nbr;
					np.psf = rp.psf + nbr + " ";
					np.min_dis = rp.min_dis + rpvtx.nbrs.get(nbr);
					// np.min_time = rp.min_time + 120 + 40*rpvtx.nbrs.get(nbr);
					stack.addFirst(np);
				}
			}
		}
		ans = ans + Integer.toString(min);
		return ans;
	}

	public ArrayList<String> get_Interchanges(String str) {
		ArrayList<String> arr = new ArrayList<>();
		String res[] = str.split("  ");
		arr.add(res[0]);
		int count = 0;
		for (int i = 1; i < res.length - 1; i++) {
			int index = res[i].indexOf('~');
			String s = res[i].substring(index + 1);

			if (s.length() == 2) {
				String prev = res[i - 1].substring(res[i - 1].indexOf('~') + 1);
				String next = res[i + 1].substring(res[i + 1].indexOf('~') + 1);

				if (prev.equals(next)) {
					arr.add(res[i]);
				} else {
					arr.add(res[i] + " ==> " + res[i + 1]);
					i++;
					count++;
				}
			} else {
				arr.add(res[i]);
			}
		}
		arr.add(Integer.toString(count));
		arr.add(res[res.length - 1]);
		return arr;
	}

	public static void Create_Bus_Way(Graph_M g) {
		g.addVertex("Howrah Railway Station");
		g.addVertex("Victoria Memorial");
		g.addVertex("Birla Planetarium");
		g.addVertex("Science City");
		g.addVertex("Zoological Garden, Alipore");
		g.addVertex("A.J Chandra Bose Indian Botanic Garden");
		g.addVertex("Fort Williams");
		g.addVertex("Nicco Park");
		g.addVertex("IIM Calcutta");
		g.addVertex("Eden Garden");
		g.addVertex("James Prinsep Ghat");
		g.addVertex("Elliot Park");
		g.addVertex("Lake Town");
		g.addVertex("Gariahat");
		g.addVertex("Fortis Hospital");
		g.addVertex("Bara Bazaar");
		g.addVertex("Apollo Multispeciality Hospital");
		g.addVertex("University of Calcutta");
		g.addVertex("Jadavpur University");
		g.addVertex("Dakshineswar Kali Temple");

		g.addEdge("Howrah Railway Station", "Eden Garden", 5);
		g.addEdge("Howrah Railway Station", "Bara Bazaar", 2);
		g.addEdge("Howrah Railway Station", "Science City", 9);
		g.addEdge("Howrah Railway Station", "Nicco Park", 8);
		g.addEdge("Fort Williams", "Eden Garden", 2);
		g.addEdge("Eden Garden", "Elliot Park", 2);
		g.addEdge("Elliot Park", "University of Calcutta", 3);
		g.addEdge("Howrah Railway Station", "Victoria Memorial", 7);
		g.addEdge("Howrah Railway Station", "Birla Planetarium", 6);
		g.addEdge("Howrah Railway Station", "James Prinsep Ghat", 6);
		g.addEdge("Dakshineswar Kali Temple", "Lake Town", 10);
		g.addEdge("University of Calcutta", "Science City", 8);
		g.addEdge("Victoria Memorial", "Zoological Garden, Alipore", 3);
		g.addEdge("Science City", "Apollo Multispeciality Hospital", 6);
		g.addEdge("Victoria Memorial", "IIM Calcutta", 14);
		g.addEdge("James Prinsep Ghat", "A.J Chandra Bose Indian Botanic Garden", 7);
		g.addEdge("Victoria Memorial", "Jadavpur University", 7);
		g.addEdge("Jadavpur University", "Gariaghat", 2);
		g.addEdge("Fortis Hospital", "Science City", 3);
		g.addEdge("Howrah Railway Station", "Birla Planetarium", 8);
		g.addEdge("Lake Town", "Nicco Park", 7);
	}

	public static String[] printCodelist() {
		System.out.println("List of station along with their codes:\n");
		ArrayList<String> keys = new ArrayList<>(vtces.keySet());
		int i = 1, j = 0, m = 1;
		StringTokenizer stname;
		String temp = "";
		String codes[] = new String[keys.size()];
		char c;
		for (String key : keys) {
			stname = new StringTokenizer(key);
			codes[i - 1] = "";
			j = 0;
			while (stname.hasMoreTokens()) {
				temp = stname.nextToken();
				c = temp.charAt(0);
				while (c > 47 && c < 58) {
					codes[i - 1] += c;
					j++;
					c = temp.charAt(j);
				}
				if ((c < 48 || c > 57) && c < 123)
					codes[i - 1] += c;
			}
			if (codes[i - 1].length() < 2)
				codes[i - 1] += Character.toUpperCase(temp.charAt(1));

			System.out.print(i + ". " + key + "\t");
			if (key.length() < (22 - m))
				System.out.print("\t");
			if (key.length() < (14 - m))
				System.out.print("\t");
			if (key.length() < (6 - m))
				System.out.print("\t");
			System.out.println(codes[i - 1]);
			i++;
			if (i == (int) Math.pow(10, m))
				m++;
		}
		return codes;
	}

	public static void main(String[] args) throws IOException {
		Graph_M g = new Graph_M();
		Create_Bus_Way(g);

		System.out.println("\n\t\t\t****WELCOME TO THE BUSWAY APP*****");
		BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("\t\t\t\t~~LIST OF ACTIONS~~\n\n");
			System.out.println("1. LIST ALL THE BUS STOPS IN THE MAP");
			System.out.println("2. GET SHORTEST DISTANCE  FROM A 'SOURCE' BUS STOP TO 'DESTINATION' BUS STOP");
			System.out.println("3. GET SHORTEST PATH (DISTANCE WISE) TO REACH FROM A 'SOURCE' BUS STOP TO 'DESTINATION' BUS STOP");
			System.out.println("4. EXIT THE MENU");
			System.out.print("\nENTER YOUR CHOICE FROM THE ABOVE LIST (1 to 4) : ");
			int choice = -1;
			try {
				choice = Integer.parseInt(inp.readLine());
			} catch (Exception e) {
			}
			System.out.print("\n***********************************************************\n");
			if (choice == 4) {
				System.exit(0);
			}
			switch (choice) {
				case 1:
					g.display_Stations();
					break;
				case 2:
					ArrayList<String> keys = new ArrayList<>(vtces.keySet());
					String codes[] = printCodelist();
					System.out.println(
							"\n1. TO ENTER SERIAL NO. OF STATIONS\n2. TO ENTER CODE OF STATIONS\n3. TO ENTER NAME OF STATIONS\n");
					System.out.println("ENTER YOUR CHOICE:");
					int ch = Integer.parseInt(inp.readLine());
					int j;

					String st1 = "", st2 = "";
					System.out.println("ENTER THE SOURCE AND DESTINATION STATIONS");
					if (ch == 1) {
						st1 = keys.get(Integer.parseInt(inp.readLine()) - 1);
						st2 = keys.get(Integer.parseInt(inp.readLine()) - 1);
					} else if (ch == 2) {
						String a, b;
						a = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys.size(); j++)
							if (a.equals(codes[j]))
								break;
						st1 = keys.get(j);
						b = (inp.readLine()).toUpperCase();
						for (j = 0; j < keys.size(); j++)
							if (b.equals(codes[j]))
								break;
						st2 = keys.get(j);
					} else if (ch == 3) {
						st1 = inp.readLine();
						st2 = inp.readLine();
					} else {
						System.out.println("Invalid choice");
						System.exit(0);
					}

					HashMap<String, Boolean> processed = new HashMap<>();
					if (!g.containsVertex(st1) || !g.containsVertex(st2) || !g.hasPath(st1, st2, processed))
						System.out.println("THE INPUTS ARE INVALID");
					else
						System.out.println("SHORTEST DISTANCE FROM " + st1 + " TO " + st2 + " IS "
								+ g.dijkstra(st1, st2, false) + "KM\n");
					break;

				case 3:
					System.out.println("ENTER THE SOURCE AND DESTINATION STATIONS");
					String s1 = inp.readLine();
					String s2 = inp.readLine();

					HashMap<String, Boolean> processed2 = new HashMap<>();
					if (!g.containsVertex(s1) || !g.containsVertex(s2) || !g.hasPath(s1, s2, processed2))
						System.out.println("THE INPUTS ARE INVALID");
					else {
						ArrayList<String> str = g.get_Interchanges(g.Get_Minimum_Distance(s1, s2));
						int len = str.size();
						System.out.println("SOURCE STATION : " + s1);
						System.out.println("SOURCE STATION : " + s2);
						System.out.println("DISTANCE : " + str.get(len - 1));
						System.out.println("NUMBER OF INTERCHANGES : " + str.get(len - 2));
						// System.out.println(str);
						System.out.println("~~~~~~~~~~~~~");
						System.out.println("START  ==>  " + str.get(0));
						for (int i = 1; i < len - 3; i++) {
							System.out.println(str.get(i));
						}
						System.out.print(str.get(len - 3) + "   ==>    END");
						System.out.println("\n~~~~~~~~~~~~~");
					}
					break;
				default:
					// No break is needed in the default case
					System.out.println("Please enter a valid option! ");
					System.out.println("The options you can choose are from 1 to 6. ");

			}
		}
	}
}