package com.aryik.firewall;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Firewall {

	private HashMap<Integer, IPTree> portIPMap;

	// Create the firewall object from the .csv at 'path'
	// Initializes the hash map and then inserts an IPTree object at every port
	// That has an associated rule.
	// Given more time, I would create a port range class and include the port range
	// object inside either the IPRange object or the FirewallRule object.
	// This would eliminate the wasted memory from mapping every port.
	public Firewall(String path) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		portIPMap = new HashMap<>();
		String line;
		// Iterate through the file creating IPTree's and FirewallRule's and
		// associating the trees with ports in the hashmap
		while((line = reader.readLine()) != null) {
			// splitLine[0] = direction, [1] = protocol, [2] = port, [3] = ip
			String[] splitLine = line.split(",");
			if(splitLine[2].contains("-")) {
				// The port is a range. Initialize an IPTree at each port.
				// If I had more time, I would change this. This is memory
				// intensive and slows down initialization significantly
				// when large port ranges are encountered.
				String[] range = splitLine[2].split("-");
				Integer start = Integer.parseInt(range[0]);
				Integer end = Integer.parseInt(range[1]);
				for(int i = start; i <= end; ++i) {
					insertPort(i, splitLine[0], splitLine[1], splitLine[3]);
				}
			} else {
				// The port is not a range. Just insert an IPTree at port.
				Integer port = Integer.parseInt(splitLine[2]);
				insertPort(port, splitLine[0], splitLine[1], splitLine[3]);
			}
		}
		reader.close();
	}

	// Check if the packet is allowed by the rules that the firewall was
	// initialized with.
	public boolean accept_packet(String direction, String protocol,
			Integer port, String ip) {
		IPTree tree = portIPMap.get(port);
		if(tree == null) {
			return false;
		} else {
			return tree.isAllowed(ip, direction, protocol);
		}
	}

	// Helper function to insert a port into the IPTree.
	private void insertPort(Integer port, String direction, String protocol,
			String ip) {
		IPTree tree = portIPMap.get(port);
		if(tree == null) {
			tree = new IPTree();
		}
		tree.insert(ip, new FirewallRule(direction, protocol));
		portIPMap.put(port, tree);
	}

		/*
	A sample main method to demonstrate usage.

	public static void main(String[] args) {
		try {
			System.out.println("beginning creation");
			// Requires outCSV.csv to be at the base directory of the project
			Firewall firewall = new Firewall("outCSV.csv");
			System.out.println("Done creating.");
			System.out.println(firewall.accept_packet("inbound", "udp", 1000, "192.168.2.2"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
*/
}
