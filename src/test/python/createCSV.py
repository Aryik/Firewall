import random
import socket
import struct


# Script to generate a very large (up to 750,000 lines) sample csv.

def randomIP(minIP=1, maxIP=0xffffffff):
    # Util to generate a random IP address.
    # Taken from https://stackoverflow.com/questions/21014618/python-randomly-generated-ip-address-of-the-string
    return socket.inet_ntoa(struct.pack('>I', random.randint(minIP, maxIP)))


csv = open("outCSV.csv", "w")
protocols = ["udp", "tcp"]
directions = ["inbound", "outbound"]
portMax = 65535
ipHalf = 2147483647

# Populate simple input up to 500,000 lines
for x in xrange(random.randint(0, 500000)):
    line = directions[random.randint(0, 1)] + "," + protocols[random.randint(0, 1)] + ","
    line += str(random.randint(1, portMax)) + "," + str(randomIP()) + "\n"
    csv.write(line)

# Populate up to 250,000 ip ranges
for x in xrange(random.randint(0, 250000)):
    line = directions[random.randint(0, 1)] + "," + protocols[random.randint(0, 1)] + ","
    # line += str(random.randint(1, 30000)) + "-" + str(random.randint(30000, portMax)) + ","
    line += str(random.randint(1, portMax)) + ","
    line += str(randomIP(1, ipHalf)) + "-" + str(randomIP(ipHalf)) + "\n"
    csv.write(line)

# Populate up to 10,000 port ranges
for x in xrange(random.randint(0, 250000)):
    line = directions[random.randint(0, 1)] + "," + protocols[random.randint(0, 1)] + ","
    line += str(random.randint(1, 30000)) + "-" + str(random.randint(30000, portMax)) + ","
    line += str(randomIP()) + "\n"
    csv.write(line)

csv.close()
