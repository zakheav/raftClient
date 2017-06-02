# raft client
If you want to know what is raft, take a look at **readme.md** in raft component
## how to use raft client?
raft client is used to send request to raft server cluster.

- write the conf.xml in conf folder
- implements Callback interface, which is used to process database query result
 
		public class MyCallback implements Callback {
		public void callback(List<Map<String, Object>> results) {
				for(Map<String, Object> row : results) {
					int index = (Integer)row.get("logIndex");
					String command = (String)row.get("command");
					System.out.println(index + " " + command);
				}
			}
		}	

	the parameter results contains rows of dataset.

- write the conf.xml in conf folder
- call the send_command() function

		String cmd = "select * from log where term = 3";
		new Client().send_command(cmd, true, new MyCallback());// true mean this command is query command
