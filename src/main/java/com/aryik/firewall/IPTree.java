package com.aryik.firewall;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;

public class IPTree {
	// A wrapper class for a map of IPRange's to FirewallRule's
	// Supports insert and isAllowed

	private TreeMap<IPRange, FirewallRule> internalMap;

	public IPTree() {
		internalMap = new TreeMap<>();
	}

	// Insert a new IPRange into the tree based on ipString.
	// ipString can be a single address or a range of IP addresses.
	public void insert(String ipString, FirewallRule rule) {
		IPRange range = new IPRange(ipString);
		FirewallRule existingRule = internalMap.get(range);
		if(existingRule != null) {
			// This exact range already exists in the tree
			existingRule.addPermissions(rule);
			internalMap.put(range, existingRule);
		} else {
			// This exact range does not exist. Create it no what because we
			// have different permissions for different ranges.
			internalMap.put(range, rule);
		}
	}

	// Convenience function so we don't have to parse the string in tests or
	// in Firewall
	public boolean isAllowed(String ip, String direction, String protocol) {
		long ipLong = IPRange.ipToLong(ip);
		// Start by eliminating any ranges with range.start > ipLong
		return isAllowed(ipLong, direction, protocol);
	}

	// Search the portion of the map whose IPRange.start's are less than
	// ip + 1 in descending order. Return early if we find a rule that matches.
	private boolean isAllowed(long ip, String direction, String protocol) {
		// Discard any IPRanges with start >= ip + 1
		NavigableMap<IPRange, FirewallRule> headMap =
				internalMap.headMap(new IPRange(ip + 1, ip + 1), false);
		// Get an iterable set of the keys in descending order
		NavigableSet<IPRange> reverseKeySet = headMap.descendingKeySet();
		Iterator<IPRange> reverseIter = reverseKeySet.iterator();
		while(reverseIter.hasNext()) {
			IPRange next = reverseIter.next();
			if(next.contains(ip)) {
				// ip is within this IPRange.
				if(internalMap.get(next).isAllowed(direction, protocol)) {
					// We found an entry that allows this rule
					return true;
				} else {
					// Do nothing because there are no guarantees on the
					// ordering of the end addresses.
				}
			}
		}
		return false;
	}
}
