package com.aryik.firewall;

public class FirewallRule {
	// A class to represent the allowable directions/protocols at a given
	// IP/port combination.

	// Don't bother with getters and setters for now.
	boolean isInbound;
	boolean isOutbound;
	boolean isTCP;
	boolean isUDP;

	public FirewallRule(String direction, String protocol) {
		// Assumes well-formed, valid input
		// Default value of boolean is false so we only need to set one value
		// for direction/protocol
		if(direction.equalsIgnoreCase("outbound"))
			this.isOutbound = true;
		else
			this.isInbound = true;

		if(protocol.equalsIgnoreCase("tcp"))
			this.isTCP = true;
		else
			this.isUDP = true;
	}

	// Take the permissions from newRule and add them to the existing
	// permissions. Never remove permissions or change newRule.
	public void addPermissions(FirewallRule newRule) {
		if(newRule.isTCP)
			this.isTCP = true;
		if(newRule.isUDP)
			this.isUDP = true;
		if(newRule.isInbound)
			this.isInbound = true;
		if(newRule.isOutbound)
			this.isOutbound = true;
	}

	// Convenience function to determine if a particular direction and protocol
	// is allowed
	public boolean isAllowed(String direction, String protocol) {
		boolean allowDirection, allowProtocol;
		if(direction.equals("outbound")) {
			allowDirection = isOutbound;
		} else {
			allowDirection = isInbound;
		}
		if(protocol.equals("tcp")) {
			allowProtocol = isTCP;
		} else {
			allowProtocol = isUDP;
		}
		return allowDirection && allowProtocol;
	}
}
