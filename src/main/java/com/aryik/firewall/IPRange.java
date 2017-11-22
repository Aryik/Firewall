package com.aryik.firewall;

// Class to represent a range of IP Addresses

public class IPRange implements Comparable<IPRange> {

	private long start;
	private long end;

	public IPRange(long start, long end) {
		// Assumes well-formed range
		this.start = start;
		this.end = end;
	}

	public IPRange(String ip) {
		if(ip.contains("-")) {
			// this is a range and not a single IP
			String[] range = ip.split("-");
			this.start = ipToLong(range[0]);
			this.end = ipToLong(range[1]);
		} else {
			this.start = ipToLong(ip);
			this.end = this.start;
		}
	}

	// Util to convert ip address to long integer.
	// Taken from https://www.mkyong.com/java/java-convert-ip-address-to-decimal-number/
	// Static so we don't need to read in a csv to test the function
	protected static long ipToLong(String ipAddress) {

		long result = 0;
		String[] ipAddressInArray = ipAddress.split("\\.");

		for(int i = 3; i >= 0; i--) {
			long ip = Long.parseLong(ipAddressInArray[3 - i]);
			//left shifting 24,16,8,0 and bitwise OR

			//1. 192 << 24
			//1. 168 << 16
			//1. 1   << 8
			//1. 2   << 0
			result |= ip << (i * 8);
		}
		return result;
	}

	public int compareTo(IPRange T) {
		if(this.start == T.start && this.end == T.end) {
			return 0;
		} else if(this.start <= T.start) {
			return -1;
		} else {
			return 1;
		}
	}

	// See if the specified ip is contained in this IPRange
	public boolean contains(long ip) {
		return start <= ip && end >= ip;
	}
}
