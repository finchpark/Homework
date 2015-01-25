package com.mobigen.ojt.mapreduce.examples.drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordTFIDFHomeWork extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		int result = ToolRunner.run(new Configuration(), new WordTFIDFHomeWork(), args);
		System.exit(result);
	}

	public int run(String[] arg0) throws Exception {
		
		return 0;
	}
}
