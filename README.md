#TradingPost [WIP]
TradingPost is a plugin created by Chris Bitler for RITCraft.
Our goal with this plugin was to try to revolutionize how people do their trading between players on minecraft servers.
The plugin has two parts: The plugin itself, where users can register for the trading post, add items to the trading post, and claim their items, and also a web interface portion, where people can search and buy the items that are currently for sale.

##Compilation:
All you need to do to compile this plugin is to have maven installed and run mvn package, assuming you don't have a firewall blocking any of the dependencies

Alternatively, you can download it from our jenkins: http://vwserver.student.rit.edu:8080/job/TradingPost/lastSuccessfulBuild/

##Configuration:
There are 6 configuration values for this plugin:
mysql_username,mysql_password,mysql_host,mysql_db - All pretty self explanatory - the username, password, ip, and the specific db you want the plugin to use.
website_ip, website_port - The ip and port to run the website portion of the plugin on.

##Credits:
Spigot project for making plugins like this possible
Sparkjava for the microwebserver that makes this plugin possible
