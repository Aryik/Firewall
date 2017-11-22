# Firewall
This project was done for the take-home coding assignment for Illumio.

The Firewall class has two public methods:
* `Firewall(String path)` - Constructor
  *  `path` is the path to a CSV file of firewall rules. 
  *  Rules are of the format `direction, protocol, port, ip address` 
     * `direction` can be either "inbound" or "outbound" 
     * `protocol` can be  "udp" or "tcp" 
     * `port` can be a single integer in the range [1, 65535] or a a port 
     range, containing two integers in the range [1, 65535] separated by a 
     dash (no spaces).
     * `ip address`: IPv4 address in dotted notation, consisting of 4 octets,
     each an integer in the range [0, 255], separated by periods or an IP
     range containing two IPv4 addresses, separated by a dash (no spaces)
  * The constructor creates a HashMap of ports and maps the ports to IPTree
  objects. The IPTree objects are balanced binary search trees that represent
  the set of allowable ip addresses for a given port and the allowable 
  directions/ports for that port/ip combination.
* `boolean accept_packet(String direction, String protocol, int port, String ip)`:
determines if a given packet should be accepted or not.
   * Checks if there's an IPTree associated with the given port. If so, 
   searches the tree to determine if the IP address is allowable. If it is,
   checks if the given protocol/direction are acceptable.
   
## Notes

 The current data structure supports very fast reads and relatively fast 
 initialization in most cases. It uses creates an IPRange object for each rule
 and then places it in an IPTree. The IPTree is a TreeMap (Red-Black tree) 
 sorted on the start of the range, represented as a long integer. 
 
 To check if a rule r1 is allowed, we first obtain the appropriate IPTree from 
 the hash of r1.port. We then discard all IPRanges r in the tree where 
 r.start > r1.range. We iterate through this minimized set in reverse order,
 searching for an IPRange that includes r1. If we find one, we check if the 
 associated FirewallRule allows the protocol/direction of r1.
 
 The biggest weak point of this data structure is its handling of port ranges.
 It creates an IPTree for each port which is wasteful of both memory and time.
 Given more time to improve, I would instead store the ports within the IPRange
 or FirewallRule. I would probably do this by maintaining a similar tree 
 structure for the port ranges at each IPRange. This would eliminate the 
 expensive enumeration of ports and still allow very efficient lookups.

 Currently, Firewall can fail due to an OutOfMemoryError if many large port
 ranges are specified. Eliminating the enumeration of ports would solve this
 problem.

## Building and Testing

To test the project, enter `mvn clean test` on the command line. To package the
project into a .jar, enter `mvn package` on the command line. The built jar 
file will be located at `target/firewall-1.0.0.jar`.

I also tested the project on a very large input csv that I generated using 
src/test/python/createCSV.python and the main method of the Firewall class.
This testing allowed me to determine that the handling of port ranges was 
suboptimal.

## Dependencies

The project is built using [Maven](https://maven.apache.org) with the standard
Maven project structure (src/main/* and src/test/*). The tests are written
using [Spock](http://spockframework.org), a testing/specification framework
with a highly expressive syntax that is compatible with most IDEs, build tools,
and CI servers due to its jUnit runner. You do not need to do anything to 
obtain spock as Maven handles dependency management.

You must have Maven installed to test or package the project. If you do not
have Maven, you can still compile the source class using `javac` but the 
test file is not guaranteed to compile because the dependencies (groovy and 
spock) may not be installed on your machine. Maven is open source and readily
available on all operating systems. To install Maven, either use your favorite
package manager (homebrew, apt-get, etc.), or download it in one of many 
formats [here](https://Maven.apache.org/download.cgi).

## Other Stuff

First of all, thanks for taking the time to look through this! I am excited to
get some feedback on my design. Given the choice, I would love to work on the
platform team with the policy team in a close second.