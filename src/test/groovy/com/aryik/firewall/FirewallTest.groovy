package com.aryik.firewall

import spock.lang.Specification

/*
Spock test suite for the Firewall package and its associated classes.
Hits as many edge cases as I could in the short amount of time.
See http://spockframework.org for information.
 */

class FirewallTest extends Specification {

	def "Firewall initialization succeeds"() {
		when:
		def firewall = new Firewall("sample.csv")

		then:
		notThrown(Exception.class)
		firewall != null
		firewall.accept_packet("inbound", "udp", 64646, "65.246.168.3")
		!firewall.accept_packet("outbound", "tcp", 1, "1.1.1.1")
	}

	def "accept_packet functions correctly"() {
		when:
		def firewall = new Firewall("sample.csv")

		then:
		firewall.accept_packet("inbound", "udp", 64646, "65.246.168.3")
		!firewall.accept_packet("outbound", "tcp", 1, "1.1.1.1")
	}

	def "FirewallRule initializes correctly"() {
		when:
		def rule = new FirewallRule("inbound", "udp")
		def rule2 = new FirewallRule("outbound", "tcp")
		then:
		rule.isInbound
		rule.isUDP
		!rule.isOutbound
		!rule.isTCP

		rule.isAllowed("inbound", "udp")
		!rule.isAllowed("inbound", "tcp")
		!rule.isAllowed("outbound", "udp")
		!rule.isAllowed("outbound", "tcp")

		!rule2.isInbound
		!rule2.isUDP
		rule2.isOutbound
		rule2.isTCP
	}

	def "FirewallRule.addPermissions functions correctly"() {
		given:
		def rule = new FirewallRule("inbound", "udp")
		def rule2 = new FirewallRule("outbound", "tcp")

		when:
		rule.addPermissions(rule2)

		then:
		// the permissions from rule2 should be added to rule 2.
		// Permissions should never be removed.
		rule.isInbound
		rule.isOutbound
		rule.isTCP
		rule.isUDP

		// rule 2 should not be changed
		!rule2.isInbound
		!rule2.isUDP
		rule2.isOutbound
		rule2.isTCP
	}

	def "FirewallRule.addPermissions functions w/o  additional permissions"() {
		given:
		def rule = new FirewallRule("inbound", "tcp")
		def rule2 = new FirewallRule("outbound", "udp")
		rule2.isUDP = false
		rule2.isOutbound = false

		when:
		rule.addPermissions(rule2)

		then:
		rule.isInbound
		!rule.isUDP
		!rule.isOutbound
		rule.isTCP
	}

	def "IPTree insertion works without ranges"() {
		given:
		def tree = new IPTree()

		when:
		tree.insert("192.185.193.39", new FirewallRule("inbound", "tcp"))

		then:
		tree.isAllowed("192.185.193.39", "inbound", "tcp")
		!tree.isAllowed("192.185.193.39", "inbound", "udp")
		!tree.isAllowed("192.185.193.39", "outbound", "tcp")
		!tree.isAllowed("192.185.193.39", "outbound", "udp")
	}

	def "IPTree insertion works with ranges"() {
		given:
		def tree = new IPTree()

		when:
		tree.insert("1.1.1.1-255.255.255.255", new FirewallRule("outbound", "udp"))

		then:
		tree.isAllowed("72.198.150.217", "outbound", "udp")
		!tree.isAllowed("125.9.231.5", "inbound", "udp")
	}

	def "IPTree insertion works with overlapping ranges"() {
		given:
		def tree = new IPTree()

		when:
		tree.insert("192.168.1.1-192.168.2.5", new FirewallRule("inbound", "tcp"))
		tree.insert("192.167.1.1-192.168.1.1", new FirewallRule("outbound", "udp"))
		tree.insert("192.168.2.1", new FirewallRule("outbound", "tcp"))

		then:
		tree.isAllowed("192.168.1.1", "outbound", "udp")
		tree.isAllowed("192.168.1.1", "inbound", "tcp")
		!tree.isAllowed("192.168.2.1", "outbound", "udp")
		tree.isAllowed("192.168.2.1.", "outbound", "tcp")
	}

	def "IPRange init no range"() {
		when:
		def range = new IPRange("192.168.1.1")

		then:
		range.start == 3232235777
		range.end == 3232235777
	}

	def "IPRange init with a range"() {
		when:
		def range = new IPRange("143.128.1.1-225.1.1.1")

		then:
		range.start == 2407530753
		range.end == 3774939393
	}
}
